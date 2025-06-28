package org.example.dao;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import jakarta.persistence.NoResultException;
import org.example.model.*;
import org.example.dto.SendDTO;
import org.example.service.EmailService;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;


import org.example.model.Send;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.SolidBorder;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class SendDAO {
//    public List<SendDTO> findByPhone(String keyword) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            String hql = "SELECT new org.example.dto.SendDTO(s.sendId, c1.phoneNumber, c2.phoneNumber, s.amount, s.date, s.reason, s.currency) " +
//                    "FROM Send s " +
//                    "JOIN s.sender c1 " +
//                    "JOIN s.receiver c2";
//
//            if (keyword == null || keyword.trim().isEmpty()) {
//                return session.createQuery(hql + " order by s.sendId", SendDTO.class).getResultList();
//            }
//
//            String kw = "%" + keyword + "%";
//
//            return session.createQuery(hql + " where c1.phoneNumber like :kw or c2.phoneNumber like :kw order by s.sendId", SendDTO.class)
//                    .setParameter("kw", kw)
//                    .getResultList();
//        }
//    }

    public List<SendDTO> findByPhone(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder(
                    "SELECT new org.example.dto.SendDTO(s.sendId, c1.phoneNumber, c2.phoneNumber, s.amount, s.date, s.reason, s.currency) " +
                            "FROM Send s JOIN s.sender c1 JOIN s.receiver c2 WHERE 1=1"
            );

            if (keyword != null && !keyword.trim().isEmpty()) {
                hql.append(" AND (c1.phoneNumber LIKE :kw OR c2.phoneNumber LIKE :kw)");
            }

            if (startDate != null) {
                hql.append(" AND s.date >= :startDate");
            }

            if (endDate != null) {
                hql.append(" AND s.date <= :endDate");
            }

            hql.append(" ORDER BY s.date DESC");

            var query = session.createQuery(hql.toString(), SendDTO.class);

            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("kw", "%" + keyword + "%");
            }

            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }

            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }

            return query.getResultList();
        }
    }


    public void create(Send send) {
        int mgaAmount = 0;
        int feeAmount = 0;
        int mgaFeeAmount = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            int amount = send.getAmount();

            // 1. Trouver le frais de transfert
            Integer resultFee = null;
            try {
                resultFee = session.createQuery("""
                SELECT t.feeAmount FROM TransferFee t
                WHERE t.minAmount <= :amount AND t.maxAmount >= :amount AND t.currency = :currency
            """, Integer.class)
                        .setParameter("amount", amount)
                        .setParameter("currency", send.getCurrency())
                        .getSingleResult();
            } catch (NoResultException e) {
                throw new RuntimeException("Aucun frais de transfert trouvé pour ce montant et cette devise.");
            }
            feeAmount = resultFee;

            // 3. Vérifier que le solde de l'envoyeur est suffisant pour l'opération
            if (send.getSender().getBalance() < amount + feeAmount) {
                throw new RuntimeException("Le solde de l'envoyeur est insuffisant pour effectuer l'opération");
            }


            // 2. Trouver le taux de change
            ExchangeRate exchangeRate;
            try {
                exchangeRate = session.createQuery("""
                FROM ExchangeRate WHERE sourceCurrency = :currency
            """, ExchangeRate.class)
                        .setParameter("currency", send.getCurrency())
                        .getSingleResult();
            } catch (NoResultException e) {
                throw new RuntimeException("Aucun taux de change trouvé pour cette unité monétaire.");
            }

            // 3. Calculs MGA
            mgaAmount = amount * exchangeRate.getRateToMGA() / exchangeRate.getBaseAmount();
            mgaFeeAmount = feeAmount * exchangeRate.getRateToMGA() / exchangeRate.getBaseAmount();

            // 4. Transaction (uniquement les écritures)
            Transaction tx = session.beginTransaction();
            try {
                updateSender(session, send.getSender(), amount + feeAmount);
                updateReceiver(session, send.getReceiver(), mgaAmount);

                // Vérifier ou créer la ligne Revenue
                LocalDate now = LocalDate.now();
                int month = now.getMonthValue();
                int year = now.getYear();

                Revenue revenue = session.createQuery("""
                FROM Revenue WHERE month = :month AND year = :year
            """, Revenue.class)
                        .setParameter("month", month)
                        .setParameter("year", year)
                        .uniqueResult();

                if (revenue != null) {
                    revenue.setTotalFee(revenue.getTotalFee() + mgaFeeAmount);
                    session.merge(revenue);
                } else {
                    Revenue newRevenue = new Revenue(month, year, mgaFeeAmount);
                    session.persist(newRevenue);
                }

                session.persist(send);
                tx.commit();

            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw new RuntimeException("Erreur lors de la transaction d'envoi : " + e.getMessage(), e);
            }
        }

        // 5. Email post-transaction
        sendConfirmationEmails(send, feeAmount, mgaAmount);
    }


    private void updateSender(Session session, Client sender, int amount) {
            sender.setBalance(sender.getBalance() - amount);
            session.merge(sender);
    }

    private void updateReceiver(Session session, Client receiver, int amount) {
            receiver.setBalance(receiver.getBalance() + amount);
            session.merge(receiver);
    }

    private void sendConfirmationEmails(Send send, int feeAmount, int mgaAmount) {
        EmailService emailService = new EmailService();

        String senderEmail = send.getSender().getEmail();
        String receiverEmail = send.getReceiver().getEmail();

        NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);

        String formattedAmount = formatter.format(send.getAmount());
        String formattedFee = formatter.format(feeAmount);
        String formattedMgaAmount = formatter.format(mgaAmount);

        String senderMessage = String.format("""
            <html>
            <body>
            <p>Bonjour %s,</p>
            
            <p>Votre opération d’envoi a été effectuée avec succès. Voici les détails :</p>
            
            <ul>
                <li><strong>Montant envoyé</strong> : %s %s</li>
            
                <li><strong>Frais appliqués</strong> : %s %s</li>
            
                <li><strong>Destinataire</strong> : %s</li>
            
                <li><strong>Raison</strong> : %s</li>
            </ul>
            
            <p>Merci d’avoir utilisé notre service.</p>
            </body>
            </html>
            """,
                send.getSender().getClientName(),
                formattedAmount, send.getCurrency().getCode(),
                formattedFee, send.getCurrency().getCode(),
                send.getReceiver().getClientName(),
                send.getReason());


        String receiverMessage = String.format("""
            <html>
            <body>
            <p>Bonjour %s,</p>
            
            <p>Vous avez reçu un transfert d’argent. Voici les détails :</p>
            
            <ul>
                <li><strong>Montant reçu</strong> : %s MGA</li>
            
                <li><strong>Expéditeur</strong> : %s</li>
            
                <li><strong>Raison</strong> : %s</li>
            </ul>
            
            <p>Merci de votre confiance.</p>
            </body>
            </html>
            """,
                send.getReceiver().getClientName(),
                formattedMgaAmount,
                send.getSender().getClientName(),
                send.getReason());


        emailService.sendEmail(senderEmail, "Confirmation d'envoi", senderMessage);
        emailService.sendEmail(receiverEmail, "Confirmation de réception", receiverMessage);
    }

    public boolean delete(String sendId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Send send = session.get(Send.class, sendId);
            if (send == null) return false;
            tx = session.beginTransaction();
            session.remove(send);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public byte[] generatePDF(Client client, int month, int year) {
        // 1. Récupérer les envois du client pour le mois/année
        List<Send> sends;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            sends = session.createQuery(
                            "FROM Send s WHERE s.sender.id = :cid AND month(s.date) = :m AND year(s.date) = :y ORDER BY s.date",
                            Send.class)
                    .setParameter("cid", client.getClientId())
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .getResultList();
        }

        // 2. Créer un flux en mémoire pour le PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            // Police par défaut
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            doc.setFont(font).setFontSize(12);

            // 3. En-tête
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            NumberFormat fmt = NumberFormat.getInstance(Locale.FRANCE);

            // Date en haut à droite
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.FRANCE);
            doc.add(new Paragraph("Date : " + monthName + " " + year)
                    .setTextAlignment(TextAlignment.RIGHT));

            // Infos client
            doc.add(new Paragraph("Contact : " + client.getPhoneNumber()));
            doc.add(new Paragraph(client.getClientName()));

            doc.add(new Paragraph(client.getGender().equals("male") ? "Masculin" : "Féminin"));
            doc.add(new Paragraph("Solde actuel : "
                    + fmt.format(client.getBalance()) + " "
                    + client.getCountry().getCurrency().getCode()));

            doc.add(new Paragraph("\n")); // saut de ligne

            // 4. Tableau des envois
            float[] colWidths = { 2f, 4f, 4f, 2f };
            Table table = new Table(UnitValue.createPercentArray(colWidths))
                    .useAllAvailableWidth();

            // En-têtes
            table.addHeaderCell("Date");
            table.addHeaderCell("Raison");
            table.addHeaderCell("Nom du récepteur");
            table.addHeaderCell("Montant");

            double totalDebit = 0;
            for (Send s : sends) {
                table.addCell(dtf.format(s.getDate()));
                table.addCell(s.getReason());
                table.addCell(s.getReceiver().getClientName());
                String montant = fmt.format(s.getAmount());
                table.addCell(montant);
                totalDebit += s.getAmount();
            }

            doc.add(table);
            doc.add(new Paragraph("\n"));

            // Créer une police en gras
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // 5. Total
            doc.add(new Paragraph("Total Débit : "
                    + fmt.format(totalDebit) + " " + client.getCountry().getCurrency().getCode())
                    .setFont(boldFont));

            // 6. Fermer le document
            doc.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}

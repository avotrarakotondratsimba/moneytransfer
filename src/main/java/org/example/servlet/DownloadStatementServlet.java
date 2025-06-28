package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.example.model.Client;
import org.example.dao.ClientDAO;
import org.example.dao.SendDAO;

@WebServlet("/downloadStatement")
public class DownloadStatementServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Valider les paramètres
            String senderPhone = request.getParameter("senderPhone");
            String monthStr = request.getParameter("month");
            String yearStr = request.getParameter("year");

            if (senderPhone == null || monthStr == null || yearStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
                return;
            }

            // 2. Convertir mois/année
            int month, year;
            try {
                month = Integer.parseInt(monthStr);
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mois ou année invalide");
                return;
            }

            // 3. Récupérer le client
            ClientDAO clientDAO = new ClientDAO();
            List<Client> clients = clientDAO.findByNameOrPhone(senderPhone);

            if (clients.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Aucun client trouvé avec le numéro: " + senderPhone);
                return;
            }

            // Prendre le premier client trouvé
            Client client = clients.getFirst();

            // 4. Générer le PDF
            SendDAO sendDAO = new SendDAO();
            byte[] pdfBytes = sendDAO.generatePDF(client, month, year);

            // 5. Configurer la réponse
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"releve_" + client.getPhoneNumber() + "_" + month + "_" + year + ".pdf\"");
            response.setContentLength(pdfBytes.length);

            // 6. Envoyer le PDF
            try (OutputStream out = response.getOutputStream()) {
                out.write(pdfBytes);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }
}

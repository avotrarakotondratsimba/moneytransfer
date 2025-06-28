package org.example.dao;

import org.example.model.ExchangeRate;
import org.example.model.TransferFee;
import org.example.model.MonetaryUnit;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TransferFeeDAO {
    public List<TransferFee> findByCurrency(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (keyword == null || keyword.trim().isEmpty()) {
                return session.createQuery("from TransferFee order by transferId", TransferFee.class).getResultList();
            }

            String kw = "%" + keyword.toLowerCase() + "%";

            List<MonetaryUnit> monetaryUnitList = session.createQuery(
                            "FROM MonetaryUnit m WHERE lower(m.code) LIKE :code or lower(m.name) LIKE :code", MonetaryUnit.class)
                    .setParameter("code", kw)
                    .getResultList();

            return session.createQuery("from TransferFee where currency in :monetaryUnitList order by transferId", TransferFee.class)
                    .setParameter("monetaryUnitList", monetaryUnitList)
                    .getResultList();
        }
    }

    public void create(TransferFee transferFee) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Vérifier s'il existe déjà une plage de frais en chevauchement avec le nouveau
            List<TransferFee> overlaps = session.createQuery("""
                FROM TransferFee t
                WHERE t.currency = :currency
                  AND NOT (:newMax < t.minAmount OR :newMin > t.maxAmount)
            """, TransferFee.class)
                        .setParameter("currency", transferFee.getCurrency())
                        .setParameter("newMin", transferFee.getMinAmount())
                        .setParameter("newMax", transferFee.getMaxAmount())
                        .getResultList();


            if (!overlaps.isEmpty()) {
                throw new RuntimeException("Chevauchement détecté avec une plage existante.");
            }

            tx = session.beginTransaction();
            session.persist(transferFee);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean update(TransferFee updatedTransferFee) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TransferFee existing = session.get(TransferFee.class, updatedTransferFee.getTransferId());
            if (existing == null) return false;

            // Vérifier s'il existe déjà une plage de frais en chevauchement avec le nouveau
            List<TransferFee> overlaps = session.createQuery("""
                FROM TransferFee t
                WHERE t.currency = :currency
                AND transferId <> :transferId
                  AND NOT (:newMax < t.minAmount OR :newMin > t.maxAmount)
            """, TransferFee.class)
                    .setParameter("currency", updatedTransferFee.getCurrency())
                    .setParameter("transferId", updatedTransferFee.getTransferId())
                    .setParameter("newMin", updatedTransferFee.getMinAmount())
                    .setParameter("newMax", updatedTransferFee.getMaxAmount())
                    .getResultList();


            if (!overlaps.isEmpty()) {
                throw new RuntimeException("Chevauchement détecté avec une plage existante.");
            }

            tx = session.beginTransaction();

            existing.setMinAmount(updatedTransferFee.getMinAmount());
            existing.setMaxAmount(updatedTransferFee.getMaxAmount());
            existing.setFeeAmount(updatedTransferFee.getFeeAmount());

            session.merge(existing);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean delete(Long transferId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TransferFee transferFee = session.get(TransferFee.class, transferId);
            if (transferFee == null) return false;
            tx = session.beginTransaction();
            session.remove(transferFee);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

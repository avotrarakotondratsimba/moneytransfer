package org.example.dao;

import jakarta.persistence.NoResultException;
import org.example.model.ExchangeRate;
import org.example.model.MonetaryUnit;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;


public class ExchangeRateDAO {
    public List<ExchangeRate> findByCurrency(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (keyword == null || keyword.trim().isEmpty()) {
                return session.createQuery("from ExchangeRate order by exchangeId", ExchangeRate.class).getResultList();
            }

            String kw = "%" + keyword.toLowerCase() + "%";

            List<MonetaryUnit> monetaryUnitList = session.createQuery(
                            "FROM MonetaryUnit m WHERE lower(m.code) LIKE :code or lower(m.name) LIKE :code", MonetaryUnit.class)
                    .setParameter("code", kw)
                    .getResultList();

            return session.createQuery("from ExchangeRate where sourceCurrency in :monetaryUnitList order by exchangeId", ExchangeRate.class)
                    .setParameter("monetaryUnitList", monetaryUnitList)
                    .getResultList();
        }
    }

    public void create(ExchangeRate exchangeRate) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Vérifier s'il existe déjà un taux pour cette unité monétaire
            ExchangeRate existingRate = session.createQuery("""
            FROM ExchangeRate
            WHERE sourceCurrency = :currency
        """, ExchangeRate.class)
                    .setParameter("currency", exchangeRate.getSourceCurrency())
                    .uniqueResult();

            if (existingRate != null) {
                throw new RuntimeException("Un taux de change pour cette unité monétaire existe déjà.");
            }

            tx = session.beginTransaction();
            session.persist(exchangeRate);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean update(ExchangeRate updatedExchangeRate) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ExchangeRate existing = session.get(ExchangeRate.class, updatedExchangeRate.getExchangeId());
            if (existing == null) return false;

            tx = session.beginTransaction();

            existing.setBaseAmount(updatedExchangeRate.getBaseAmount());
            existing.setRateToMGA(updatedExchangeRate.getRateToMGA());
            existing.setModificationDate(updatedExchangeRate.getModificationDate());

            session.merge(existing);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean delete(Long exchangeId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ExchangeRate exchangeRate = session.get(ExchangeRate.class, exchangeId);
            if (exchangeRate == null) return false;
            tx = session.beginTransaction();
            session.remove(exchangeRate);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public float conversion(MonetaryUnit currency, Float amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ExchangeRate exchangeRate;
            try {
                exchangeRate = session.createQuery("""
                FROM ExchangeRate WHERE sourceCurrency = :currency
            """, ExchangeRate.class)
                        .setParameter("currency", currency)
                        .getSingleResult();
            } catch (NoResultException e) {
                throw new RuntimeException("Il n'y a pas de taux de change qui correspond à cette devise");
            }

            return amount * exchangeRate.getRateToMGA() / exchangeRate.getBaseAmount();
        }
    }
}

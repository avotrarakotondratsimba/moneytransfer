package org.example.dao;

import org.example.model.MonetaryUnit;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MonetaryUnitDAO {

    public List<MonetaryUnit> findByCodeOrName(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (keyword == null || keyword.trim().isEmpty()) {
                return session.createSelectionQuery("from MonetaryUnit order by unitId", MonetaryUnit.class)
                        .getResultList();
            }

            return session.createSelectionQuery(
                            "from MonetaryUnit where lower(code) like :kw or lower(name) like :kw order by unitId", MonetaryUnit.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .getResultList();
        }
    }


    public void create(MonetaryUnit unit) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(unit); // persist = pour insertion uniquement
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean update(MonetaryUnit updatedUnit) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Récupérer l'unité monétaire existante
            MonetaryUnit existing = session.get(MonetaryUnit.class, updatedUnit.getUnitId());
            if (existing == null) return false;

            tx = session.beginTransaction();

            // Maintenant, on peut mettre à jour l'unité monétaire
            existing.setCode(updatedUnit.getCode());
            existing.setName(updatedUnit.getName());

            session.merge(existing); // Effectuer la mise à jour
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean delete(Long unitId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            MonetaryUnit unit = session.get(MonetaryUnit.class, unitId);
            if (unit == null) return false;

            boolean hasDependencies =
                    !session.createSelectionQuery("from Client where currency = :unit", Object.class).setParameter("unit", unit).getResultList().isEmpty() ||
                            !session.createSelectionQuery("from ExchangeRate where sourceCurrency = :unit", Object.class).setParameter("unit", unit).getResultList().isEmpty() ||
                            !session.createSelectionQuery("from Send where currency = :unit", Object.class).setParameter("unit", unit).getResultList().isEmpty() ||
                            !session.createSelectionQuery("from TransferFee where currency = :unit", Object.class).setParameter("unit", unit).getResultList().isEmpty();

            if (hasDependencies) return false;

            tx = session.beginTransaction();
            session.remove(unit); // session.delete() → replaced by session.remove()
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

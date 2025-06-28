package org.example.dao;

import org.example.model.Client;
import org.example.model.Country;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CountryDAO {
    public List<Country> findByName(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (keyword == null || keyword.trim().isEmpty()) {
                return session.createQuery("from Country order by name", Country.class).getResultList();
            }
            String kw = "%" + keyword.toLowerCase() + "%";
            return session.createQuery("from Country where lower(name) like :kw order by name", Country.class)
                    .setParameter("kw", kw)
                    .getResultList();
        }
    }

    public void create(Country country) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(country);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean update(Country updatedCountry) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Country existing = session.get(Country.class, updatedCountry.getCountryId());
            if (existing == null) return false;

            tx = session.beginTransaction();
            existing.setName(updatedCountry.getName());
            existing.setPhoneCode(updatedCountry.getPhoneCode());
            existing.setCurrency(updatedCountry.getCurrency());
            session.merge(existing);
            tx.commit();

            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean delete(Long countryId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Country country = session.get(Country.class, countryId);
            if (country == null) return false;

            tx = session.beginTransaction();
            session.remove(country);
            tx.commit();

            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

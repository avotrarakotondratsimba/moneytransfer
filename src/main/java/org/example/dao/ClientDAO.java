package org.example.dao;

import org.example.model.ExchangeRate;
import org.hibernate.query.Query;
import org.example.model.Client;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public List<Client> findByNameOrPhone(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (keyword == null || keyword.trim().isEmpty()) {
                return session.createQuery("from Client order by clientId", Client.class).getResultList();
            }

            String kw = "%" + keyword.toLowerCase() + "%";

            return session.createQuery("from Client where lower(clientName) like :kw or lower(phoneNumber) like :kw order by clientId", Client.class)
                    .setParameter("kw", kw)
                    .getResultList();
        }
    }

    public List<Client> findAbroadClient(String keyword) {
        List<Client> clients = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Client c WHERE LOWER(c.country.name) <> :countryName";

            if (keyword != null && !keyword.isEmpty()) {
                hql += " AND (LOWER(c.clientName) LIKE :kw OR c.phoneNumber LIKE :kw)";
            }

            hql += " ORDER BY clientId";

            Query<Client> query = session.createQuery(hql, Client.class);
            query.setParameter("countryName", "madagascar");

            if (keyword != null && !keyword.isEmpty()) {
                query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            }

            clients = query.list();
        }

        return clients;
    }


    public List<Client> findLocalClient(String keyword) {
        List<Client> clients = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Client c WHERE LOWER(c.country.name) = :countryName";

            if (keyword != null && !keyword.isEmpty()) {
                hql += " AND (LOWER(c.clientName) LIKE :kw OR c.phoneNumber LIKE :kw)";
            }

            hql += " ORDER BY clientId";

            Query<Client> query = session.createQuery(hql, Client.class);
            query.setParameter("countryName", "madagascar");

            if (keyword != null && !keyword.isEmpty()) {
                query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            }

            clients = query.list();
        }

        return clients;
    }

    public void create(Client client) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Vérifier s'il existe déjà un client avec le même numéro
            Client existingClient = session.createQuery("""
            FROM Client
            WHERE phoneNumber = :phoneNumber
        """, Client.class)
                    .setParameter("phoneNumber", client.getPhoneNumber())
                    .uniqueResult();

            if (existingClient != null) {
                throw new RuntimeException("Un client ayant le même numéro de téléphone existe déjà.");
            }

            tx = session.beginTransaction();
            session.persist(client);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean delete(Long clientId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, clientId);
            if (client == null) return false;

            boolean hasDependencies = !session.createSelectionQuery("from Send where sender = :client or receiver = :client", Object.class).setParameter("client", client).getResultList().isEmpty();

            if (hasDependencies) return false;

            tx = session.beginTransaction();
            session.remove(client);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean update(Client updatedClient) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client existing = session.get(Client.class, updatedClient.getClientId());
            if (existing == null) return false;

            // Vérifier s'il existe déjà un client avec le même numéro
            Client existingClient = session.createQuery("""
            FROM Client
            WHERE phoneNumber = :phoneNumber
            AND clientId <> :clientId
        """, Client.class)
                    .setParameter("phoneNumber", updatedClient.getPhoneNumber())
                    .setParameter("clientId", updatedClient.getClientId())
                    .uniqueResult();

            if (existingClient != null) {
                throw new RuntimeException("Un client ayant le même numéro de téléphone existe déjà.");
            }

            tx = session.beginTransaction();

            existing.setPhoneNumber(updatedClient.getPhoneNumber());
            existing.setClientName(updatedClient.getClientName());
            existing.setGender(updatedClient.getGender());
            existing.setCountry(updatedClient.getCountry());
            existing.setBalance(updatedClient.getBalance());
            existing.setEmail(updatedClient.getEmail());
            existing.setIsActive(updatedClient.getIsActive());

            session.merge(existing);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}



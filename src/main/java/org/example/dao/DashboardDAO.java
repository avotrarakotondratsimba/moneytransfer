package org.example.dao;

import org.example.model.Revenue;
import org.example.model.Send;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

public class DashboardDAO {

    public int getTotalMonthlyRevenue(int month, int year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery("""
                SELECT SUM(r.totalFee) FROM Revenue r
                WHERE r.month = :month AND r.year = :year
            """, Long.class)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .uniqueResult();

            return total != null ? total.intValue() : 0;
        }
    }

    public int getTotalAnnualRevenue(int year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery("""
                SELECT SUM(r.totalFee) FROM Revenue r
                WHERE r.year = :year
            """, Long.class)
                    .setParameter("year", year)
                    .uniqueResult();

            return total != null ? total.intValue() : 0;
        }
    }

    public long getMonthlyOperationCount(int month, int year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate start = LocalDate.of(year, month, 1);
            LocalDate end = start.plusMonths(1);

            Long count = session.createQuery("""
                SELECT COUNT(s) FROM Send s
                WHERE s.date >= :start AND s.date < :end
            """, Long.class)
                    .setParameter("start", start.atStartOfDay())
                    .setParameter("end", end.atStartOfDay())
                    .uniqueResult();

            return count != null ? count : 0L;
        }
    }

    public long getAnnualOperationCount(int year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate start = Year.of(year).atDay(1);
            LocalDate end = start.plusYears(1);

            Long count = session.createQuery("""
                SELECT COUNT(s) FROM Send s
                WHERE s.date >= :start AND s.date < :end
            """, Long.class)
                    .setParameter("start", start.atStartOfDay())
                    .setParameter("end", end.atStartOfDay())
                    .uniqueResult();

            return count != null ? count : 0L;
        }
    }

    public List<Object[]> getMonthlyRevenueStats(int year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                SELECT r.month, SUM(r.totalFee) FROM Revenue r
                WHERE r.year = :year
                GROUP BY r.month
                ORDER BY r.month
            """, Object[].class)
                    .setParameter("year", year)
                    .getResultList();
        }
    }

    public List<Object[]> getMonthlyOperationStats(int year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                SELECT MONTH(s.date), COUNT(s) FROM Send s
                WHERE YEAR(s.date) = :year
                GROUP BY MONTH(s.date)
                ORDER BY MONTH(s.date)
            """, Object[].class)
                    .setParameter("year", year)
                    .getResultList();
        }
    }
}

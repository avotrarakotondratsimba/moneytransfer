package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.dao.DashboardDAO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final DashboardDAO dao = new DashboardDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Mois/année courants
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        // Collecte des données
        int monthlyRevenue = dao.getTotalMonthlyRevenue(month, year);
        int annualRevenue = dao.getTotalAnnualRevenue(year);
        long monthlyOps = dao.getMonthlyOperationCount(month, year);
        long annualOps = dao.getAnnualOperationCount(year);
        List<Object[]> revenueStats = dao.getMonthlyRevenueStats(year);
        List<Object[]> operationStats = dao.getMonthlyOperationStats(year);

        // Format JSON à envoyer
        Map<String, Object> data = new HashMap<>();
        data.put("monthlyRevenue", monthlyRevenue);
        data.put("annualRevenue", annualRevenue);
        data.put("monthlyOperations", monthlyOps);
        data.put("annualOperations", annualOps);
        data.put("revenueStats", revenueStats);
        data.put("operationStats", operationStats);

        // Configuration de la réponse
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }
}

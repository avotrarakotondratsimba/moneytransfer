package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.dao.ExchangeRateDAO;
import org.example.model.ExchangeRate;
import org.example.model.MonetaryUnit;
import org.example.utils.HibernateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/exchange-rates/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateDAO dao = new ExchangeRateDAO();
    private final ObjectMapper mapper;

    public ExchangeRateServlet() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if ("/convert".equals(pathInfo)) {
            handleConversion(req, resp);
            return;
        }

        List<ExchangeRate> exchangeRates;

        String requestedWith = req.getHeader("X-Requested-With");

        if ("XMLHttpRequest".equals(requestedWith)) {
            String keyword = req.getParameter("search");
            exchangeRates = dao.findByCurrency(keyword);
            String json = this.mapper.writeValueAsString(exchangeRates);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
        }
        else {
            exchangeRates = dao.findByCurrency(null);
            req.setAttribute("rates", exchangeRates);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ExchangeRate exchangeRate = mapper.readValue(req.getInputStream(), ExchangeRate.class);

        try {
            dao.create(exchangeRate);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Taux de change enregistré avec succès\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\" " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExchangeRate updatedExchangeRate = mapper.readValue(req.getInputStream(), ExchangeRate.class);

        try {
            boolean updated = dao.update(updatedExchangeRate);
            if (updated) {
                resp.getWriter().write("{\"message\":\"Mise à jour effectuée avec succès\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Taux de change non trouvé\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Echec du mis à jour du taux de change: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String exchangeIdStr = req.getPathInfo();
        Long exchangeId = null;

        if (exchangeIdStr != null && !exchangeIdStr.isEmpty()) {
            try {
                exchangeId = Long.parseLong(exchangeIdStr.substring(1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Absence du paramètre exhangeId\"}");
            return;
        }

        try {
            boolean deleted = dao.delete(exchangeId);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"exchangeId non trouvé\"}");}
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleConversion(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String currencyIdStr = req.getParameter("currencyId");
            String amountStr = req.getParameter("amount");

            if (currencyIdStr == null || amountStr == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Absence du paramètre currency ou amount\"}");
                return;
            }

            Long currencyId = Long.parseLong(currencyIdStr);
            Float amount = Float.parseFloat(amountStr);

            // Récupérer la devise
            MonetaryUnit currency;
            try (var session = HibernateUtil.getSessionFactory().openSession()) {
                currency = session.get(MonetaryUnit.class, currencyId);
            }

            if (currency == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Devise non trouvée\"}");
                return;
            }

            // Calculer la conversion
            ExchangeRateDAO dao = new ExchangeRateDAO();
            float mgaAmount = dao.conversion(currency, amount);

            resp.getWriter().write("{\"convertedAmount\":" + mgaAmount + "}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

}

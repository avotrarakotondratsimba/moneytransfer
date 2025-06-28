package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.dao.MonetaryUnitDAO;
import org.example.model.MonetaryUnit;

import java.io.IOException;
import java.util.List;

@WebServlet("/monetary-units")
public class MonetaryUnitServlet extends HttpServlet {

    private final MonetaryUnitDAO dao = new MonetaryUnitDAO();
    private final ObjectMapper mapper = new ObjectMapper(); // pour convertir en JSON

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("search");

        List<MonetaryUnit> units = dao.findByCodeOrName(keyword);
        String json = mapper.writeValueAsString(units);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        MonetaryUnit unit = mapper.readValue(req.getInputStream(), MonetaryUnit.class);

        try {
            dao.create(unit);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Created successfully\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to create unit: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Convertir le corps de la requête JSON en objet MonetaryUnit
        MonetaryUnit updatedUnit = mapper.readValue(req.getInputStream(), MonetaryUnit.class);

        try {
            // Appeler la méthode pour mettre à jour l'unité monétaire
            boolean updated = dao.update(updatedUnit);
            if (updated) {
                resp.getWriter().write("{\"message\":\"Updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Unit not found\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to update unit: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String unitIdStr = req.getParameter("unitId");
        Long unitId = null;

        if (unitIdStr != null && !unitIdStr.isEmpty()) {
            try {
                unitId = Long.parseLong(unitIdStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing unitId parameter\"}");
            return;
        }

        try {
            boolean deleted = dao.delete(unitId);
            if (deleted) {
                resp.getWriter().write("{\"message\":\"Deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Cannot delete: either not found or has dependencies\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

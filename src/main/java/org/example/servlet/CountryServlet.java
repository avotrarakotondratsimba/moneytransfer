package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.dao.CountryDAO;
import org.example.model.Country;

import java.io.IOException;
import java.util.List;

@WebServlet("/countries/*")
public class CountryServlet extends HttpServlet {

    private final CountryDAO dao = new CountryDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("search");

        List<Country> countries = dao.findByName(keyword);

        String json = mapper.writeValueAsString(countries);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Country country = mapper.readValue(req.getInputStream(), Country.class);

        try {
            dao.create(country);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Pays ajouté avec succès\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Erreur lors de l'ajout du pays: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Country updatedCountry = mapper.readValue(req.getInputStream(), Country.class);

        try {
            boolean updated = dao.update(updatedCountry);
            if (updated) {
                resp.getWriter().write("{\"message\":\"Pays mis à jour avex succès\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Pays non trouvé\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Erreur lors du mis à jour du pays: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Long countryId = null;

        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                countryId = Long.parseLong(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Identifiant du pays non valide\"}");
                return;
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Absence de l'identifiant du pays dans le chemin\"}");
            return;
        }

        try {
            boolean deleted = dao.delete(countryId);
            if (deleted) {
                resp.getWriter().write("{\"message\":\"Information du pays supprimé avec succès\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Pays non trouvé ou ne peut pas être supprimé\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.dao.ClientDAO;
import org.example.model.Client;

import java.io.IOException;
import java.util.List;

@WebServlet("/clients/*")
public class ClientServlet extends HttpServlet {

    private final ClientDAO dao = new ClientDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String keyword = req.getParameter("search");

        List<Client> clients;

        if (pathInfo != null && pathInfo.equals("/abroad")) {
            clients = dao.findAbroadClient(keyword);
        } else if (pathInfo != null && pathInfo.equals("/local")) {
            clients = dao.findLocalClient(keyword);
        } else clients = dao.findByNameOrPhone(keyword);

        String json = mapper.writeValueAsString(clients);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Client clients = mapper.readValue(req.getInputStream(), Client.class);

        try {
            dao.create(clients);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Client crée avec succès\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\" " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Client updatedClient = mapper.readValue(req.getInputStream(), Client.class);

        try {
            boolean updated = dao.update(updatedClient);
            if (updated) {
                resp.getWriter().write("{\"message\":\"Client mis à jour avec succès\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Le client n'existe pas\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\" " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String clientIdStr = req.getPathInfo();
        Long clientId = null;

        if (clientIdStr != null && !clientIdStr.isEmpty()) {
            try {
                clientId = Long.parseLong(clientIdStr.substring(1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Absence du paramètre clientId\"}");
            return;
        }

        try {
            boolean deleted = dao.delete(clientId);
            if (deleted) {
                resp.getWriter().write("{\"message\":\"Client supprimé avec succès\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Le client est lié à une opération d'envoi\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

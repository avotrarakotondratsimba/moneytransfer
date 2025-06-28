package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.dao.TransferFeeDAO;
import org.example.model.TransferFee;

import java.io.IOException;
import java.util.List;

@WebServlet("/transfer-fees/*")
public class TransferFeeServlet extends HttpServlet {

    private final TransferFeeDAO dao = new TransferFeeDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("search");

        List<TransferFee> transferFees = dao.findByCurrency(keyword);
        String json = mapper.writeValueAsString(transferFees);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        TransferFee transferFee = mapper.readValue(req.getInputStream(), TransferFee.class);

        try {
            dao.create(transferFee);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Frais de transfert crée avec succès\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\" " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        TransferFee updatedTransferFee = mapper.readValue(req.getInputStream(), TransferFee.class);

        try {
            boolean updated = dao.update(updatedTransferFee);
            if (updated) {
                resp.getWriter().write("{\"message\":\"Frais de transfert mis à jour avec succès\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Le frais de transfert n'existe pas\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\" " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String transferIdStr = req.getPathInfo();
        Long transferId = null;

        if (transferIdStr != null && !transferIdStr.isEmpty()) {
            try {
                transferId = Long.parseLong(transferIdStr.substring(1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing transferId parameter\"}");
            return;
        }

        try {
            boolean deleted = dao.delete(transferId);
            if (deleted) {
                resp.getWriter().write("{\"message\":\"Deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Cannot delete: transferId not found\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

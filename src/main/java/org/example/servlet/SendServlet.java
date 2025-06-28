package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.dao.SendDAO;
import org.example.dao.TransferFeeDAO;
import org.example.dto.SendDTO;
import org.example.model.Send;
import org.example.model.TransferFee;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.hibernate.internal.util.ExceptionHelper.getRootCause;

@WebServlet("/sends/*")
public class SendServlet extends HttpServlet {

    private final SendDAO dao = new SendDAO();
    private final ObjectMapper mapper;

    public SendServlet() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String keyword = req.getParameter("search");
//
//        List<SendDTO> sends = dao.findByPhone(keyword);
//        String json = mapper.writeValueAsString(sends);
//
//        resp.setContentType("application/json");
//        resp.setCharacterEncoding("UTF-8");
//        resp.getWriter().write(json);
//    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("search");
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr, formatter).atStartOfDay(); // 00:00:00
            }

            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX); // 23:59:59.999999999
            }
        } catch (DateTimeParseException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Format de date invalide. Format attendu : jj/MM/aaaa\"}");
            return;
        }

        List<SendDTO> sends = dao.findByPhone(keyword, startDate, endDate);
        String json = mapper.writeValueAsString(sends);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Send send = mapper.readValue(req.getInputStream(), Send.class);

        try {
            dao.create(send);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Envoi effectué avec succès\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\" " + e.getMessage() + "\"}");
        }
    }

//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
//
//        try {
//            Send send = mapper.readValue(req.getInputStream(), Send.class);
//
//            try {
//                dao.create(send);
//                resp.setStatus(HttpServletResponse.SC_CREATED);
//                resp.getWriter().write("{\"message\":\"Created successfully\"}");
//            } catch (RuntimeException e) {
//                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                resp.getWriter().write("{\"Runtime error\":\"" + escapeJson(e.getMessage()) + "\"}");
//            } catch (Exception e) {
//                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                resp.getWriter().write("{\"error\":\"Failed to create send: " + e.getMessage() + "\"}");
//            }
//        } catch (Exception e) {
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            resp.getWriter().write("{\"error\":\"Invalid request data\"}");
//        }
//    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String sendId = req.getPathInfo().substring(1);

        if (sendId.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing sendId parameter\"}");
            return;
        }

        try {
            boolean deleted = dao.delete(sendId);
            if (deleted) {
                resp.getWriter().write("{\"message\":\"Deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Cannot delete: sendId not found\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

//    private String escapeJson(String message) {
//        return message == null ? "" : message.replace("\"", "\\\"").replace("\n", "").replace("\r", "");
//    }
}

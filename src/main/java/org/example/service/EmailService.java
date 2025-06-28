package org.example.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import org.example.config.ConfigLoader;

public class EmailService {
    private final String username = ConfigLoader.get("email.username");
    private final String password = ConfigLoader.get("email.password");

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(text, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("Email envoyé à " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Échec d'envoi de mail : " + e.getMessage(), e);
        }
    }
}


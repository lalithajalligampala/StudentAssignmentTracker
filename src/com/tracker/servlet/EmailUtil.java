package com.tracker.servlet;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    public static void sendEmail(String to, String subject, String messageText)
            throws Exception {

        // Your Gmail address
        final String fromEmail = System.getenv("MAIL_USERNAME");

        // Gmail App Password
        final String password = System.getenv("MAIL_PASSWORD");

        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            throw new Exception("MAIL_USERNAME environment variable is missing.");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new Exception("MAIL_PASSWORD environment variable is missing.");
        }

        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(
                properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                fromEmail,
                                password
                        );
                    }
                }
        );

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(fromEmail));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
        );

        message.setSubject(subject);

        message.setText(messageText);

        Transport.send(message);

        System.out.println(
                "Email sent successfully to: " + to
        );
    }
}
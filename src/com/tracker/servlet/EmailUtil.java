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

    /*
     * ============================================================
     * GMAIL SMTP CONFIGURATION
     * ============================================================
     *
     * IMPORTANT:
     * Gmail username and App Password are read from
     * environment variables.
     *
     * DO NOT put the Gmail App Password directly in this file.
     */

    private static final String SENDER_EMAIL =
            System.getenv("GMAIL_USERNAME");

    private static final String SENDER_PASSWORD =
            System.getenv("GMAIL_APP_PASSWORD");


    /*
     * ============================================================
     * SEND EMAIL
     * ============================================================
     */

    public static void sendEmail(
            String recipientEmail,
            String subject,
            String body) throws Exception {

        // --------------------------------------------------------
        // Validate environment variables
        // --------------------------------------------------------

        if (SENDER_EMAIL == null ||
            SENDER_EMAIL.trim().isEmpty()) {

            throw new Exception(
                "GMAIL_USERNAME environment variable is missing."
            );
        }

        if (SENDER_PASSWORD == null ||
            SENDER_PASSWORD.trim().isEmpty()) {

            throw new Exception(
                "GMAIL_APP_PASSWORD environment variable is missing."
            );
        }


        // --------------------------------------------------------
        // Gmail SMTP properties
        // --------------------------------------------------------

        Properties properties = new Properties();

        properties.put(
            "mail.smtp.host",
            "smtp.gmail.com"
        );

        properties.put(
            "mail.smtp.port",
            "587"
        );

        properties.put(
            "mail.smtp.auth",
            "true"
        );

        properties.put(
            "mail.smtp.starttls.enable",
            "true"
        );


        // --------------------------------------------------------
        // Create Gmail SMTP session
        // --------------------------------------------------------

        Session session = Session.getInstance(
            properties,
            new Authenticator() {

                @Override
                protected PasswordAuthentication
                getPasswordAuthentication() {

                    return new PasswordAuthentication(
                        SENDER_EMAIL,
                        SENDER_PASSWORD
                    );
                }
            }
        );


        // --------------------------------------------------------
        // Create email
        // --------------------------------------------------------

        Message message = new MimeMessage(session);

        message.setFrom(
            new InternetAddress(SENDER_EMAIL)
        );

        message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(recipientEmail)
        );

        message.setSubject(subject);

        message.setText(body);


        // --------------------------------------------------------
        // Send email
        // --------------------------------------------------------

        Transport.send(message);


        System.out.println(
            "Email sent successfully to: "
            + recipientEmail
        );
    }
}
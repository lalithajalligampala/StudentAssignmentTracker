package com.tracker.servlet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailUtil {

    // ============================================================
    // EmailJS Configuration
    // ============================================================

    private static final String EMAILJS_SERVICE_ID =
            getEnv("EMAILJS_SERVICE_ID");

    private static final String EMAILJS_TEMPLATE_ID =
            getEnv("EMAILJS_TEMPLATE_ID");

    private static final String EMAILJS_PUBLIC_KEY =
            getEnv("EMAILJS_PUBLIC_KEY");

    private static final String EMAILJS_PRIVATE_KEY =
            getEnv("EMAILJS_PRIVATE_KEY");

    private static final String EMAILJS_URL =
            "https://api.emailjs.com/api/v1.0/email/send";


    // ============================================================
    // Read and trim environment variables
    // ============================================================

    private static String getEnv(String name) {

        String value = System.getenv(name);

        if (value == null) {
            return null;
        }

        return value.trim();
    }


    // ============================================================
    // Send Assignment Reminder Email
    // ============================================================

    public static void sendReminderEmail(
            String recipientEmail,
            String assignmentName,
            String subjectName,
            String deadline,
            String deadlineTime,
            String reminderType,
            String message) throws Exception {

        // --------------------------------------------------------
        // Validate EmailJS configuration
        // --------------------------------------------------------

        if (EMAILJS_SERVICE_ID == null ||
            EMAILJS_SERVICE_ID.isEmpty()) {

            throw new Exception(
                    "EMAILJS_SERVICE_ID environment variable is missing."
            );
        }


        if (EMAILJS_TEMPLATE_ID == null ||
            EMAILJS_TEMPLATE_ID.isEmpty()) {

            throw new Exception(
                    "EMAILJS_TEMPLATE_ID environment variable is missing."
            );
        }


        if (EMAILJS_PUBLIC_KEY == null ||
            EMAILJS_PUBLIC_KEY.isEmpty()) {

            throw new Exception(
                    "EMAILJS_PUBLIC_KEY environment variable is missing."
            );
        }


        if (EMAILJS_PRIVATE_KEY == null ||
            EMAILJS_PRIVATE_KEY.isEmpty()) {

            throw new Exception(
                    "EMAILJS_PRIVATE_KEY environment variable is missing."
            );
        }


        if (recipientEmail == null ||
            recipientEmail.trim().isEmpty()) {

            throw new Exception(
                    "Recipient email address is missing."
            );
        }


        // --------------------------------------------------------
        // Clean input values
        // --------------------------------------------------------

        recipientEmail = recipientEmail.trim();

        if (assignmentName == null) {
            assignmentName = "";
        }

        if (subjectName == null) {
            subjectName = "";
        }

        if (deadline == null) {
            deadline = "";
        }

        if (deadlineTime == null) {
            deadlineTime = "";
        }

        if (reminderType == null) {
            reminderType = "";
        }

        if (message == null) {
            message = "";
        }


        // --------------------------------------------------------
        // Email subject
        // --------------------------------------------------------

        String emailSubject =
                "Assignment Reminder - " + assignmentName;


        // --------------------------------------------------------
        // Create JSON request
        // --------------------------------------------------------

        String jsonBody =
                "{"
                + "\"service_id\":\""
                + escapeJson(EMAILJS_SERVICE_ID)
                + "\","

                + "\"template_id\":\""
                + escapeJson(EMAILJS_TEMPLATE_ID)
                + "\","

                + "\"user_id\":\""
                + escapeJson(EMAILJS_PUBLIC_KEY)
                + "\","

                + "\"accessToken\":\""
                + escapeJson(EMAILJS_PRIVATE_KEY)
                + "\","

                + "\"template_params\":{"

                + "\"email\":\""
                + escapeJson(recipientEmail)
                + "\","

                + "\"assignment_name\":\""
                + escapeJson(assignmentName)
                + "\","

                + "\"subject\":\""
                + escapeJson(subjectName)
                + "\","

                + "\"deadline\":\""
                + escapeJson(deadline)
                + "\","

                + "\"deadline_time\":\""
                + escapeJson(deadlineTime)
                + "\","

                + "\"reminder_type\":\""
                + escapeJson(reminderType)
                + "\","

                + "\"message\":\""
                + escapeJson(message)
                + "\""

                + "}"
                + "}";


        // --------------------------------------------------------
        // Send HTTP request to EmailJS
        // --------------------------------------------------------

        HttpURLConnection connection = null;

        try {

            System.out.println("--------------------------------------");
            System.out.println("Sending reminder through EmailJS...");
            System.out.println("Recipient: " + recipientEmail);
            System.out.println("Assignment: " + assignmentName);
            System.out.println("Reminder Type: " + reminderType);
            System.out.println("--------------------------------------");


            // ----------------------------------------------------
            // Connect to EmailJS
            // ----------------------------------------------------

            URL url = new URL(EMAILJS_URL);

            connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");


            // ----------------------------------------------------
            // HTTP Headers
            // ----------------------------------------------------

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );

            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            connection.setRequestProperty(
                    "User-Agent",
                    "StudentAssignmentTracker/1.0"
            );


            connection.setDoOutput(true);

            connection.setConnectTimeout(15000);

            connection.setReadTimeout(15000);


            // ----------------------------------------------------
            // Send request
            // ----------------------------------------------------

            try (OutputStream outputStream =
                         connection.getOutputStream()) {

                byte[] input =
                        jsonBody.getBytes(StandardCharsets.UTF_8);

                outputStream.write(input);

                outputStream.flush();
            }


            // ----------------------------------------------------
            // Get response
            // ----------------------------------------------------

            int responseCode =
                    connection.getResponseCode();

            InputStream responseStream;

            if (responseCode >= 200 &&
                responseCode < 300) {

                responseStream =
                        connection.getInputStream();

            } else {

                responseStream =
                        connection.getErrorStream();
            }


            // ----------------------------------------------------
            // Read response
            // ----------------------------------------------------

            StringBuilder response =
                    new StringBuilder();

            if (responseStream != null) {

                try (BufferedReader reader =
                             new BufferedReader(
                                 new InputStreamReader(
                                     responseStream,
                                     StandardCharsets.UTF_8))) {

                    String line;

                    while ((line = reader.readLine()) != null) {

                        response.append(line);
                    }
                }
            }


            // ----------------------------------------------------
            // EmailJS success
            // ----------------------------------------------------

            if (responseCode >= 200 &&
                responseCode < 300) {

                System.out.println(
                        "======================================"
                );

                System.out.println(
                        "EmailJS reminder sent successfully"
                );

                System.out.println(
                        "Recipient: " + recipientEmail
                );

                System.out.println(
                        "Assignment: " + assignmentName
                );

                System.out.println(
                        "Reminder: " + reminderType
                );

                System.out.println(
                        "Email Subject: " + emailSubject
                );

                System.out.println(
                        "EmailJS Response: " + response
                );

                System.out.println(
                        "======================================"
                );


            } else {

                // ------------------------------------------------
                // EmailJS failure
                // ------------------------------------------------

                System.err.println(
                        "======================================"
                );

                System.err.println(
                        "EmailJS reminder failed"
                );

                System.err.println(
                        "HTTP Status: " + responseCode
                );

                System.err.println(
                        "Recipient: " + recipientEmail
                );

                System.err.println(
                        "Assignment: " + assignmentName
                );

                System.err.println(
                        "Reminder: " + reminderType
                );

                System.err.println(
                        "EmailJS Response: " + response
                );

                System.err.println(
                        "======================================"
                );


                throw new Exception(
                        "EmailJS failed with HTTP status "
                        + responseCode
                        + ": "
                        + response
                );
            }


        } finally {

            if (connection != null) {

                connection.disconnect();
            }
        }
    }


    // ============================================================
    // Escape special characters for JSON
    // ============================================================

    private static String escapeJson(String value) {

        if (value == null) {

            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
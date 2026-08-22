package com.tracker.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailUtil {

    private static final String RESEND_API_URL =
            "https://api.resend.com/emails";

    public static void sendEmail(
            String to,
            String subject,
            String messageText) throws Exception {

        // Get values from environment variables
        String apiKey = System.getenv("RESEND_API_KEY");
        String fromEmail = System.getenv("RESEND_FROM_EMAIL");

        // Validate API key
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception(
                    "RESEND_API_KEY environment variable is missing."
            );
        }

        // Validate sender email
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            throw new Exception(
                    "RESEND_FROM_EMAIL environment variable is missing."
            );
        }

        // Validate recipient
        if (to == null || to.trim().isEmpty()) {
            throw new Exception("Recipient email is missing.");
        }

        // Prevent null values
        if (subject == null) {
            subject = "";
        }

        if (messageText == null) {
            messageText = "";
        }

        // Build JSON request body
        String jsonBody =
                "{"
                + "\"from\":\"" + escapeJson(fromEmail) + "\","
                + "\"to\":[\"" + escapeJson(to) + "\"],"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"text\":\"" + escapeJson(messageText) + "\""
                + "}";

        System.out.println("Sending email to: " + to);
        System.out.println("Using Resend API...");

        HttpURLConnection connection = null;

        try {
            // Create connection
            URL url = new URL(RESEND_API_URL);

            connection = (HttpURLConnection) url.openConnection();

            // Configure request
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);

            // Headers
            connection.setRequestProperty(
                    "Authorization",
                    "Bearer " + apiKey
            );

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );

            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            // Send JSON body
            byte[] requestData =
                    jsonBody.getBytes(StandardCharsets.UTF_8);

            connection.setRequestProperty(
                    "Content-Length",
                    String.valueOf(requestData.length)
            );

            OutputStream outputStream =
                    connection.getOutputStream();

            outputStream.write(requestData);
            outputStream.flush();
            outputStream.close();

            // Get HTTP response
            int statusCode =
                    connection.getResponseCode();

            // Read response from the correct stream
            InputStream responseStream;

            if (statusCode >= 200 && statusCode < 300) {
                responseStream = connection.getInputStream();
            } else {
                responseStream = connection.getErrorStream();

                if (responseStream == null) {
                    responseStream = connection.getInputStream();
                }
            }

            String responseBody =
                    readResponse(responseStream);

            // Successful response
            if (statusCode >= 200 && statusCode < 300) {

                System.out.println(
                        "Email sent successfully to: " + to
                );

                System.out.println(
                        "Resend HTTP Status: " + statusCode
                );

                System.out.println(
                        "Resend response: " + responseBody
                );

            } else {

                System.err.println(
                        "Resend email failed."
                );

                System.err.println(
                        "HTTP Status: " + statusCode
                );

                System.err.println(
                        "Resend response: " + responseBody
                );

                throw new IOException(
                        "Email sending failed. Resend returned HTTP "
                        + statusCode
                        + ": "
                        + responseBody
                );
            }

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Read HTTP response as String.
     */
    private static String readResponse(
            InputStream inputStream) throws IOException {

        if (inputStream == null) {
            return "";
        }

        StringBuilder response =
                new StringBuilder();

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                inputStream,
                                StandardCharsets.UTF_8
                        )
                );

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }

    /**
     * Escape special characters for JSON.
     */
    private static String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}

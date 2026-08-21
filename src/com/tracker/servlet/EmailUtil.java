package com.tracker.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class EmailUtil {

    public static void sendEmail(
            String to,
            String subject,
            String messageText) throws Exception {

        // Resend API key from Render environment variables
        String apiKey = System.getenv("RESEND_API_KEY");

        // Email address/domain configured in Resend
        String fromEmail = System.getenv("RESEND_FROM_EMAIL");

        // Check API key
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception(
                    "RESEND_API_KEY environment variable is missing."
            );
        }

        // Check sender email
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            throw new Exception(
                    "RESEND_FROM_EMAIL environment variable is missing."
            );
        }

        if (to == null || to.trim().isEmpty()) {
            throw new Exception("Recipient email is missing.");
        }

        if (subject == null) {
            subject = "";
        }

        if (messageText == null) {
            messageText = "";
        }

        /*
         * Convert the email content to JSON-safe strings.
         */
        String jsonSubject = escapeJson(subject);
        String jsonText = escapeJson(messageText);
        String jsonTo = escapeJson(to);
        String jsonFrom = escapeJson(fromEmail);

        /*
         * Resend API request body.
         */
        String jsonBody =
                "{"
                + "\"from\":\"" + jsonFrom + "\","
                + "\"to\":[\"" + jsonTo + "\"],"
                + "\"subject\":\"" + jsonSubject + "\","
                + "\"text\":\"" + jsonText + "\""
                + "}";

        /*
         * Create HTTP client.
         */
        HttpClient client = HttpClient.newHttpClient();

        /*
         * Create POST request.
         */
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                jsonBody,
                                StandardCharsets.UTF_8
                        )
                )
                .build();

        /*
         * Send request to Resend.
         */
        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        /*
         * Check response.
         */
        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            System.out.println(
                    "Email sent successfully to: " + to
            );

            System.out.println(
                    "Resend response: " + response.body()
            );

        } else {

            System.err.println(
                    "Resend email failed."
            );

            System.err.println(
                    "HTTP Status: " + response.statusCode()
            );

            System.err.println(
                    "Response: " + response.body()
            );

            throw new IOException(
                    "Email sending failed. Resend returned HTTP "
                    + response.statusCode()
                    + ": "
                    + response.body()
            );
        }
    }

    /*
     * Escape characters that have special meaning in JSON.
     */
    private static String escapeJson(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
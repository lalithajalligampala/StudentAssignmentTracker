package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter out = response.getWriter();

        // Validate input
        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            showErrorPage(
                    out,
                    "Registration Failed",
                    "All fields are required.",
                    "register.html",
                    "Go Back"
            );

            return;
        }

        String sql =
                "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        Connection connection = null;
        PreparedStatement statement = null;

        try {

            connection = DBConnection.getConnection();

            if (connection == null) {

                showErrorPage(
                        out,
                        "Database Connection Failed",
                        "Unable to connect to the database.",
                        "register.html",
                        "Try Again"
                );

                return;
            }

            statement = connection.prepareStatement(sql);

            statement.setString(1, name.trim());
            statement.setString(2, email.trim());
            statement.setString(3, password);

            int result = statement.executeUpdate();

            if (result > 0) {

                showSuccessPage(
                        out,
                        name.trim()
                );

            } else {

                showErrorPage(
                        out,
                        "Registration Failed",
                        "The account could not be created.",
                        "register.html",
                        "Try Again"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            showErrorPage(
                    out,
                    "Registration Failed",
                    "Email may already be registered.",
                    "register.html",
                    "Try Again"
            );

        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // =========================================================
    // SUCCESS PAGE
    // =========================================================

    private void showSuccessPage(PrintWriter out, String name) {

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Registration Successful</title>");

        out.println("<style>");

        out.println("* {");
        out.println("    box-sizing: border-box;");
        out.println("}");

        out.println("body {");
        out.println("    margin: 0;");
        out.println("    font-family: Arial, Helvetica, sans-serif;");
        out.println("    background-color: #f4f6f8;");
        out.println("    color: #1f2937;");
        out.println("}");

        /* Header */
        out.println(".header {");
        out.println("    background-color: #2c4054;");
        out.println("    color: white;");
        out.println("    text-align: center;");
        out.println("    padding: 32px 20px;");
        out.println("}");

        out.println(".header h1 {");
        out.println("    margin: 0;");
        out.println("    font-size: 38px;");
        out.println("}");

        /* Main container */
        out.println(".container {");
        out.println("    width: 90%;");
        out.println("    max-width: 850px;");
        out.println("    margin: 55px auto;");
        out.println("}");

        /* Card */
        out.println(".card {");
        out.println("    background-color: white;");
        out.println("    border-radius: 12px;");
        out.println("    padding: 45px;");
        out.println("    box-shadow: 0 4px 14px rgba(0, 0, 0, 0.12);");
        out.println("    text-align: center;");
        out.println("}");

        /* Success icon */
        out.println(".success-icon {");
        out.println("    width: 80px;");
        out.println("    height: 80px;");
        out.println("    margin: 0 auto 25px auto;");
        out.println("    background-color: #22b463;");
        out.println("    color: white;");
        out.println("    border-radius: 50%;");
        out.println("    display: flex;");
        out.println("    align-items: center;");
        out.println("    justify-content: center;");
        out.println("    font-size: 42px;");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".card h2 {");
        out.println("    color: #234b70;");
        out.println("    font-size: 30px;");
        out.println("    margin-bottom: 20px;");
        out.println("}");

        out.println(".card p {");
        out.println("    font-size: 18px;");
        out.println("    margin: 12px 0;");
        out.println("    line-height: 1.6;");
        out.println("}");

        /* Buttons */
        out.println(".button {");
        out.println("    display: inline-block;");
        out.println("    text-decoration: none;");
        out.println("    color: white;");
        out.println("    font-size: 17px;");
        out.println("    font-weight: bold;");
        out.println("    padding: 14px 30px;");
        out.println("    border-radius: 6px;");
        out.println("    margin-top: 25px;");
        out.println("}");

        out.println(".login-button {");
        out.println("    background-color: #3498db;");
        out.println("}");

        out.println(".login-button:hover {");
        out.println("    background-color: #2980b9;");
        out.println("}");

        out.println(".home-button {");
        out.println("    background-color: #2c4054;");
        out.println("    margin-left: 10px;");
        out.println("}");

        out.println(".home-button:hover {");
        out.println("    background-color: #1f2f40;");
        out.println("}");

        /* Responsive */
        out.println("@media (max-width: 600px) {");

        out.println("    .header h1 {");
        out.println("        font-size: 28px;");
        out.println("    }");

        out.println("    .container {");
        out.println("        width: 94%;");
        out.println("        margin: 30px auto;");
        out.println("    }");

        out.println("    .card {");
        out.println("        padding: 30px 20px;");
        out.println("    }");

        out.println("    .card h2 {");
        out.println("        font-size: 25px;");
        out.println("    }");

        out.println("    .button {");
        out.println("        display: block;");
        out.println("        margin: 15px 0 0 0;");
        out.println("        width: 100%;");
        out.println("    }");

        out.println("}");

        out.println("</style>");

        out.println("</head>");

        out.println("<body>");

        // Header
        out.println("<div class='header'>");
        out.println("<h1>Student Assignment &amp; Deadline Tracker</h1>");
        out.println("</div>");

        // Main
        out.println("<div class='container'>");

        out.println("<div class='card'>");

        // Green check mark
        out.println("<div class='success-icon'>");
        out.println("&#10003;");
        out.println("</div>");

        out.println("<h2>Registration Successful!</h2>");

        out.println("<p>Welcome, <strong>"
                + escapeHtml(name)
                + "</strong>!</p>");

        out.println("<p>Your account has been created successfully.</p>");

        out.println("<a class='button login-button' href='login.html'>");
        out.println("Go to Login");
        out.println("</a>");

        out.println("<a class='button home-button' href='index.html'>");
        out.println("Back to Home");
        out.println("</a>");

        out.println("</div>");

        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }


    // =========================================================
    // ERROR PAGE
    // =========================================================

    private void showErrorPage(PrintWriter out,
                               String title,
                               String message,
                               String link,
                               String linkText) {

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");

        out.println("<head>");

        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");

        out.println("<title>" + escapeHtml(title) + "</title>");

        out.println("<style>");

        out.println("* {");
        out.println("    box-sizing: border-box;");
        out.println("}");

        out.println("body {");
        out.println("    margin: 0;");
        out.println("    font-family: Arial, Helvetica, sans-serif;");
        out.println("    background-color: #f4f6f8;");
        out.println("    color: #1f2937;");
        out.println("}");

        /* Header */
        out.println(".header {");
        out.println("    background-color: #2c4054;");
        out.println("    color: white;");
        out.println("    text-align: center;");
        out.println("    padding: 32px 20px;");
        out.println("}");

        out.println(".header h1 {");
        out.println("    margin: 0;");
        out.println("    font-size: 38px;");
        out.println("}");

        /* Container */
        out.println(".container {");
        out.println("    width: 90%;");
        out.println("    max-width: 850px;");
        out.println("    margin: 55px auto;");
        out.println("}");

        /* Card */
        out.println(".card {");
        out.println("    background-color: white;");
        out.println("    border-radius: 12px;");
        out.println("    padding: 45px;");
        out.println("    box-shadow: 0 4px 14px rgba(0, 0, 0, 0.12);");
        out.println("    text-align: center;");
        out.println("}");

        /* Error icon */
        out.println(".error-icon {");
        out.println("    width: 80px;");
        out.println("    height: 80px;");
        out.println("    margin: 0 auto 25px auto;");
        out.println("    background-color: #e74c3c;");
        out.println("    color: white;");
        out.println("    border-radius: 50%;");
        out.println("    display: flex;");
        out.println("    align-items: center;");
        out.println("    justify-content: center;");
        out.println("    font-size: 42px;");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".card h2 {");
        out.println("    color: #234b70;");
        out.println("    font-size: 30px;");
        out.println("    margin-bottom: 20px;");
        out.println("}");

        out.println(".card p {");
        out.println("    font-size: 18px;");
        out.println("    line-height: 1.6;");
        out.println("}");

        /* Button */
        out.println(".button {");
        out.println("    display: inline-block;");
        out.println("    text-decoration: none;");
        out.println("    color: white;");
        out.println("    background-color: #3498db;");
        out.println("    font-size: 17px;");
        out.println("    font-weight: bold;");
        out.println("    padding: 14px 30px;");
        out.println("    border-radius: 6px;");
        out.println("    margin-top: 25px;");
        out.println("}");

        out.println(".button:hover {");
        out.println("    background-color: #2980b9;");
        out.println("}");

        /* Responsive */
        out.println("@media (max-width: 600px) {");

        out.println("    .header h1 {");
        out.println("        font-size: 28px;");
        out.println("    }");

        out.println("    .card {");
        out.println("        padding: 30px 20px;");
        out.println("    }");

        out.println("    .card h2 {");
        out.println("        font-size: 25px;");
        out.println("    }");

        out.println("    .button {");
        out.println("        display: block;");
        out.println("        width: 100%;");
        out.println("    }");

        out.println("}");

        out.println("</style>");

        out.println("</head>");

        out.println("<body>");

        // Header
        out.println("<div class='header'>");
        out.println("<h1>Student Assignment &amp; Deadline Tracker</h1>");
        out.println("</div>");

        // Main
        out.println("<div class='container'>");

        out.println("<div class='card'>");

        // Error icon
        out.println("<div class='error-icon'>");
        out.println("!");
        out.println("</div>");

        out.println("<h2>" + escapeHtml(title) + "</h2>");

        out.println("<p>" + escapeHtml(message) + "</p>");

        out.println("<a class='button' href='"
                + escapeHtml(link)
                + "'>"
                + escapeHtml(linkText)
                + "</a>");

        out.println("</div>");

        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }


    // =========================================================
    // HTML ESCAPE METHOD
    // =========================================================

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

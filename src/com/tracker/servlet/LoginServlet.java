package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter out = response.getWriter();

        // --------------------------------------------------
        // Validate input
        // --------------------------------------------------

        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Login Error</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h2>Login Failed</h2>");
            out.println("<p>Email and password are required.</p>");
            out.println("<a href='login.html'>Try Again</a>");

            out.println("</body>");
            out.println("</html>");

            return;
        }

        email = email.trim();

        // --------------------------------------------------
        // SQL query
        // --------------------------------------------------

        String sql =
                "SELECT id, name, email, password " +
                "FROM users " +
                "WHERE email = ?";

        // --------------------------------------------------
        // Database operation
        // --------------------------------------------------

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    String storedPassword =
                            resultSet.getString("password");

                    // --------------------------------------------------
                    // Check password
                    // --------------------------------------------------

                    if (storedPassword != null
                            && storedPassword.equals(password)) {

                        int userId =
                                resultSet.getInt("id");

                        String userName =
                                resultSet.getString("name");

                        String userEmail =
                                resultSet.getString("email");

                        // --------------------------------------------------
                        // Remove old session
                        // --------------------------------------------------

                        HttpSession oldSession =
                                request.getSession(false);

                        if (oldSession != null) {
                            oldSession.invalidate();
                        }

                        // --------------------------------------------------
                        // Create new session
                        // --------------------------------------------------

                        HttpSession session =
                                request.getSession(true);

                        // --------------------------------------------------
                        // Store logged-in user information
                        // --------------------------------------------------

                        session.setAttribute(
                                "userId",
                                userId
                        );

                        session.setAttribute(
                                "userName",
                                userName
                        );

                        session.setAttribute(
                                "userEmail",
                                userEmail
                        );

                        // --------------------------------------------------
                        // Login successful
                        // --------------------------------------------------

                        System.out.println(
                                "Login successful for user ID: "
                                        + userId
                        );

                        response.sendRedirect("Dashboard");

                        return;

                    } else {

                        showLoginFailed(out);
                        return;
                    }

                } else {

                    showLoginFailed(out);
                    return;
                }
            }

        } catch (Exception e) {

            // --------------------------------------------------
            // IMPORTANT:
            // Show the REAL error while debugging.
            // --------------------------------------------------

            e.printStackTrace();

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");
            out.println("<title>Login Error</title>");

            out.println("<style>");

            out.println("body {");
            out.println("font-family: Arial, sans-serif;");
            out.println("background-color: #f4f6f8;");
            out.println("padding: 50px;");
            out.println("}");

            out.println(".box {");
            out.println("background: white;");
            out.println("max-width: 700px;");
            out.println("margin: auto;");
            out.println("padding: 30px;");
            out.println("border-radius: 10px;");
            out.println("box-shadow: 0 2px 8px rgba(0,0,0,0.12);");
            out.println("}");

            out.println("h2 {");
            out.println("color: #c0392b;");
            out.println("}");

            out.println(".error {");
            out.println("background: #fce4e4;");
            out.println("padding: 15px;");
            out.println("border-radius: 6px;");
            out.println("word-wrap: break-word;");
            out.println("}");

            out.println("a {");
            out.println("display: inline-block;");
            out.println("margin-top: 20px;");
            out.println("padding: 12px 20px;");
            out.println("background: #2c3e50;");
            out.println("color: white;");
            out.println("text-decoration: none;");
            out.println("border-radius: 6px;");
            out.println("}");

            out.println("</style>");
            out.println("</head>");

            out.println("<body>");

            out.println("<div class='box'>");

            out.println("<h2>Login Error</h2>");

            out.println(
                    "<p>Something went wrong while processing your login.</p>"
            );

            out.println(
                    "<p class='error'><b>Actual Error:</b> "
                            + escapeHtml(e.getMessage())
                            + "</p>"
            );

            out.println(
                    "<a href='login.html'>Try Again</a>"
            );

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    // --------------------------------------------------
    // Display invalid login
    // --------------------------------------------------

    private void showLoginFailed(PrintWriter out) {

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");
        out.println("<title>Login Failed</title>");

        out.println("<style>");

        out.println("body {");
        out.println("font-family: Arial, sans-serif;");
        out.println("background-color: #f4f6f8;");
        out.println("text-align: center;");
        out.println("padding-top: 100px;");
        out.println("}");

        out.println(".box {");
        out.println("background: white;");
        out.println("width: 500px;");
        out.println("max-width: 90%;");
        out.println("margin: auto;");
        out.println("padding: 40px;");
        out.println("border-radius: 15px;");
        out.println("box-shadow: 0 5px 18px rgba(0,0,0,0.12);");
        out.println("}");

        out.println("h2 {");
        out.println("color: #e74c3c;");
        out.println("}");

        out.println("a {");
        out.println("display: inline-block;");
        out.println("margin-top: 20px;");
        out.println("padding: 12px 25px;");
        out.println("background-color: #3498db;");
        out.println("color: white;");
        out.println("text-decoration: none;");
        out.println("border-radius: 7px;");
        out.println("font-weight: bold;");
        out.println("}");

        out.println("</style>");
        out.println("</head>");

        out.println("<body>");

        out.println("<div class='box'>");

        out.println("<h2>Login Failed</h2>");
        out.println("<p>Invalid email or password.</p>");

        out.println(
                "<a href='login.html'>Try Again</a>"
        );

        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    // --------------------------------------------------
    // Prevent HTML characters in database error messages
    // --------------------------------------------------

    private String escapeHtml(String value) {

        if (value == null) {
            return "Unknown database error";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}


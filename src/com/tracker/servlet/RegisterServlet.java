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

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // ==================================================
        // GET REGISTRATION DETAILS
        // ==================================================

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter out = response.getWriter();

        // ==================================================
        // 1. VALIDATE INPUT
        // ==================================================

        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            out.println("<html><body>");
            out.println("<h2>Registration Failed</h2>");
            out.println("<p>Name, email and password are required.</p>");
            out.println("<a href='register.html'>Try Again</a>");
            out.println("</body></html>");

            return;
        }

        name = name.trim();
        email = email.trim();

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement checkStatement = null;
        ResultSet checkResult = null;

        try {

            // ==================================================
            // 2. CONNECT TO AIVEN DATABASE
            // ==================================================

            connection = DBConnection.getConnection();

            if (connection == null) {

                out.println("<html><body>");
                out.println("<h2>Database Connection Failed</h2>");
                out.println("<p>Unable to connect to the database.</p>");
                out.println("<a href='register.html'>Try Again</a>");
                out.println("</body></html>");

                return;
            }

            // ==================================================
            // 3. CHECK WHETHER EMAIL ALREADY EXISTS
            // ==================================================

            String checkSql =
                    "SELECT id FROM users WHERE email = ?";

            checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, email);

            checkResult =
                    checkStatement.executeQuery();

            if (checkResult.next()) {

                out.println("<html><body>");
                out.println("<h2>Email Already Registered</h2>");
                out.println("<p>This email address is already registered.</p>");
                out.println("<a href='register.html'>Try Again</a>");
                out.println("</body></html>");

                return;
            }

            // ==================================================
            // 4. INSERT USER INTO AIVEN DATABASE
            // ==================================================

            String sql =
                    "INSERT INTO users (name, email, password) "
                    + "VALUES (?, ?, ?)";

            statement =
                    connection.prepareStatement(sql);

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);

            int rowsInserted =
                    statement.executeUpdate();

            // ==================================================
            // 5. CHECK REGISTRATION
            // ==================================================

            if (rowsInserted > 0) {

                boolean emailSent = false;
                String emailError = null;

                // ==================================================
                // 6. SEND REGISTRATION EMAIL
                // ==================================================

                try {

                    String subject =
                            "Student Assignment Tracker - Account Created";

                    /*
                     * IMPORTANT:
                     *
                     * The email address is ONLY used as the
                     * recipient.
                     *
                     * The email BODY contains:
                     * 1. Name
                     * 2. Password
                     *
                     * We are NOT displaying the email as username.
                     */

                    String message =
                            "Hello " + name + ",\n\n"

                            + "Your Student Assignment Tracker account "
                            + "has been created successfully.\n\n"

                            + "Your registration details are:\n\n"

                            + "Name: " + name + "\n"
                            + "Password: " + password + "\n\n"

                            + "You can now log in to the "
                            + "Student Assignment Tracker using "
                            + "your registered email address.\n\n"

                            + "Please keep your password secure.\n\n"

                            + "Regards,\n"
                            + "Student Assignment Tracker Team";


                    /*
                     * EMAIL IS SENT TO THE REGISTERED EMAIL.
                     *
                     * Example:
                     *
                     * email = krishna@gmail.com
                     *
                     * The email goes TO:
                     * krishna@gmail.com
                     *
                     * But the email BODY contains:
                     *
                     * Name: Krishna
                     * Password: ********
                     */

                    EmailUtil.sendEmail(
                            email,
                            subject,
                            message
                    );

                    emailSent = true;

                    System.out.println(
                            "Registration email sent successfully to: "
                            + email
                    );

                } catch (Exception emailException) {

                    emailError =
                            emailException.getMessage();

                    System.err.println(
                            "Registration email could not be sent."
                    );

                    emailException.printStackTrace();
                }

                // ==================================================
                // 7. REGISTRATION SUCCESS PAGE
                // ==================================================

                out.println("<html>");
                out.println("<head>");

                out.println("<title>Registration Successful</title>");

                out.println("<style>");

                out.println("body {");
                out.println("font-family: Arial, sans-serif;");
                out.println("background-color: #f4f6f8;");
                out.println("text-align: center;");
                out.println("padding-top: 80px;");
                out.println("}");

                out.println(".box {");
                out.println("background: white;");
                out.println("width: 550px;");
                out.println("max-width: 90%;");
                out.println("margin: auto;");
                out.println("padding: 40px;");
                out.println("border-radius: 15px;");
                out.println("box-shadow: 0 5px 18px rgba(0,0,0,0.12);");
                out.println("}");

                out.println("h2 {");
                out.println("color: #27ae60;");
                out.println("}");

                out.println(".success {");
                out.println("background-color: #d4edda;");
                out.println("color: #155724;");
                out.println("padding: 15px;");
                out.println("border-radius: 8px;");
                out.println("margin-top: 20px;");
                out.println("}");

                out.println(".warning {");
                out.println("background-color: #fff3cd;");
                out.println("color: #856404;");
                out.println("padding: 15px;");
                out.println("border-radius: 8px;");
                out.println("margin-top: 20px;");
                out.println("}");

                out.println("a {");
                out.println("display: inline-block;");
                out.println("margin-top: 20px;");
                out.println("padding: 12px 25px;");
                out.println("background-color: #2c3e50;");
                out.println("color: white;");
                out.println("text-decoration: none;");
                out.println("border-radius: 7px;");
                out.println("font-weight: bold;");
                out.println("}");

                out.println("</style>");

                out.println("</head>");

                out.println("<body>");

                out.println("<div class='box'>");

                out.println(
                        "<h2>Registration Successful!</h2>"
                );

                out.println(
                        "<p>Your account has been created successfully.</p>"
                );

                // ==================================================
                // 8. EMAIL STATUS
                // ==================================================

                if (emailSent) {

                    out.println("<div class='success'>");

                    out.println(
                            "<b>Confirmation email sent successfully!</b>"
                    );

                    out.println(
                            "<p>Your name and password have been sent to:</p>"
                    );

                    out.println(
                            "<b>" + email + "</b>"
                    );

                    out.println("</div>");

                } else {

                    out.println("<div class='warning'>");

                    out.println(
                            "<b>Account created, but email could not be sent.</b>"
                    );

                    out.println(
                            "<p>You can still log in using your "
                            + "registered email and password.</p>"
                    );

                    if (emailError != null) {

                        out.println(
                                "<p>Email service error: "
                                + emailError
                                + "</p>"
                        );
                    }

                    out.println("</div>");
                }

                // ==================================================
                // 9. LOGIN LINK
                // ==================================================

                out.println(
                        "<a href='login.html'>Go to Login</a>"
                );

                out.println("</div>");

                out.println("</body>");

                out.println("</html>");

            } else {

                out.println("<html><body>");

                out.println(
                        "<h2>Registration Failed</h2>"
                );

                out.println(
                        "<p>Unable to create your account.</p>"
                );

                out.println(
                        "<a href='register.html'>Try Again</a>"
                );

                out.println("</body></html>");
            }

        } catch (Exception e) {

            e.printStackTrace();

            // ==================================================
            // DATABASE / OTHER ERROR
            // ==================================================

            out.println("<html>");

            out.println("<head>");

            out.println("<title>Registration Error</title>");

            out.println("<style>");

            out.println("body {");
            out.println("font-family: Arial, sans-serif;");
            out.println("background-color: #f4f6f8;");
            out.println("padding: 60px;");
            out.println("}");

            out.println(".box {");
            out.println("background: white;");
            out.println("max-width: 900px;");
            out.println("margin: auto;");
            out.println("padding: 35px;");
            out.println("border-radius: 15px;");
            out.println("box-shadow: 0 5px 18px rgba(0,0,0,0.12);");
            out.println("}");

            out.println("h2 {");
            out.println("color: #e74c3c;");
            out.println("}");

            out.println(".error {");
            out.println("background-color: #fde2e2;");
            out.println("padding: 15px;");
            out.println("border-radius: 8px;");
            out.println("word-wrap: break-word;");
            out.println("}");

            out.println("a {");
            out.println("display: inline-block;");
            out.println("margin-top: 20px;");
            out.println("padding: 12px 25px;");
            out.println("background-color: #2c3e50;");
            out.println("color: white;");
            out.println("text-decoration: none;");
            out.println("border-radius: 7px;");
            out.println("}");

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='box'>");

            out.println("<h2>Registration Error</h2>");

            out.println(
                    "<p>Something went wrong while processing your registration.</p>"
            );

            out.println("<div class='error'>");

            out.println("<b>Actual Error:</b> ");

            out.println(e.getMessage());

            out.println("</div>");

            out.println(
                    "<a href='register.html'>Try Again</a>"
            );

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

        } finally {

            // ==================================================
            // 10. CLOSE DATABASE RESOURCES
            // ==================================================

            try {

                if (checkResult != null) {
                    checkResult.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                if (checkStatement != null) {
                    checkStatement.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

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
}
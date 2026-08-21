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

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Registration Error</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h2>Registration Failed</h2>");
            out.println("<p>Name, email and password are required.</p>");
            out.println("<a href='register.html'>Try Again</a>");

            out.println("</body>");
            out.println("</html>");

            return;
        }

        name = name.trim();
        email = email.trim();

        Connection connection = null;
        PreparedStatement statement = null;

        try {

            connection = DBConnection.getConnection();

            if (connection == null) {

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Database Error</title>");
                out.println("</head>");
                out.println("<body>");

                out.println("<h2>Database Connection Failed</h2>");
                out.println("<p>Unable to connect to the database.</p>");
                out.println("<a href='register.html'>Try Again</a>");

                out.println("</body>");
                out.println("</html>");

                return;
            }

            // Check whether email already exists
            String checkSql =
                    "SELECT id FROM users WHERE email = ?";

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, email);

            java.sql.ResultSet checkResult =
                    checkStatement.executeQuery();

            if (checkResult.next()) {

                checkResult.close();
                checkStatement.close();

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Registration Failed</title>");

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

                out.println("<h2>Email Already Registered</h2>");
                out.println("<p>This email address is already registered.</p>");
                out.println("<a href='register.html'>Try Again</a>");

                out.println("</div>");

                out.println("</body>");
                out.println("</html>");

                return;
            }

            checkResult.close();
            checkStatement.close();

            // Insert new user
            String sql =
                    "INSERT INTO users (name, email, password) " +
                    "VALUES (?, ?, ?)";

            statement = connection.prepareStatement(sql);

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {

                /*
                 * Registration successful.
                 *
                 * Send the registered user's details
                 * to their email address.
                 */
                String subject =
                        "Student Assignment Tracker - Registration Successful";

                String message =
                        "Hello " + name + ",\n\n"
                        + "Your Student Assignment Tracker account "
                        + "has been created successfully.\n\n"
                        + "Your login details are:\n\n"
                        + "Name: " + name + "\n"
                        + "Email: " + email + "\n"
                        + "Password: " + password + "\n\n"
                        + "Please keep this email safe for future reference.\n\n"
                        + "Regards,\n"
                        + "Student Assignment Tracker Team";

                // Send email
                EmailUtil.sendEmail(
                        email,
                        subject,
                        message
                );

                // Show success page
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

                out.println("p {");
                out.println("font-size: 17px;");
                out.println("line-height: 1.6;");
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

                out.println("<h2>Registration Successful!</h2>");

                out.println("<p>Your account has been created successfully.</p>");

                out.println("<p>");
                out.println("Your login details have been sent to your registered email address.");
                out.println("</p>");

                out.println("<a href='login.html'>Go to Login</a>");

                out.println("</div>");

                out.println("</body>");
                out.println("</html>");

            } else {

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Registration Failed</title>");
                out.println("</head>");

                out.println("<body>");

                out.println("<h2>Registration Failed</h2>");
                out.println("<p>Unable to create your account.</p>");
                out.println("<a href='register.html'>Try Again</a>");

                out.println("</body>");
                out.println("</html>");
            }

        } catch (Exception e) {

            e.printStackTrace();

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

            out.println("<p>Something went wrong while processing your registration.</p>");

            out.println("<div class='error'>");
            out.println("<b>Actual Error:</b> ");
            out.println(e.getMessage());
            out.println("</div>");

            out.println("<a href='register.html'>Try Again</a>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

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
}

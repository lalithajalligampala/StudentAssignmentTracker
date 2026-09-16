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

        // ==================================================
        // RESPONSE TYPE
        // ==================================================

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();


        // ==================================================
        // 1. GET REGISTRATION DETAILS
        // ==================================================

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");


        // ==================================================
        // 2. VALIDATE INPUT
        // ==================================================

        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            out.print("Name, email and password are required.");
            return;
        }


        // Remove unnecessary spaces

        name = name.trim();
        email = email.trim().toLowerCase();


        Connection connection = null;
        PreparedStatement checkStatement = null;
        PreparedStatement statement = null;
        ResultSet checkResult = null;


        try {

            // ==================================================
            // 3. CONNECT TO DATABASE
            // ==================================================

            connection = DBConnection.getConnection();


            if (connection == null) {

                out.print("Database connection failed.");
                return;
            }


            // ==================================================
            // 4. CHECK WHETHER EMAIL ALREADY EXISTS
            // ==================================================

            String checkSql =
                    "SELECT id FROM users WHERE email = ?";

            checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, email);

            checkResult =
                    checkStatement.executeQuery();


            if (checkResult.next()) {

                out.print("Email Already Registered");
                return;
            }


            // ==================================================
            // 5. INSERT USER INTO DATABASE
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
            // 6. REGISTRATION RESULT
            // ==================================================

            if (rowsInserted > 0) {

                /*
                 * IMPORTANT:
                 *
                 * Do NOT send email from Java here.
                 *
                 * EmailJS in register.html will send
                 * the registration email.
                 *
                 * register.html is waiting for exactly:
                 *
                 * SUCCESS
                 */

                System.out.println(
                        "User registered successfully: " + email
                );

                out.print("SUCCESS");

            } else {

                out.print("Registration failed.");

            }


        } catch (Exception e) {

            // ==================================================
            // 7. ERROR
            // ==================================================

            e.printStackTrace();

            out.print(
                    "Registration error: "
                    + e.getMessage()
            );


        } finally {

            // ==================================================
            // 8. CLOSE RESULT SET
            // ==================================================

            try {

                if (checkResult != null) {
                    checkResult.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            // ==================================================
            // 9. CLOSE CHECK STATEMENT
            // ==================================================

            try {

                if (checkStatement != null) {
                    checkStatement.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            // ==================================================
            // 10. CLOSE INSERT STATEMENT
            // ==================================================

            try {

                if (statement != null) {
                    statement.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            // ==================================================
            // 11. CLOSE DATABASE CONNECTION
            // ==================================================

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
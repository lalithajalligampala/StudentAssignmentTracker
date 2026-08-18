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

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter out = response.getWriter();

        // Validate input
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

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

        String sql =
                "SELECT id, name, email FROM users WHERE email = ? AND password = ?";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            connection = DBConnection.getConnection();

            if (connection == null) {

                out.println("<html>");
                out.println("<head><title>Database Error</title></head>");
                out.println("<body>");

                out.println("<h2>Database Connection Failed</h2>");
                out.println("<p>Unable to connect to the database.</p>");
                out.println("<a href='login.html'>Try Again</a>");

                out.println("</body>");
                out.println("</html>");

                return;
            }

            statement = connection.prepareStatement(sql);

            statement.setString(1, email.trim());
            statement.setString(2, password);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {

                // Create session
                HttpSession session = request.getSession();

                session.setAttribute("userId", resultSet.getInt("id"));
                session.setAttribute("userName", resultSet.getString("name"));
                session.setAttribute("userEmail", resultSet.getString("email"));

                // Redirect to dashboard
                response.sendRedirect("Dashboard");

            } else {

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
                out.println("<a href='login.html'>Try Again</a>");

                out.println("</div>");

                out.println("</body>");

                out.println("</html>");
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<html>");

            out.println("<head>");
            out.println("<title>Login Error</title>");
            out.println("</head>");

            out.println("<body>");

            out.println("<h2>Login Error</h2>");
            out.println("<p>Something went wrong while processing your login.</p>");
            out.println("<a href='login.html'>Try Again</a>");

            out.println("</body>");

            out.println("</html>");

        } finally {

            try {
                if (resultSet != null) {
                    resultSet.close();
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
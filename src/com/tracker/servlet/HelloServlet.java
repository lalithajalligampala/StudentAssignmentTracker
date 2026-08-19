package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String contextPath = request.getContextPath();

        // Get logged-in user's session
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {

            response.sendRedirect(
                contextPath + "/login.html"
            );

            return;
        }

        int userId;

        try {

            userId = (Integer) session.getAttribute("userId");

        } catch (Exception e) {

            response.sendRedirect(
                contextPath + "/login.html"
            );

            return;
        }

        // Get form values
        String assignment =
                request.getParameter("assignmentName");

        String subject =
                request.getParameter("subject");

        String deadline =
                request.getParameter("deadline");

        String priority =
                request.getParameter("priority");

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Assignment Added</title>");

        out.println("<style>");

        out.println("body {");
        out.println("    font-family: Arial, sans-serif;");
        out.println("    margin: 0;");
        out.println("    background-color: #f4f6f8;");
        out.println("    color: #333;");
        out.println("}");

        out.println(".header {");
        out.println("    background-color: #2c3e50;");
        out.println("    color: white;");
        out.println("    padding: 25px;");
        out.println("    text-align: center;");
        out.println("}");

        out.println(".header h1 {");
        out.println("    margin: 0;");
        out.println("    font-size: 30px;");
        out.println("}");

        out.println(".container {");
        out.println("    width: 90%;");
        out.println("    max-width: 600px;");
        out.println("    margin: 50px auto;");
        out.println("}");

        out.println(".card {");
        out.println("    background-color: white;");
        out.println("    padding: 35px;");
        out.println("    border-radius: 10px;");
        out.println("    box-shadow: 0 2px 8px rgba(0,0,0,0.12);");
        out.println("    text-align: center;");
        out.println("}");

        out.println(".success {");
        out.println("    color: #27ae60;");
        out.println("    font-size: 26px;");
        out.println("    margin-bottom: 25px;");
        out.println("}");

        out.println(".details {");
        out.println("    text-align: left;");
        out.println("    background-color: #f8f9fa;");
        out.println("    padding: 20px;");
        out.println("    border-radius: 8px;");
        out.println("    margin-bottom: 25px;");
        out.println("}");

        out.println(".details p {");
        out.println("    margin: 10px 0;");
        out.println("}");

        out.println(".button {");
        out.println("    display: inline-block;");
        out.println("    padding: 12px 20px;");
        out.println("    margin: 5px;");
        out.println("    background-color: #2c3e50;");
        out.println("    color: white;");
        out.println("    text-decoration: none;");
        out.println("    border-radius: 6px;");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".button:hover {");
        out.println("    background-color: #1f2d3a;");
        out.println("}");

        out.println(".error {");
        out.println("    color: #c0392b;");
        out.println("}");

        out.println("</style>");
        out.println("</head>");

        out.println("<body>");

        out.println("<div class='header'>");
        out.println(
            "<h1>Student Assignment &amp; Deadline Tracker</h1>"
        );
        out.println("</div>");

        out.println("<div class='container'>");
        out.println("<div class='card'>");

        // Validate form fields
        if (assignment == null || assignment.trim().isEmpty()
                || subject == null || subject.trim().isEmpty()
                || deadline == null || deadline.trim().isEmpty()
                || priority == null || priority.trim().isEmpty()) {

            out.println(
                "<h2 class='error'>Invalid Assignment</h2>"
            );

            out.println(
                "<p>Please fill in all assignment details.</p>"
            );

            out.println(
                "<a class='button' href='" +
                contextPath +
                "/add.html'>" +
                "Back to Add Assignment" +
                "</a>"
            );

            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

            return;
        }

        /*
         * IMPORTANT:
         *
         * user_id is now included in the INSERT.
         */
        String sql =
                "INSERT INTO assignments " +
                "(assignment_name, subject, deadline, priority, user_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement statement = null;

        try {

            connection = DBConnection.getConnection();

            if (connection == null) {

                throw new Exception(
                    "Database connection failed."
                );
            }

            statement =
                    connection.prepareStatement(sql);

            statement.setString(
                    1,
                    assignment.trim()
            );

            statement.setString(
                    2,
                    subject.trim()
            );

            statement.setString(
                    3,
                    deadline.trim()
            );

            statement.setString(
                    4,
                    priority.trim()
            );

            // IMPORTANT:
            // Store the logged-in user's ID.
            statement.setInt(
                    5,
                    userId
            );

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                out.println(
                    "<h2 class='success'>" +
                    "Assignment Saved Successfully!" +
                    "</h2>"
                );

                out.println("<div class='details'>");

                out.println(
                    "<p><b>Assignment:</b> " +
                    assignment.trim() +
                    "</p>"
                );

                out.println(
                    "<p><b>Subject:</b> " +
                    subject.trim() +
                    "</p>"
                );

                out.println(
                    "<p><b>Deadline:</b> " +
                    deadline.trim() +
                    "</p>"
                );

                out.println(
                    "<p><b>Priority:</b> " +
                    priority.trim() +
                    "</p>"
                );

                out.println("</div>");

                out.println(
                    "<a class='button' href='" +
                    contextPath +
                    "/add.html'>" +
                    "Add Another Assignment" +
                    "</a>"
                );

                out.println(
                    "<a class='button' href='" +
                    contextPath +
                    "/ViewAssignments'>" +
                    "View Assignments" +
                    "</a>"
                );

            } else {

                out.println(
                    "<h2 class='error'>" +
                    "Assignment Was Not Saved" +
                    "</h2>"
                );

                out.println(
                    "<a class='button' href='" +
                    contextPath +
                    "/add.html'>" +
                    "Back to Add Assignment" +
                    "</a>"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                "<h2 class='error'>" +
                "Error Saving Assignment" +
                "</h2>"
            );

            out.println(
                "<p>" +
                "Something went wrong while saving " +
                "the assignment." +
                "</p>"
            );

            out.println(
                "<p><b>Error:</b> " +
                e.getMessage() +
                "</p>"
            );

            out.println(
                "<a class='button' href='" +
                contextPath +
                "/add.html'>" +
                "Back to Add Assignment" +
                "</a>"
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

        out.println("</div>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
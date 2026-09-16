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

        // ==================================================
        // GET LOGGED-IN USER'S SESSION
        // ==================================================

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


        // ==================================================
        // GET FORM VALUES
        // ==================================================

        String assignment =
                request.getParameter("assignmentName");

        String subject =
                request.getParameter("subject");

        String deadline =
                request.getParameter("deadline");

        String deadlineTime =
                request.getParameter("deadlineTime");

        String priority =
                request.getParameter("priority");


        // ==================================================
        // HTML PAGE
        // ==================================================

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");

        out.println("<meta charset='UTF-8'>");

        out.println("<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>");

        out.println("<title>Assignment Added</title>");


        // ==================================================
        // CSS
        // ==================================================

        out.println("<style>");

        out.println("* {");
        out.println("box-sizing: border-box;");
        out.println("}");

        // Body
        out.println("body {");
        out.println("font-family: Arial, Helvetica, sans-serif;");
        out.println("margin: 0;");
        out.println("min-height: 100vh;");
        out.println("background: linear-gradient(135deg, #eef6ff, #f4fbfa);");
        out.println("color: #1f2d3d;");
        out.println("}");

        // Decorative background
        out.println("body::before {");
        out.println("content: '';");
        out.println("position: fixed;");
        out.println("width: 420px;");
        out.println("height: 420px;");
        out.println("border-radius: 50%;");
        out.println("background: rgba(74, 144, 226, 0.06);");
        out.println("top: -150px;");
        out.println("left: -150px;");
        out.println("z-index: -1;");
        out.println("}");

        out.println("body::after {");
        out.println("content: '';");
        out.println("position: fixed;");
        out.println("width: 500px;");
        out.println("height: 500px;");
        out.println("border-radius: 50%;");
        out.println("background: rgba(80, 190, 170, 0.06);");
        out.println("bottom: -200px;");
        out.println("right: -180px;");
        out.println("z-index: -1;");
        out.println("}");

        // Header
        out.println(".header {");
        out.println("width: 100%;");
        out.println("background: rgba(255,255,255,0.96);");
        out.println("color: #1d2d44;");
        out.println("padding: 28px 20px;");
        out.println("text-align: center;");
        out.println("border-bottom: 1px solid #d4e3f5;");
        out.println("box-shadow: 0 4px 15px rgba(31,45,61,0.08);");
        out.println("}");

        // Header heading
        out.println(".header h1 {");
        out.println("margin: 0;");
        out.println("font-size: 28px;");
        out.println("font-weight: bold;");
        out.println("color: #1d2d44;");
        out.println("}");

        // Container
        out.println(".container {");
        out.println("width: 90%;");
        out.println("max-width: 650px;");
        out.println("margin: 55px auto;");
        out.println("}");

        // Card
        out.println(".card {");
        out.println("background: rgba(255,255,255,0.96);");
        out.println("padding: 38px;");
        out.println("border-radius: 12px;");
        out.println("border: 1px solid #d4e3f5;");
        out.println("box-shadow: 0 10px 30px rgba(31,45,61,0.10);");
        out.println("text-align: center;");
        out.println("}");

        // Success
        out.println(".success {");
        out.println("color: #3978d8;");
        out.println("font-size: 26px;");
        out.println("margin: 0 0 28px;");
        out.println("font-weight: bold;");
        out.println("}");

        // Details
        out.println(".details {");
        out.println("text-align: left;");
        out.println("background: #eaf3ff;");
        out.println("border: 1px solid #d4e3f5;");
        out.println("padding: 22px;");
        out.println("border-radius: 9px;");
        out.println("margin-bottom: 28px;");
        out.println("}");

        // Details paragraph
        out.println(".details p {");
        out.println("margin: 12px 0;");
        out.println("color: #526579;");
        out.println("font-size: 15px;");
        out.println("line-height: 1.5;");
        out.println("}");

        // Details bold
        out.println(".details b {");
        out.println("color: #1d2d44;");
        out.println("}");

        // Button
        out.println(".button {");
        out.println("display: inline-block;");
        out.println("padding: 12px 20px;");
        out.println("margin: 5px;");
        out.println("background: #3978d8;");
        out.println("color: white;");
        out.println("text-decoration: none;");
        out.println("border-radius: 7px;");
        out.println("font-weight: bold;");
        out.println("font-size: 15px;");
        out.println("border: 1px solid #3978d8;");
        out.println("transition: all 0.2s ease;");
        out.println("box-shadow: 0 5px 12px rgba(57,120,216,0.18);");
        out.println("}");

        // Button hover
        out.println(".button:hover {");
        out.println("background: #2f69c2;");
        out.println("border-color: #2f69c2;");
        out.println("transform: translateY(-1px);");
        out.println("box-shadow: 0 7px 16px rgba(57,120,216,0.25);");
        out.println("}");

        // Dashboard button
        out.println(".dashboard-button {");
        out.println("background: white;");
        out.println("color: #3978d8;");
        out.println("border: 1px solid #3978d8;");
        out.println("box-shadow: none;");
        out.println("}");

        // Dashboard hover
        out.println(".dashboard-button:hover {");
        out.println("background: #3978d8;");
        out.println("color: white;");
        out.println("border-color: #3978d8;");
        out.println("box-shadow: 0 5px 12px rgba(57,120,216,0.18);");
        out.println("}");

        // Error
        out.println(".error {");
        out.println("color: #c0392b;");
        out.println("font-size: 25px;");
        out.println("margin-bottom: 20px;");
        out.println("}");

        // Paragraph
        out.println(".card > p {");
        out.println("color: #526579;");
        out.println("font-size: 15px;");
        out.println("line-height: 1.6;");
        out.println("}");

        // Mobile
        out.println("@media (max-width: 600px) {");

        out.println(".header {");
        out.println("padding: 22px 15px;");
        out.println("}");

        out.println(".header h1 {");
        out.println("font-size: 23px;");
        out.println("}");

        out.println(".container {");
        out.println("width: 92%;");
        out.println("margin: 35px auto;");
        out.println("}");

        out.println(".card {");
        out.println("padding: 28px 22px;");
        out.println("}");

        out.println(".success {");
        out.println("font-size: 22px;");
        out.println("}");

        out.println(".button {");
        out.println("display: block;");
        out.println("width: 100%;");
        out.println("margin: 10px 0;");
        out.println("}");

        out.println("}");

        out.println("</style>");

        out.println("</head>");


        // ==================================================
        // BODY
        // ==================================================

        out.println("<body>");


        // ==================================================
        // HEADER
        // ==================================================

        out.println("<div class='header'>");

        out.println(
            "<h1>Student Assignment &amp; Deadline Tracker</h1>"
        );

        out.println("</div>");


        // ==================================================
        // CONTAINER
        // ==================================================

        out.println("<div class='container'>");

        out.println("<div class='card'>");


        // ==================================================
        // VALIDATE FORM FIELDS
        // ==================================================

        if (assignment == null || assignment.trim().isEmpty()
                || subject == null || subject.trim().isEmpty()
                || deadline == null || deadline.trim().isEmpty()
                || deadlineTime == null || deadlineTime.trim().isEmpty()
                || priority == null || priority.trim().isEmpty()) {

            out.println(
                "<h2 class='error'>Invalid Assignment</h2>"
            );

            out.println(
                "<p>Please fill in all assignment details.</p>"
            );


            // Back to Add Assignment

            out.println(
                "<a class='button' href='" +
                contextPath +
                "/add.html'>" +
                "Back to Add Assignment" +
                "</a>"
            );


            // Back to Dashboard

            out.println(
                "<a class='button dashboard-button' href='" +
                contextPath +
                "/Dashboard'>" +
                "Back to Dashboard" +
                "</a>"
            );


            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

            return;
        }


        // ==================================================
        // INSERT INTO DATABASE
        // ==================================================

        String sql =
                "INSERT INTO assignments " +
                "(assignment_name, subject, deadline, " +
                "deadline_time, priority, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";


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


            // Assignment Name
            statement.setString(
                    1,
                    assignment.trim()
            );


            // Subject
            statement.setString(
                    2,
                    subject.trim()
            );


            // Deadline Date
            statement.setString(
                    3,
                    deadline.trim()
            );


            // Deadline Time
            statement.setString(
                    4,
                    deadlineTime.trim()
            );


            // Priority
            statement.setString(
                    5,
                    priority.trim()
            );


            // Logged-in User
            statement.setInt(
                    6,
                    userId
            );


            int rows =
                    statement.executeUpdate();


            // ==================================================
            // SUCCESS
            // ==================================================

            if (rows > 0) {

                out.println(
                    "<h2 class='success'>" +
                    "Assignment Saved Successfully!" +
                    "</h2>"
                );


                // Assignment Details

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
                    "<p><b>Deadline Date:</b> " +
                    deadline.trim() +
                    "</p>"
                );


                out.println(
                    "<p><b>Deadline Time:</b> " +
                    deadlineTime.trim() +
                    "</p>"
                );


                out.println(
                    "<p><b>Priority:</b> " +
                    priority.trim() +
                    "</p>"
                );


                out.println("</div>");


                // Add Another Assignment

                out.println(
                    "<a class='button' href='" +
                    contextPath +
                    "/add.html'>" +
                    "Add Another Assignment" +
                    "</a>"
                );


                // View Assignments

                out.println(
                    "<a class='button' href='" +
                    contextPath +
                    "/ViewAssignments'>" +
                    "View Assignments" +
                    "</a>"
                );


                // Back to Dashboard

                out.println(
                    "<a class='button dashboard-button' href='" +
                    contextPath +
                    "/Dashboard'>" +
                    "Back to Dashboard" +
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


                out.println(
                    "<a class='button dashboard-button' href='" +
                    contextPath +
                    "/Dashboard'>" +
                    "Back to Dashboard" +
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


            out.println(
                "<a class='button dashboard-button' href='" +
                contextPath +
                "/Dashboard'>" +
                "Back to Dashboard" +
                "</a>"
            );


        } finally {

            // Close Statement

            try {

                if (statement != null) {
                    statement.close();
                }

            } catch (Exception e) {

                e.printStackTrace();

            }


            // Close Connection

            try {

                if (connection != null) {
                    connection.close();
                }

            } catch (Exception e) {

                e.printStackTrace();

            }

        }


        // ==================================================
        // CLOSE HTML
        // ==================================================

        out.println("</div>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
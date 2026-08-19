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

public class AddAssignmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // Get logged-in user's session
        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            out.println("<html><body>");
            out.println("<h2>Login Required</h2>");
            out.println("<p>Please login before adding an assignment.</p>");
            out.println("<a href='login.html'>Go to Login</a>");
            out.println("</body></html>");

            return;
        }

        int userId =
                (Integer) session.getAttribute("userId");

        String assignmentName =
                request.getParameter("assignment_name");

        String subject =
                request.getParameter("subject");

        String deadline =
                request.getParameter("deadline");

        String priority =
                request.getParameter("priority");

        // Validate input
        if (assignmentName == null ||
            assignmentName.trim().isEmpty() ||

            subject == null ||
            subject.trim().isEmpty() ||

            deadline == null ||
            deadline.trim().isEmpty() ||

            priority == null ||
            priority.trim().isEmpty()) {

            out.println("<html><body>");

            out.println("<h2>Assignment could not be added</h2>");

            out.println("<p>All fields are required.</p>");

            out.println(
                "<a href='add-assignment.html'>Go Back</a>"
            );

            out.println("</body></html>");

            return;
        }

        String sql =
                "INSERT INTO assignments " +
                "(assignment_name, subject, deadline, priority, user_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement statement = null;

        try {

            connection = DBConnection.getConnection();

            if (connection == null) {

                out.println("<html><body>");

                out.println(
                    "<h2>Database Connection Failed</h2>"
                );

                out.println(
                    "<p>Unable to connect to the database.</p>"
                );

                out.println(
                    "<a href='add-assignment.html'>Try Again</a>"
                );

                out.println("</body></html>");

                return;
            }

            statement =
                    connection.prepareStatement(sql);

            statement.setString(
                    1,
                    assignmentName.trim()
            );

            statement.setString(
                    2,
                    subject.trim()
            );

            statement.setString(
                    3,
                    deadline
            );

            statement.setString(
                    4,
                    priority.trim()
            );

            // Logged-in user's ID
            statement.setInt(
                    5,
                    userId
            );

            int result =
                    statement.executeUpdate();

            if (result > 0) {

                out.println("<html>");

                out.println("<head>");
                out.println("<title>Assignment Added</title>");
                out.println("</head>");

                out.println("<body>");

                out.println(
                    "<h2>Assignment Added Successfully!</h2>"
                );

                out.println(
                    "<p>Assignment: " +
                    assignmentName.trim() +
                    "</p>"
                );

                out.println(
                    "<p>Subject: " +
                    subject.trim() +
                    "</p>"
                );

                out.println(
                    "<p>Deadline: " +
                    deadline +
                    "</p>"
                );

                out.println(
                    "<p>Priority: " +
                    priority.trim() +
                    "</p>"
                );

                out.println(
                    "<a href='" +
                    request.getContextPath() +
                    "/ViewAssignments'>" +
                    "View My Assignments</a>"
                );

                out.println("<br><br>");

                out.println(
                    "<a href='" +
                    request.getContextPath() +
                    "/add-assignment.html'>" +
                    "Add Another Assignment</a>"
                );

                out.println("</body>");
                out.println("</html>");

            } else {

                out.println("<html><body>");

                out.println(
                    "<h2>Assignment was not added.</h2>"
                );

                out.println(
                    "<a href='" +
                    request.getContextPath() +
                    "/add-assignment.html'>" +
                    "Try Again</a>"
                );

                out.println("</body></html>");
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<html><body>");

            out.println(
                "<h2>Failed to Add Assignment</h2>"
            );

            out.println(
                "<p>" +
                e.getMessage() +
                "</p>"
            );

            out.println(
                "<a href='" +
                request.getContextPath() +
                "/add-assignment.html'>" +
                "Try Again</a>"
            );

            out.println("</body></html>");

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
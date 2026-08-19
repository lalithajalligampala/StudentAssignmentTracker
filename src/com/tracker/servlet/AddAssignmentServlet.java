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

        String contextPath = request.getContextPath();

        /*
         * IMPORTANT:
         * Get the currently logged-in user's session.
         */
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {

            response.sendRedirect(contextPath + "/login.html");
            return;
        }

        int userId;

        try {

            userId = (Integer) session.getAttribute("userId");

        } catch (Exception e) {

            response.sendRedirect(contextPath + "/login.html");
            return;
        }

        String assignmentName =
                request.getParameter("assignment_name");

        String subject =
                request.getParameter("subject");

        String deadline =
                request.getParameter("deadline");

        String priority =
                request.getParameter("priority");

        PrintWriter out = response.getWriter();

        /*
         * Validate input.
         */
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
                "<a href='" +
                contextPath +
                "/add-assignment.html'>" +
                "Go Back</a>"
            );

            out.println("</body></html>");

            return;
        }

        /*
         * IMPORTANT CHANGE:
         *
         * user_id is now included in the INSERT.
         *
         * The assignment belongs to the currently logged-in user.
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

                out.println("<html><body>");

                out.println(
                    "<h2>Database Connection Failed</h2>"
                );

                out.println(
                    "<p>Unable to connect to the database.</p>"
                );

                out.println(
                    "<a href='" +
                    contextPath +
                    "/add-assignment.html'>" +
                    "Try Again</a>"
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

            /*
             * IMPORTANT:
             * Save the logged-in user's ID.
             */
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

                out.println(
                    "<style>" +

                    "body {" +
                    "font-family: Arial, sans-serif;" +
                    "background-color: #f4f6f8;" +
                    "text-align: center;" +
                    "padding-top: 80px;" +
                    "}" +

                    ".box {" +
                    "background: white;" +
                    "width: 600px;" +
                    "max-width: 90%;" +
                    "margin: auto;" +
                    "padding: 35px;" +
                    "border-radius: 12px;" +
                    "box-shadow: 0 3px 12px rgba(0,0,0,0.12);" +
                    "}" +

                    "h2 {" +
                    "color: #27ae60;" +
                    "}" +

                    ".button {" +
                    "display: inline-block;" +
                    "margin-top: 20px;" +
                    "padding: 12px 20px;" +
                    "background-color: #2c3e50;" +
                    "color: white;" +
                    "text-decoration: none;" +
                    "border-radius: 6px;" +
                    "font-weight: bold;" +
                    "}" +

                    "</style>"
                );

                out.println("</head>");

                out.println("<body>");

                out.println("<div class='box'>");

                out.println(
                    "<h2>Assignment Added Successfully!</h2>"
                );

                out.println(
                    "<p><strong>Assignment:</strong> " +
                    escapeHtml(assignmentName.trim()) +
                    "</p>"
                );

                out.println(
                    "<p><strong>Subject:</strong> " +
                    escapeHtml(subject.trim()) +
                    "</p>"
                );

                out.println(
                    "<p><strong>Deadline:</strong> " +
                    escapeHtml(deadline) +
                    "</p>"
                );

                out.println(
                    "<p><strong>Priority:</strong> " +
                    escapeHtml(priority.trim()) +
                    "</p>"
                );

                out.println(
                    "<p><strong>User ID:</strong> " +
                    userId +
                    "</p>"
                );

                out.println(
                    "<a class='button' href='" +
                    contextPath +
                    "/ViewAssignments'>" +
                    "View My Assignments</a>"
                );

                out.println("<br><br>");

                out.println(
                    "<a class='button' href='" +
                    contextPath +
                    "/add-assignment.html'>" +
                    "Add Another Assignment</a>"
                );

                out.println("</div>");

                out.println("</body>");
                out.println("</html>");

            } else {

                out.println("<html><body>");

                out.println(
                    "<h2>Assignment was not added.</h2>"
                );

                out.println(
                    "<a href='" +
                    contextPath +
                    "/add-assignment.html'>" +
                    "Try Again</a>"
                );

                out.println("</body></html>");
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<html>");

            out.println("<head>");
            out.println("<title>Assignment Error</title>");
            out.println("</head>");

            out.println("<body>");

            out.println(
                "<h2>Error Saving Assignment</h2>"
            );

            out.println(
                "<p>Something went wrong while saving the assignment.</p>"
            );

            out.println(
                "<p><strong>Error:</strong> " +
                escapeHtml(e.getMessage()) +
                "</p>"
            );

            out.println(
                "<a href='" +
                contextPath +
                "/add-assignment.html'>" +
                "Back to Add Assignment</a>"
            );

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

    /*
     * Prevent HTML characters from being written directly
     * into the response.
     */
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
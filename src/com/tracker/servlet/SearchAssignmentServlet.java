package com.tracker.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class SearchAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check whether the student is logged in
        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect(
                request.getContextPath() + "/login.html"
            );

            return;
        }

        int userId;

        try {

            Object userIdObject =
                    session.getAttribute("userId");

            if (userIdObject instanceof Integer) {

                userId = (Integer) userIdObject;

            } else if (userIdObject instanceof Number) {

                userId =
                    ((Number) userIdObject).intValue();

            } else {

                response.sendRedirect(
                    request.getContextPath() + "/login.html"
                );

                return;
            }

        } catch (Exception e) {

            response.sendRedirect(
                request.getContextPath() + "/login.html"
            );

            return;
        }

        String subject = request.getParameter("subject");

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        /*
         * If subject is empty
         */
        if (subject == null ||
            subject.trim().isEmpty()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
            );

            out.println(
                "<title>Search Assignments</title>"
            );

            out.println("<style>");

            // BODY
            out.println(
                "body {" +
                "font-family: Arial, sans-serif;" +
                "margin: 0;" +
                "padding: 0;" +
                "background: linear-gradient(135deg, #eef6ff, #f4fbfa);" +
                "color: #1d2d44;" +
                "min-height: 100vh;" +
                "}"
            );

            // CONTAINER
            out.println(
                ".container {" +
                "width: 92%;" +
                "max-width: 600px;" +
                "margin: 70px auto;" +
                "}"
            );

            // CARD
            out.println(
                ".card {" +
                "background: rgba(255,255,255,0.96);" +
                "padding: 35px;" +
                "border-radius: 16px;" +
                "box-shadow: 0 8px 25px rgba(45,80,120,0.12);" +
                "border: 1px solid #d4e3f5;" +
                "text-align: center;" +
                "}"
            );

            // HEADING
            out.println(
                ".card h2 {" +
                "margin: 0 0 20px 0;" +
                "color: #1d2d44;" +
                "font-size: 24px;" +
                "}"
            );

            // MESSAGE
            out.println(
                ".message {" +
                "background: #f4f8ff;" +
                "border: 1px solid #d4e3f5;" +
                "color: #526579;" +
                "padding: 15px;" +
                "border-radius: 8px;" +
                "margin-bottom: 20px;" +
                "}"
            );

            // BUTTONS
            out.println(
                ".button {" +
                "display: inline-block;" +
                "margin: 6px;" +
                "padding: 12px 20px;" +
                "background: #3978d8;" +
                "color: white;" +
                "text-decoration: none;" +
                "border-radius: 8px;" +
                "font-weight: bold;" +
                "font-size: 14px;" +
                "transition: 0.2s;" +
                "}"
            );

            // BUTTON HOVER
            out.println(
                ".button:hover {" +
                "background: #2f69c2;" +
                "transform: translateY(-1px);" +
                "}"
            );

            // RESPONSIVE
            out.println(
                "@media (max-width: 600px) {" +

                ".container {" +
                "width: 94%;" +
                "margin: 35px auto;" +
                "}" +

                ".card {" +
                "padding: 25px 20px;" +
                "}" +

                ".button {" +
                "display: block;" +
                "margin: 10px 0;" +
                "}" +

                "}"
            );

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='container'>");

            out.println("<div class='card'>");

            out.println(
                "<h2>SEARCH ASSIGNMENTS</h2>"
            );

            out.println(
                "<div class='message'>" +
                "Please enter a subject to search." +
                "</div>"
            );

            // BACK TO SEARCH
            out.println(
                "<a class='button' href='" +
                request.getContextPath() +
                "/SearchAssignment'>" +
                "BACK TO SEARCH" +
                "</a>"
            );

            // BACK TO DASHBOARD
            out.println(
                "<a class='button' href='" +
                request.getContextPath() +
                "/Dashboard'>" +
                "BACK TO DASHBOARD" +
                "</a>"
            );

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            /*
             * DATABASE CONNECTION
             */
            conn = DBConnection.getConnection();

            if (conn == null) {

                throw new Exception(
                    "Database connection failed."
                );
            }

            /*
             * Search assignments for the logged-in user.
             *
             * Sort by:
             * 1. Deadline date
             * 2. Deadline time
             */
            String sql =
                "SELECT id, assignment_name, subject, " +
                "deadline, deadline_time, priority " +
                "FROM assignments " +
                "WHERE user_id = ? AND subject = ? " +
                "ORDER BY deadline ASC, deadline_time ASC";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            ps.setString(2, subject);

            rs = ps.executeQuery();

            /*
             * HTML PAGE
             */
            out.println("<!DOCTYPE html>");

            out.println("<html>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
            );

            out.println(
                "<title>Search Assignments</title>"
            );

            out.println("<style>");

            // BODY
            out.println(
                "body {" +
                "font-family: Arial, sans-serif;" +
                "margin: 0;" +
                "padding: 0;" +
                "background: linear-gradient(135deg, #eef6ff, #f4fbfa);" +
                "color: #1d2d44;" +
                "min-height: 100vh;" +
                "}"
            );

            // CONTAINER
            out.println(
                ".container {" +
                "width: 92%;" +
                "max-width: 1100px;" +
                "margin: 45px auto;" +
                "}"
            );

            // CARD
            out.println(
                ".card {" +
                "background: rgba(255,255,255,0.96);" +
                "padding: 30px;" +
                "border-radius: 16px;" +
                "box-shadow: 0 8px 25px rgba(45,80,120,0.12);" +
                "border: 1px solid #d4e3f5;" +
                "overflow-x: auto;" +
                "}"
            );

            // HEADING
            out.println(
                ".card h2 {" +
                "text-align: center;" +
                "margin: 0 0 10px 0;" +
                "color: #1d2d44;" +
                "font-size: 25px;" +
                "letter-spacing: 0.5px;" +
                "}"
            );

            // SEARCH SUBJECT
            out.println(
                ".search-subject {" +
                "text-align: center;" +
                "color: #526579;" +
                "font-size: 15px;" +
                "margin-bottom: 25px;" +
                "}"
            );

            // TABLE
            out.println(
                "table {" +
                "width: 100%;" +
                "border-collapse: collapse;" +
                "margin-top: 10px;" +
                "background: white;" +
                "}"
            );

            // TABLE HEADER
            out.println(
                "th {" +
                "background: #3978d8;" +
                "color: white;" +
                "padding: 14px 12px;" +
                "font-size: 14px;" +
                "border: 1px solid #3978d8;" +
                "}"
            );

            // TABLE DATA
            out.println(
                "td {" +
                "padding: 13px 12px;" +
                "text-align: center;" +
                "border-bottom: 1px solid #dce6f2;" +
                "color: #33475b;" +
                "font-size: 14px;" +
                "}"
            );

            // ROW HOVER
            out.println(
                "tr:hover {" +
                "background-color: #f4f8ff;" +
                "}"
            );

            // EMPTY RESULT
            out.println(
                ".empty {" +
                "text-align: center;" +
                "padding: 20px;" +
                "color: #526579;" +
                "}"
            );

            // BUTTON CONTAINER
            out.println(
                ".buttons {" +
                "text-align: center;" +
                "margin-top: 28px;" +
                "}"
            );

            // BUTTON
            out.println(
                ".button {" +
                "display: inline-block;" +
                "margin: 6px;" +
                "padding: 12px 20px;" +
                "background: #3978d8;" +
                "color: white;" +
                "text-decoration: none;" +
                "border-radius: 8px;" +
                "font-weight: bold;" +
                "font-size: 14px;" +
                "transition: 0.2s;" +
                "}"
            );

            // BUTTON HOVER
            out.println(
                ".button:hover {" +
                "background: #2f69c2;" +
                "transform: translateY(-1px);" +
                "}"
            );

            // ERROR
            out.println(
                ".error {" +
                "background: #fff1f1;" +
                "border: 1px solid #f0bcbc;" +
                "color: #b42318;" +
                "padding: 14px;" +
                "border-radius: 8px;" +
                "margin-top: 20px;" +
                "}"
            );

            // RESPONSIVE DESIGN
            out.println(
                "@media (max-width: 700px) {" +

                ".container {" +
                "width: 94%;" +
                "margin: 25px auto;" +
                "}" +

                ".card {" +
                "padding: 20px;" +
                "}" +

                ".card h2 {" +
                "font-size: 21px;" +
                "}" +

                "th, td {" +
                "padding: 10px 8px;" +
                "font-size: 12px;" +
                "}" +

                ".button {" +
                "display: block;" +
                "margin: 8px 0;" +
                "}" +

                "}"
            );

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='container'>");

            out.println("<div class='card'>");

            /*
             * HEADING
             */
            out.println(
                "<h2>SEARCH RESULTS</h2>"
            );

            out.println(
                "<div class='search-subject'>" +
                "Subject: <b>" +
                escapeHtml(subject) +
                "</b>" +
                "</div>"
            );

            /*
             * TABLE
             */
            out.println("<table>");

            out.println("<tr>");

            out.println("<th>S.No.</th>");

            out.println("<th>Assignment Name</th>");

            out.println("<th>Subject</th>");

            out.println("<th>Deadline</th>");

            out.println("<th>Time</th>");

            out.println("<th>Priority</th>");

            out.println("</tr>");

            int serialNumber = 1;

            boolean found = false;

            /*
             * DISPLAY ASSIGNMENTS
             */
            while (rs.next()) {

                found = true;

                String assignmentName =
                    rs.getString("assignment_name");

                String assignmentSubject =
                    rs.getString("subject");

                Date deadline =
                    rs.getDate("deadline");

                Time deadlineTime =
                    rs.getTime("deadline_time");

                String priority =
                    rs.getString("priority");

                out.println("<tr>");

                // S.No.
                out.println(
                    "<td>" +
                    serialNumber +
                    "</td>"
                );

                // Assignment Name
                out.println(
                    "<td>" +
                    escapeHtml(assignmentName) +
                    "</td>"
                );

                // Subject
                out.println(
                    "<td>" +
                    escapeHtml(assignmentSubject) +
                    "</td>"
                );

                // Deadline
                out.println(
                    "<td>" +
                    (deadline != null
                        ? deadline.toString()
                        : "-") +
                    "</td>"
                );

                // Time
                out.println(
                    "<td>" +
                    (deadlineTime != null
                        ? deadlineTime.toString()
                        : "-") +
                    "</td>"
                );

                // Priority
                out.println(
                    "<td>" +
                    escapeHtml(priority) +
                    "</td>"
                );

                out.println("</tr>");

                serialNumber++;
            }

            /*
             * NO RESULTS
             */
            if (!found) {

                out.println("<tr>");

                out.println(
                    "<td colspan='6' class='empty'>"
                );

                out.println(
                    "No assignments found for this subject."
                );

                out.println("</td>");

                out.println("</tr>");
            }

            out.println("</table>");

            /*
             * BUTTONS
             */
            out.println("<div class='buttons'>");

            // SEARCH AGAIN
            out.println(
                "<a class='button' href='" +
                request.getContextPath() +
                "/SearchAssignment'>" +
                "SEARCH AGAIN" +
                "</a>"
            );

            // VIEW ASSIGNMENTS
            out.println(
                "<a class='button' href='" +
                request.getContextPath() +
                "/ViewAssignments'>" +
                "VIEW MY ASSIGNMENTS" +
                "</a>"
            );

            // DASHBOARD
            out.println(
                "<a class='button' href='" +
                request.getContextPath() +
                "/Dashboard'>" +
                "BACK TO DASHBOARD" +
                "</a>"
            );

            out.println("</div>");

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<!DOCTYPE html>");

            out.println("<html>");

            out.println("<head>");

            out.println(
                "<meta charset='UTF-8'>"
            );

            out.println(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
            );

            out.println(
                "<title>Error</title>"
            );

            out.println("<style>");

            out.println(
                "body {" +
                "font-family: Arial, sans-serif;" +
                "margin: 0;" +
                "background: linear-gradient(135deg, #eef6ff, #f4fbfa);" +
                "color: #1d2d44;" +
                "}"
            );

            out.println(
                ".container {" +
                "width: 92%;" +
                "max-width: 600px;" +
                "margin: 70px auto;" +
                "}"
            );

            out.println(
                ".card {" +
                "background: white;" +
                "padding: 30px;" +
                "border-radius: 16px;" +
                "box-shadow: 0 8px 25px rgba(45,80,120,0.12);" +
                "border: 1px solid #d4e3f5;" +
                "text-align: center;" +
                "}"
            );

            out.println(
                ".error {" +
                "background: #fff1f1;" +
                "border: 1px solid #f0bcbc;" +
                "color: #b42318;" +
                "padding: 15px;" +
                "border-radius: 8px;" +
                "margin: 20px 0;" +
                "}"
            );

            out.println(
                ".button {" +
                "display: inline-block;" +
                "padding: 12px 20px;" +
                "background: #3978d8;" +
                "color: white;" +
                "text-decoration: none;" +
                "border-radius: 8px;" +
                "font-weight: bold;" +
                "}"
            );

            out.println(
                ".button:hover {" +
                "background: #2f69c2;" +
                "}"
            );

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='container'>");

            out.println("<div class='card'>");

            out.println(
                "<h2>SEARCH ASSIGNMENTS</h2>"
            );

            out.println(
                "<div class='error'>" +
                "<b>Error while searching assignments.</b>" +
                "<br><br>" +
                escapeHtml(e.getMessage()) +
                "</div>"
            );

            out.println(
                "<a class='button' href='" +
                request.getContextPath() +
                "/Dashboard'>" +
                "BACK TO DASHBOARD" +
                "</a>"
            );

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

        } finally {

            // CLOSE RESULTSET
            try {

                if (rs != null) {
                    rs.close();
                }

            } catch (Exception ignored) {
            }

            // CLOSE PREPARED STATEMENT
            try {

                if (ps != null) {
                    ps.close();
                }

            } catch (Exception ignored) {
            }

            // CLOSE CONNECTION
            try {

                if (conn != null) {
                    conn.close();
                }

            } catch (Exception ignored) {
            }
        }
    }

    /*
     * Escape HTML characters to prevent
     * user/database values from breaking the page.
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
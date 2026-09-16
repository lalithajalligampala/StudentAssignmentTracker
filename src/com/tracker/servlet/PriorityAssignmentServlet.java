package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Time;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
public class PriorityAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String contextPath = request.getContextPath();

        // ==========================================
        // CHECK LOGIN SESSION
        // ==========================================

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect(
                contextPath + "/login.html"
            );

            return;
        }

        // ==========================================
        // GET LOGGED-IN USER ID
        // ==========================================

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
                    contextPath + "/login.html"
                );

                return;
            }

        } catch (Exception e) {

            response.sendRedirect(
                contextPath + "/login.html"
            );

            return;
        }

        // ==========================================
        // GET PRIORITY
        // ==========================================

        String priority =
                request.getParameter("priority");

        // ==========================================
        // HTML PAGE
        // ==========================================

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");

        out.println("<meta charset='UTF-8'>");

        out.println(
            "<meta name='viewport' " +
            "content='width=device-width, initial-scale=1.0'>"
        );

        out.println(
            "<title>Priority Assignments</title>"
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

        // SELECTED PRIORITY
        out.println(
            ".priority-info {" +
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

        // EMPTY MESSAGE
        out.println(
            ".empty {" +
            "text-align: center;" +
            "padding: 20px;" +
            "color: #526579;" +
            "font-size: 15px;" +
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

        // ==========================================
        // HEADING
        // ==========================================

        out.println(
            "<h2>FILTER BY PRIORITY</h2>"
        );

        // ==========================================
        // VALIDATE PRIORITY
        // ==========================================

        if (priority == null ||
            priority.trim().isEmpty()) {

            out.println(
                "<div class='error'>" +
                "Please select a priority." +
                "</div>"
            );

            out.println(
                "<div class='buttons'>"
            );

            out.println(
                "<a class='button' href='" +
                contextPath +
                "/priority.html'>" +
                "BACK TO PRIORITY FILTER" +
                "</a>"
            );

            out.println(
                "<a class='button' href='" +
                contextPath +
                "/Dashboard'>" +
                "BACK TO DASHBOARD" +
                "</a>"
            );

            out.println("</div>");

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

            return;
        }

        priority = priority.trim();

        // ==========================================
        // SELECTED PRIORITY
        // ==========================================

        out.println(
            "<div class='priority-info'>" +
            "Selected Priority: <b>" +
            escapeHtml(priority) +
            "</b>" +
            "</div>"
        );

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            // ==========================================
            // DATABASE CONNECTION
            // ==========================================

            con = DBConnection.getConnection();

            if (con == null) {

                throw new Exception(
                    "Database connection failed."
                );
            }

            // ==========================================
            // SEARCH ASSIGNMENTS
            // ==========================================

            String sql =
                "SELECT id, assignment_name, subject, " +
                "deadline, deadline_time, priority " +
                "FROM assignments " +
                "WHERE user_id = ? AND priority = ? " +
                "ORDER BY deadline ASC, deadline_time ASC";

            ps = con.prepareStatement(sql);

            // Logged-in user
            ps.setInt(1, userId);

            // Selected priority
            ps.setString(2, priority);

            rs = ps.executeQuery();

            // ==========================================
            // TABLE
            // ==========================================

            out.println("<table>");

            out.println("<tr>");

            out.println("<th>S.No.</th>");

            out.println("<th>Assignment Name</th>");

            out.println("<th>Subject</th>");

            out.println("<th>Deadline</th>");

            out.println("<th>Time</th>");

            out.println("<th>Priority</th>");

            out.println("</tr>");

            boolean found = false;

            int serialNumber = 1;

            // ==========================================
            // DISPLAY ASSIGNMENTS
            // ==========================================

            while (rs.next()) {

                found = true;

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
                    escapeHtml(
                        rs.getString("assignment_name")
                    ) +
                    "</td>"
                );

                // Subject
                out.println(
                    "<td>" +
                    escapeHtml(
                        rs.getString("subject")
                    ) +
                    "</td>"
                );

                // Deadline
                Date deadline =
                    rs.getDate("deadline");

                out.println(
                    "<td>" +
                    (deadline != null
                        ? deadline.toString()
                        : "-") +
                    "</td>"
                );

                // Deadline Time
                Time deadlineTime =
                    rs.getTime("deadline_time");

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
                    escapeHtml(
                        rs.getString("priority")
                    ) +
                    "</td>"
                );

                out.println("</tr>");

                serialNumber++;
            }

            out.println("</table>");

            // ==========================================
            // NO ASSIGNMENTS
            // ==========================================

            if (!found) {

                out.println(
                    "<div class='empty'>" +
                    "No assignments found with priority: " +
                    "<b>" +
                    escapeHtml(priority) +
                    "</b>" +
                    "</div>"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                "<div class='error'>" +
                "<b>Error loading assignments:</b>" +
                "<br><br>" +
                escapeHtml(e.getMessage()) +
                "</div>"
            );

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

                if (con != null) {
                    con.close();
                }

            } catch (Exception ignored) {
            }
        }

        // ==========================================
        // BUTTONS
        // ==========================================

        out.println(
            "<div class='buttons'>"
        );

        // FILTER AGAIN
        out.println(
            "<a class='button' href='" +
            contextPath +
            "/priority.html'>" +
            "FILTER AGAIN" +
            "</a>"
        );

        // VIEW ASSIGNMENTS
        out.println(
            "<a class='button' href='" +
            contextPath +
            "/ViewAssignments'>" +
            "VIEW MY ASSIGNMENTS" +
            "</a>"
        );

        // DASHBOARD
        out.println(
            "<a class='button' href='" +
            contextPath +
            "/Dashboard'>" +
            "BACK TO DASHBOARD" +
            "</a>"
        );

        out.println("</div>");

        out.println("</div>");

        out.println("</div>");

        out.println("</body>");

        out.println("</html>");
    }

    // ==========================================
    // ESCAPE HTML
    // ==========================================

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
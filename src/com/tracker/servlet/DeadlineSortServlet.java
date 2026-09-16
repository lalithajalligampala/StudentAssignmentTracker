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

public class DeadlineSortServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String contextPath = request.getContextPath();

        // Check login session
        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect(
                contextPath + "/login.html"
            );

            return;
        }

        // Get logged-in user's ID
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

        // HTML PAGE
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");

        out.println("<meta charset='UTF-8'>");

        out.println(
            "<meta name='viewport' " +
            "content='width=device-width, initial-scale=1.0'>"
        );

        out.println(
            "<title>Sort by Deadline</title>"
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
            "margin: 0 0 25px 0;" +
            "color: #1d2d44;" +
            "font-size: 25px;" +
            "letter-spacing: 0.5px;" +
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

        // NO ASSIGNMENTS
        out.println(
            ".empty {" +
            "text-align: center;" +
            "padding: 25px;" +
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

        // CONTAINER
        out.println("<div class='container'>");

        // CARD
        out.println("<div class='card'>");

        // HEADING
        out.println(
            "<h2>SORT BY DEADLINE</h2>"
        );

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            // DATABASE CONNECTION
            con = DBConnection.getConnection();

            if (con == null) {

                throw new Exception(
                    "Database connection failed."
                );
            }

            /*
             * Get assignments for the logged-in user.
             *
             * Sort by:
             * 1. Deadline date
             * 2. Deadline time
             */

            String sql =
                "SELECT assignment_name, subject, " +
                "deadline, deadline_time, priority " +
                "FROM assignments " +
                "WHERE user_id = ? " +
                "ORDER BY deadline ASC, deadline_time ASC";

            ps = con.prepareStatement(sql);

            // Set logged-in user's ID
            ps.setInt(1, userId);

            rs = ps.executeQuery();

            // TABLE
            out.println("<table>");

            out.println("<tr>");

            out.println("<th>S.No.</th>");

            out.println(
                "<th>Assignment Name</th>"
            );

            out.println("<th>Subject</th>");

            out.println("<th>Deadline</th>");

            out.println("<th>Time</th>");

            out.println("<th>Priority</th>");

            out.println("</tr>");

            boolean found = false;

            int serialNumber = 1;

            // DISPLAY ASSIGNMENTS
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

                // Deadline Date
                out.println(
                    "<td>" +
                    rs.getDate("deadline") +
                    "</td>"
                );

                // Deadline Time
                String deadlineTime =
                    rs.getString("deadline_time");

                if (deadlineTime == null ||
                    deadlineTime.trim().isEmpty()) {

                    deadlineTime = "-";
                }

                out.println(
                    "<td>" +
                    escapeHtml(deadlineTime) +
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

            // NO ASSIGNMENTS
            if (!found) {

                out.println(
                    "<div class='empty'>" +
                    "No assignments found." +
                    "</div>"
                );
            }

        } catch (Exception e) {

            out.println(
                "<div class='error'>" +
                "<b>Error loading assignments:</b> " +
                escapeHtml(e.getMessage()) +
                "</div>"
            );

            e.printStackTrace();

        } finally {

            // CLOSE RESULTSET
            try {

                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // CLOSE PREPARED STATEMENT
            try {

                if (ps != null) {
                    ps.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // CLOSE CONNECTION
            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // BUTTONS
        out.println("<div class='buttons'>");

        // BACK TO ALL ASSIGNMENTS
        out.println(
            "<a class='button' href='" +
            contextPath +
            "/ViewAssignments'>" +
            "BACK TO ALL ASSIGNMENTS" +
            "</a>"
        );

        // BACK TO DASHBOARD
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

    // HTML ESCAPE METHOD
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
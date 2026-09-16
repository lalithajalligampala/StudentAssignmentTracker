package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ViewAssignmentsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // ==================================================
        // CHECK LOGIN SESSION
        // ==================================================

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html"
            );

            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        String contextPath = request.getContextPath();

        // ==================================================
        // HTML START
        // ==================================================

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");

        out.println("<meta charset='UTF-8'>");

        out.println(
            "<meta name='viewport' " +
            "content='width=device-width, initial-scale=1.0'>"
        );

        out.println("<title>View Assignments</title>");

        // ==================================================
        // CSS
        // ==================================================

        out.println("<style>");

        // Universal
        out.println(
            "* {" +
            "box-sizing: border-box;" +
            "}"
        );

        // Body
        out.println(
            "body {" +
            "margin: 0;" +
            "min-height: 100vh;" +
            "font-family: Arial, Helvetica, sans-serif;" +
            "background: linear-gradient(135deg, #eef6ff, #f4fbfa);" +
            "color: #1f2d3d;" +
            "}"
        );

        // Decorative Circle - Top Left
        out.println(
            "body::before {" +
            "content: '';" +
            "position: fixed;" +
            "width: 420px;" +
            "height: 420px;" +
            "border-radius: 50%;" +
            "background: rgba(74, 144, 226, 0.06);" +
            "top: -150px;" +
            "left: -150px;" +
            "z-index: -1;" +
            "}"
        );

        // Decorative Circle - Bottom Right
        out.println(
            "body::after {" +
            "content: '';" +
            "position: fixed;" +
            "width: 500px;" +
            "height: 500px;" +
            "border-radius: 50%;" +
            "background: rgba(80, 190, 170, 0.06);" +
            "bottom: -200px;" +
            "right: -180px;" +
            "z-index: -1;" +
            "}"
        );

        // Main Container
        out.println(
            ".container {" +
            "width: 92%;" +
            "max-width: 1350px;" +
            "margin: 35px auto 50px auto;" +
            "}"
        );

        // Card
        out.println(
            ".card {" +
            "background: rgba(255, 255, 255, 0.96);" +
            "padding: 32px;" +
            "border: 1px solid #d4e3f5;" +
            "border-radius: 12px;" +
            "box-shadow: " +
            "0 12px 35px rgba(31, 60, 90, 0.12), " +
            "0 2px 8px rgba(31, 60, 90, 0.06);" +
            "overflow-x: auto;" +
            "}"
        );

        // Card Heading
        out.println(
            ".card h2 {" +
            "margin: 0 0 24px 0;" +
            "font-size: 28px;" +
            "font-weight: 600;" +
            "color: #1d2d44;" +
            "}"
        );

        // Table
        out.println(
            "table {" +
            "width: 100%;" +
            "border-collapse: separate;" +
            "border-spacing: 0;" +
            "margin-top: 10px;" +
            "overflow: hidden;" +
            "border: 1px solid #d9e3ee;" +
            "border-radius: 8px;" +
            "}"
        );

        // Table Header
        out.println(
            "th {" +
            "background: #3978d8;" +
            "color: white;" +
            "padding: 15px 12px;" +
            "text-align: center;" +
            "font-size: 16px;" +
            "font-weight: 600;" +
            "white-space: nowrap;" +
            "}"
        );

        // Table Data
        out.println(
            "td {" +
            "padding: 15px 12px;" +
            "text-align: center;" +
            "font-size: 16px;" +
            "color: #26384a;" +
            "border-bottom: 1px solid #e1e6ec;" +
            "background: #ffffff;" +
            "}"
        );

        // Last Row
        out.println(
            "tr:last-child td {" +
            "border-bottom: none;" +
            "}"
        );

        // Row Hover
        out.println(
            "tr:hover td {" +
            "background: #f5f9ff;" +
            "}"
        );

        // Status
        out.println(
            ".status {" +
            "font-weight: bold;" +
            "white-space: nowrap;" +
            "}"
        );

        // Overdue
        out.println(
            ".overdue {" +
            "color: #d9362b;" +
            "}"
        );

        // Due Soon
        out.println(
            ".due-soon {" +
            "color: #e58a00;" +
            "}"
        );

        // Upcoming
        out.println(
            ".upcoming {" +
            "color: #149447;" +
            "}"
        );

        // Action Column
        out.println(
            ".action-cell {" +
            "white-space: nowrap;" +
            "}"
        );

        // Edit and Delete Buttons
        out.println(
            ".edit-btn, .delete-btn {" +
            "display: inline-block;" +
            "padding: 9px 15px;" +
            "border-radius: 7px;" +
            "text-decoration: none;" +
            "font-weight: bold;" +
            "font-size: 15px;" +
            "transition: all 0.2s ease;" +
            "}"
        );

        // Edit Button
        out.println(
            ".edit-btn {" +
            "background: #3978d8;" +
            "color: white;" +
            "}"
        );

        // Delete Button
        out.println(
            ".delete-btn {" +
            "background: #e94b3c;" +
            "color: white;" +
            "}"
        );

        // Edit Hover
        out.println(
            ".edit-btn:hover {" +
            "background: #2f69c2;" +
            "transform: translateY(-1px);" +
            "box-shadow: 0 4px 10px rgba(57, 120, 216, 0.20);" +
            "}"
        );

        // Delete Hover
        out.println(
            ".delete-btn:hover {" +
            "background: #d63d30;" +
            "transform: translateY(-1px);" +
            "box-shadow: 0 4px 10px rgba(233, 75, 60, 0.18);" +
            "}"
        );

        // Dashboard Button
        out.println(
            ".dashboard-btn {" +
            "display: inline-block;" +
            "margin-top: 25px;" +
            "padding: 13px 22px;" +
            "background: white;" +
            "color: #3978d8;" +
            "border: 2px solid #3978d8;" +
            "border-radius: 7px;" +
            "text-decoration: none;" +
            "font-weight: bold;" +
            "font-size: 16px;" +
            "letter-spacing: 0.5px;" +
            "transition: all 0.2s ease;" +
            "}"
        );

        // Dashboard Hover
        out.println(
            ".dashboard-btn:hover {" +
            "background: #3978d8;" +
            "color: white;" +
            "transform: translateY(-1px);" +
            "box-shadow: 0 5px 14px rgba(57, 120, 216, 0.20);" +
            "}"
        );

        // No Assignment Message
        out.println(
            ".no-data {" +
            "padding: 25px;" +
            "color: #718096;" +
            "font-size: 16px;" +
            "}"
        );

        // Error Message
        out.println(
            ".error-message {" +
            "color: #d9362b;" +
            "font-weight: bold;" +
            "padding: 20px;" +
            "}"
        );

        // Responsive Design
        out.println(
            "@media (max-width: 900px) {" +

            ".container {" +
            "width: 95%;" +
            "}" +

            ".card {" +
            "padding: 22px;" +
            "}" +

            "th, td {" +
            "padding: 12px 9px;" +
            "font-size: 14px;" +
            "}" +

            ".edit-btn, .delete-btn {" +
            "padding: 8px 11px;" +
            "font-size: 14px;" +
            "}" +

            "}"
        );

        // Mobile Design
        out.println(
            "@media (max-width: 600px) {" +

            ".container {" +
            "width: 96%;" +
            "margin-top: 25px;" +
            "}" +

            ".card {" +
            "padding: 18px;" +
            "}" +

            ".card h2 {" +
            "font-size: 24px;" +
            "}" +

            "th, td {" +
            "padding: 10px 8px;" +
            "font-size: 13px;" +
            "}" +

            ".dashboard-btn {" +
            "width: 100%;" +
            "text-align: center;" +
            "}" +

            "}"
        );

        out.println("</style>");

        out.println("</head>");

        // ==================================================
        // BODY
        // ==================================================

        out.println("<body>");

        // ==================================================
        // CONTAINER
        // ==================================================

        out.println("<div class='container'>");

        // Card
        out.println("<div class='card'>");

        out.println("<h2>My Assignments</h2>");

        // ==================================================
        // TABLE
        // ==================================================

        out.println("<table>");

        out.println("<tr>");

        out.println("<th>S.No.</th>");
        out.println("<th>Assignment Name</th>");
        out.println("<th>Subject</th>");
        out.println("<th>Deadline</th>");
        out.println("<th>Deadline Time</th>");
        out.println("<th>Priority</th>");
        out.println("<th>Deadline Status</th>");
        out.println("<th>Action</th>");

        out.println("</tr>");

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();

            if (con == null) {

                throw new Exception(
                    "Database connection failed."
                );
            }

            // ==================================================
            // ONLY SHOW LOGGED-IN USER'S ASSIGNMENTS
            // ==================================================

            String sql =
                    "SELECT id, assignment_name, subject, " +
                    "deadline, deadline_time, priority " +
                    "FROM assignments " +
                    "WHERE user_id = ? " +
                    "ORDER BY deadline ASC, deadline_time ASC";

            ps = con.prepareStatement(sql);

            ps.setInt(1, userId);

            rs = ps.executeQuery();

            int serialNumber = 1;

            while (rs.next()) {

                int id = rs.getInt("id");

                String assignmentName =
                        rs.getString("assignment_name");

                String subject =
                        rs.getString("subject");

                LocalDate deadline =
                        rs.getDate("deadline").toLocalDate();

                Time deadlineTime =
                        rs.getTime("deadline_time");

                String priority =
                        rs.getString("priority");

                LocalDate today =
                        LocalDate.now();

                long daysRemaining =
                        ChronoUnit.DAYS.between(
                            today,
                            deadline
                        );

                String deadlineStatus;
                String statusClass;

                if (daysRemaining < 0) {

                    deadlineStatus = "Overdue";
                    statusClass = "overdue";

                } else if (daysRemaining <= 3) {

                    deadlineStatus = "Due Soon";
                    statusClass = "due-soon";

                } else {

                    deadlineStatus = "Upcoming";
                    statusClass = "upcoming";
                }

                // ==================================================
                // ROW
                // ==================================================

                out.println("<tr>");

                out.println(
                    "<td>" +
                    serialNumber +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    assignmentName +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    subject +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    deadline +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    (deadlineTime != null
                        ? deadlineTime.toString()
                        : "Not Set") +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    priority +
                    "</td>"
                );

                out.println(
                    "<td class='status " +
                    statusClass +
                    "'>" +
                    deadlineStatus +
                    "</td>"
                );

                // ==================================================
                // ACTIONS
                // ==================================================

                out.println(
                    "<td class='action-cell'>"
                );

                out.println(
                    "<a class='edit-btn' " +
                    "href='" +
                    contextPath +
                    "/EditAssignment?id=" +
                    id +
                    "'>" +
                    "Edit" +
                    "</a>"
                );

                out.println("&nbsp;");

                out.println(
                    "<a class='delete-btn' " +
                    "href='" +
                    contextPath +
                    "/DeleteAssignment?id=" +
                    id +
                    "'>" +
                    "Delete" +
                    "</a>"
                );

                out.println("</td>");

                out.println("</tr>");

                serialNumber++;
            }

            // ==================================================
            // NO ASSIGNMENTS
            // ==================================================

            if (serialNumber == 1) {

                out.println("<tr>");

                out.println(
                    "<td colspan='8' class='no-data'>" +
                    "No assignments found." +
                    "</td>"
                );

                out.println("</tr>");
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<tr>");

            out.println(
                "<td colspan='8' class='error-message'>"
            );

            out.println(
                "Error: " +
                e.getMessage()
            );

            out.println("</td>");

            out.println("</tr>");

        } finally {

            try {

                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                if (ps != null) {
                    ps.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out.println("</table>");

        // ==================================================
        // BACK TO DASHBOARD
        // ==================================================

        out.println(
            "<a class='dashboard-btn' " +
            "href='" +
            contextPath +
            "/Dashboard'>" +
            "Back to Dashboard" +
            "</a>"
        );

        out.println("</div>");

        out.println("</div>");

        out.println("</body>");

        out.println("</html>");
    }
}
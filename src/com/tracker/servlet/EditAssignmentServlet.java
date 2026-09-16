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

public class EditAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String contextPath = request.getContextPath();

        // ==================================================
        // CHECK LOGIN SESSION
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
        // GET ASSIGNMENT ID
        // ==================================================

        String id = request.getParameter("id");

        PrintWriter out = response.getWriter();

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

        out.println("<title>Edit Assignment</title>");

        // ==================================================
        // CSS
        // ==================================================

        out.println("<style>");

        out.println(
            "* {" +
            "box-sizing: border-box;" +
            "}"
        );

        out.println(
            "body {" +
            "margin: 0;" +
            "min-height: 100vh;" +
            "font-family: Arial, Helvetica, sans-serif;" +
            "background: linear-gradient(135deg, #eef6ff, #f4fbfa);" +
            "color: #1f2d3d;" +
            "}"
        );

        // ==================================================
        // DECORATIVE BACKGROUND
        // ==================================================

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

        // ==================================================
        // MAIN CONTAINER
        // ==================================================

        out.println(
            ".container {" +
            "width: 760px;" +
            "max-width: 90%;" +
            "margin: 50px auto;" +
            "}"
        );

        // ==================================================
        // CARD
        // ==================================================

        out.println(
            ".card {" +
            "background: rgba(255, 255, 255, 0.96);" +
            "border: 1px solid #d4e3f5;" +
            "border-radius: 12px;" +
            "padding: 42px 45px 45px 45px;" +
            "box-shadow: " +
            "0 12px 35px rgba(31, 60, 90, 0.12), " +
            "0 2px 8px rgba(31, 60, 90, 0.06);" +
            "}"
        );

        // ==================================================
        // SINGLE HEADING
        // ==================================================

        out.println(
            ".card h2 {" +
            "margin: 0 0 35px 0;" +
            "text-align: center;" +
            "font-size: 30px;" +
            "font-weight: 500;" +
            "letter-spacing: 3px;" +
            "color: #1d2d44;" +
            "}"
        );

        // ==================================================
        // FORM GROUP
        // ==================================================

        out.println(
            ".form-group {" +
            "margin-bottom: 25px;" +
            "}"
        );

        // ==================================================
        // LABEL
        // ==================================================

        out.println(
            "label {" +
            "display: block;" +
            "margin-bottom: 9px;" +
            "font-size: 17px;" +
            "font-weight: 600;" +
            "color: #1f2d3d;" +
            "}"
        );

        // ==================================================
        // INPUTS
        // ==================================================

        out.println(
            "input[type='text'], " +
            "input[type='date'], " +
            "input[type='time'], " +
            "select {" +
            "width: 100%;" +
            "height: 58px;" +
            "padding: 0 18px;" +
            "border: 1px solid #d4dce6;" +
            "border-radius: 7px;" +
            "background: #ffffff;" +
            "color: #34495e;" +
            "font-size: 16px;" +
            "outline: none;" +
            "transition: all 0.2s ease;" +
            "}"
        );

        // ==================================================
        // INPUT FOCUS
        // ==================================================

        out.println(
            "input[type='text']:focus, " +
            "input[type='date']:focus, " +
            "input[type='time']:focus, " +
            "select:focus {" +
            "border-color: #3978d8;" +
            "box-shadow: 0 0 0 3px rgba(57, 120, 216, 0.10);" +
            "}"
        );

        // ==================================================
        // UPDATE BUTTON
        // ==================================================

        out.println(
            "input[type='submit'] {" +
            "width: 100%;" +
            "height: 64px;" +
            "margin-top: 8px;" +
            "border: none;" +
            "border-radius: 7px;" +
            "background: #3978d8;" +
            "color: white;" +
            "font-size: 18px;" +
            "font-weight: bold;" +
            "letter-spacing: 3px;" +
            "cursor: pointer;" +
            "transition: all 0.2s ease;" +
            "}"
        );

        out.println(
            "input[type='submit']:hover {" +
            "background: #2f69c2;" +
            "transform: translateY(-1px);" +
            "box-shadow: 0 5px 14px rgba(57, 120, 216, 0.20);" +
            "}"
        );

        // ==================================================
        // BACK BUTTON
        // ==================================================

        out.println(
            ".back-btn {" +
            "display: block;" +
            "width: 100%;" +
            "height: 62px;" +
            "margin-top: 18px;" +
            "border: 2px solid #3978d8;" +
            "border-radius: 7px;" +
            "background: white;" +
            "color: #3978d8;" +
            "text-decoration: none;" +
            "text-align: center;" +
            "line-height: 58px;" +
            "font-size: 18px;" +
            "font-weight: bold;" +
            "letter-spacing: 3px;" +
            "transition: all 0.2s ease;" +
            "}"
        );

        out.println(
            ".back-btn:hover {" +
            "background: #3978d8;" +
            "color: white;" +
            "}"
        );

        // ==================================================
        // ERROR BOX
        // ==================================================

        out.println(
            ".error-box {" +
            "text-align: center;" +
            "background: #fff5f5;" +
            "border: 1px solid #f1c4c4;" +
            "border-radius: 9px;" +
            "padding: 22px;" +
            "margin-bottom: 25px;" +
            "}"
        );

        out.println(
            ".error {" +
            "margin: 0 0 10px 0;" +
            "color: #d64545;" +
            "font-size: 25px;" +
            "}"
        );

        out.println(
            ".info {" +
            "color: #526579;" +
            "font-size: 16px;" +
            "line-height: 1.6;" +
            "margin: 0;" +
            "}"
        );

        // ==================================================
        // RESPONSIVE DESIGN
        // ==================================================

        out.println(
            "@media (max-width: 700px) {" +

            ".container {" +
            "max-width: 92%;" +
            "margin: 30px auto;" +
            "}" +

            ".card {" +
            "padding: 30px 25px 35px 25px;" +
            "}" +

            ".card h2 {" +
            "font-size: 25px;" +
            "letter-spacing: 2px;" +
            "margin-bottom: 28px;" +
            "}" +

            "input[type='text'], " +
            "input[type='date'], " +
            "input[type='time'], " +
            "select {" +
            "height: 54px;" +
            "}" +

            "input[type='submit'] {" +
            "height: 58px;" +
            "font-size: 16px;" +
            "}" +

            ".back-btn {" +
            "height: 58px;" +
            "line-height: 54px;" +
            "font-size: 16px;" +
            "}" +

            "}"
        );

        out.println("</style>");

        out.println("</head>");

        // ==================================================
        // BODY
        // ==================================================

        out.println("<body>");

        // NO TOP HEADER HERE

        out.println("<div class='container'>");

        out.println("<div class='card'>");

        // ONLY ONE HEADING

        out.println("<h2>EDIT ASSIGNMENT</h2>");

        // ==================================================
        // DATABASE
        // ==================================================

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            // ==================================================
            // VALIDATE ID
            // ==================================================

            if (id == null || id.trim().isEmpty()) {

                throw new Exception(
                    "Assignment ID is missing."
                );
            }

            int assignmentId =
                    Integer.parseInt(id.trim());

            // ==================================================
            // DATABASE CONNECTION
            // ==================================================

            con = DBConnection.getConnection();

            if (con == null) {

                throw new Exception(
                    "Database connection failed."
                );
            }

            // ==================================================
            // GET ASSIGNMENT
            // ==================================================

            String sql =
                    "SELECT id, assignment_name, subject, " +
                    "deadline, deadline_time, priority " +
                    "FROM assignments " +
                    "WHERE id = ? AND user_id = ?";

            ps = con.prepareStatement(sql);

            ps.setInt(1, assignmentId);
            ps.setInt(2, userId);

            rs = ps.executeQuery();

            // ==================================================
            // ASSIGNMENT FOUND
            // ==================================================

            if (rs.next()) {

                String assignmentName =
                        rs.getString("assignment_name");

                String subject =
                        rs.getString("subject");

                String deadline =
                        rs.getString("deadline");

                String deadlineTime =
                        rs.getString("deadline_time");

                String priority =
                        rs.getString("priority");

                // Convert database time HH:mm:ss
                // to HTML time HH:mm

                if (deadlineTime != null &&
                    deadlineTime.length() >= 5) {

                    deadlineTime =
                        deadlineTime.substring(0, 5);
                }

                // ==================================================
                // FORM
                // ==================================================

                out.println(
                    "<form action='" +
                    contextPath +
                    "/UpdateAssignment' method='post'>"
                );

                // Hidden ID

                out.println(
                    "<input type='hidden' " +
                    "name='id' value='" +
                    escapeHtml(String.valueOf(assignmentId)) +
                    "'>"
                );

                // ==================================================
                // ASSIGNMENT NAME
                // ==================================================

                out.println("<div class='form-group'>");

                out.println(
                    "<label>Assignment Name</label>"
                );

                out.println(
                    "<input type='text' " +
                    "name='assignment_name' " +
                    "value='" +
                    escapeHtml(assignmentName) +
                    "' required>"
                );

                out.println("</div>");

                // ==================================================
                // SUBJECT
                // ==================================================

                out.println("<div class='form-group'>");

                out.println(
                    "<label>Subject</label>"
                );

                out.println(
                    "<input type='text' " +
                    "name='subject' " +
                    "value='" +
                    escapeHtml(subject) +
                    "' required>"
                );

                out.println("</div>");

                // ==================================================
                // DEADLINE
                // ==================================================

                out.println("<div class='form-group'>");

                out.println(
                    "<label>Deadline</label>"
                );

                out.println(
                    "<input type='date' " +
                    "name='deadline' " +
                    "value='" +
                    escapeHtml(deadline) +
                    "' required>"
                );

                out.println("</div>");

                // ==================================================
                // TIME
                // ==================================================

                out.println("<div class='form-group'>");

                out.println(
                    "<label>Deadline Time</label>"
                );

                out.println(
                    "<input type='time' " +
                    "name='deadline_time' " +
                    "value='" +
                    escapeHtml(deadlineTime) +
                    "' required>"
                );

                out.println("</div>");

                // ==================================================
                // PRIORITY
                // ==================================================

                out.println("<div class='form-group'>");

                out.println(
                    "<label>Priority</label>"
                );

                out.println(
                    "<select name='priority' required>"
                );

                out.println(
                    "<option value='High' " +
                    selected(priority, "High") +
                    ">High</option>"
                );

                out.println(
                    "<option value='Medium' " +
                    selected(priority, "Medium") +
                    ">Medium</option>"
                );

                out.println(
                    "<option value='Low' " +
                    selected(priority, "Low") +
                    ">Low</option>"
                );

                out.println("</select>");

                out.println("</div>");

                // ==================================================
                // UPDATE BUTTON
                // ==================================================

                out.println(
                    "<input type='submit' " +
                    "value='UPDATE ASSIGNMENT'>"
                );

                out.println("</form>");

            } else {

                // ==================================================
                // NOT FOUND
                // ==================================================

                out.println("<div class='error-box'>");

                out.println(
                    "<h2 class='error'>" +
                    "Assignment Not Found" +
                    "</h2>"
                );

                out.println(
                    "<p class='info'>" +
                    "The assignment does not exist or " +
                    "does not belong to your account." +
                    "</p>"
                );

                out.println("</div>");
            }

        } catch (NumberFormatException e) {

            out.println("<div class='error-box'>");

            out.println(
                "<h2 class='error'>" +
                "Invalid Assignment ID" +
                "</h2>"
            );

            out.println(
                "<p class='info'>" +
                "The assignment ID must be a number." +
                "</p>"
            );

            out.println("</div>");

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<div class='error-box'>");

            out.println(
                "<h2 class='error'>" +
                "Error Loading Assignment" +
                "</h2>"
            );

            out.println(
                "<p class='info'>" +
                escapeHtml(e.getMessage()) +
                "</p>"
            );

            out.println("</div>");

        } finally {

            // ==================================================
            // CLOSE RESULT SET
            // ==================================================

            try {

                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            // ==================================================
            // CLOSE STATEMENT
            // ==================================================

            try {

                if (ps != null) {
                    ps.close();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            // ==================================================
            // CLOSE CONNECTION
            // ==================================================

            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        // ==================================================
        // BACK BUTTON
        // ==================================================

        out.println(
            "<a class='back-btn' " +
            "href='" +
            contextPath +
            "/ViewAssignments'>" +
            "BACK TO ASSIGNMENTS" +
            "</a>"
        );

        out.println("</div>");

        out.println("</div>");

        out.println("</body>");

        out.println("</html>");
    }

    // ==================================================
    // SELECTED OPTION
    // ==================================================

    private String selected(String value, String option) {

        if (value != null &&
            value.equalsIgnoreCase(option)) {

            return "selected";
        }

        return "";
    }

    // ==================================================
    // ESCAPE HTML
    // ==================================================

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
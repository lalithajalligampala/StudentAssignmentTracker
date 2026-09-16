package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String contextPath = request.getContextPath();

        // =========================================================
        // CHECK LOGIN SESSION
        // =========================================================

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect(
                    contextPath + "/login.html"
            );

            return;
        }


        // =========================================================
        // GET LOGGED-IN USER ID
        // =========================================================

        int userId;

        Object userIdObject = session.getAttribute("userId");

        try {

            if (userIdObject instanceof Integer) {

                userId = (Integer) userIdObject;

            } else if (userIdObject instanceof Number) {

                userId = ((Number) userIdObject).intValue();

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


        // =========================================================
        // GET USER NAME
        // =========================================================

        String userName =
                (String) session.getAttribute("userName");

        if (userName == null ||
            userName.trim().isEmpty()) {

            userName = "Student";
        }


        // =========================================================
        // DASHBOARD COUNTERS
        // =========================================================

        int totalAssignments = 0;
        int highPriority = 0;
        int dueSoon = 0;
        int overdue = 0;


        // =========================================================
        // LOAD ONLY CURRENT USER'S ASSIGNMENTS
        // =========================================================

        String sql =
                "SELECT priority, deadline " +
                "FROM assignments " +
                "WHERE user_id = ?";


        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            if (con == null) {

                throw new Exception(
                        "Database connection failed."
                );
            }

            ps.setInt(1, userId);

            System.out.println(
                    "Dashboard loading assignments for user ID: "
                            + userId
            );

            ResultSet rs = ps.executeQuery();

            LocalDate today = LocalDate.now();


            while (rs.next()) {

                // =================================================
                // TOTAL ASSIGNMENTS
                // =================================================

                totalAssignments++;


                // =================================================
                // HIGH PRIORITY
                // =================================================

                String priority =
                        rs.getString("priority");

                if (priority != null &&
                    priority.equalsIgnoreCase("High")) {

                    highPriority++;
                }


                // =================================================
                // DEADLINE
                // =================================================

                java.sql.Date sqlDeadline =
                        rs.getDate("deadline");

                if (sqlDeadline != null) {

                    LocalDate deadline =
                            sqlDeadline.toLocalDate();

                    long daysRemaining =
                            ChronoUnit.DAYS.between(
                                    today,
                                    deadline
                            );


                    // =============================================
                    // OVERDUE
                    // =============================================

                    if (daysRemaining < 0) {

                        overdue++;

                    }

                    // =============================================
                    // DUE SOON
                    // =============================================

                    else if (daysRemaining <= 3) {

                        dueSoon++;
                    }
                }
            }

            rs.close();


            System.out.println(
                    "User ID " + userId +
                    " -> Total Assignments: " +
                    totalAssignments
            );


        } catch (Exception e) {

            e.printStackTrace();


            // =====================================================
            // ERROR PAGE
            // =====================================================

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println(
                    "<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>"
            );

            out.println(
                    "<title>Dashboard Error</title>"
            );


            out.println("<style>");

            out.println(
                    "* {" +
                    "box-sizing: border-box;" +
                    "}"
            );


            // Error page background

            out.println(
                    "body {" +
                    "margin: 0;" +
                    "font-family: Arial, Helvetica, sans-serif;" +
                    "background: linear-gradient(135deg, #eef6ff, #f4fbfa);" +
                    "color: #1f2d3d;" +
                    "display: flex;" +
                    "justify-content: center;" +
                    "align-items: center;" +
                    "min-height: 100vh;" +
                    "}"
            );


            // Error box

            out.println(
                    ".error {" +
                    "background: white;" +
                    "width: 80%;" +
                    "max-width: 600px;" +
                    "padding: 40px;" +
                    "border-radius: 12px;" +
                    "box-shadow: 0 12px 35px rgba(31,60,90,0.12);" +
                    "text-align: center;" +
                    "border: 1px solid #e1e8f0;" +
                    "}"
            );


            // Error heading

            out.println(
                    "h2 {" +
                    "color: #1d2d44;" +
                    "letter-spacing: 2px;" +
                    "}"
            );


            // Error paragraph

            out.println(
                    ".error p {" +
                    "color: #526579;" +
                    "}"
            );


            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='error'>");

            out.println(
                    "<h2>Error Loading Dashboard</h2>"
            );

            out.println(
                    "<p>" +
                    escapeHtml(e.getMessage()) +
                    "</p>"
            );

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

            return;
        }


        // =========================================================
        // HTML PAGE
        // =========================================================

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");

        out.println("<meta charset='UTF-8'>");

        out.println(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
        );

        out.println(
                "<title>Assignment Dashboard</title>"
        );


        // =========================================================
        // CSS
        // PROFESSIONAL LIGHT DESIGN
        // MATCHING ADD.HTML
        // =========================================================

        out.println("<style>");


        // =========================================================
        // RESET
        // =========================================================

        out.println(
                "* {" +
                "box-sizing: border-box;" +
                "}"
        );


        // =========================================================
        // BODY
        // Light blue + mint background
        // =========================================================

        out.println(
                "body {" +
                "margin: 0;" +
                "font-family: Arial, Helvetica, sans-serif;" +
                "background: linear-gradient(135deg, #eef6ff, #f4fbfa);" +
                "color: #1f2d3d;" +
                "min-height: 100vh;" +
                "}"
        );


        // =========================================================
        // DECORATIVE BACKGROUND
        // =========================================================

        out.println(
                "body::before {" +
                "content: '';" +
                "position: fixed;" +
                "width: 420px;" +
                "height: 420px;" +
                "border-radius: 50%;" +
                "background: rgba(74,144,226,0.06);" +
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
                "background: rgba(80,190,170,0.06);" +
                "bottom: -200px;" +
                "right: -180px;" +
                "z-index: -1;" +
                "}"
        );


        // =========================================================
        // HEADER
        // White professional header
        // =========================================================

        out.println(
                ".header {" +
                "background: rgba(255,255,255,0.96);" +
                "color: #1d2d44;" +
                "padding: 35px 20px;" +
                "text-align: center;" +
                "box-shadow: 0 5px 20px rgba(31,60,90,0.10);" +
                "border-bottom: 1px solid #e1e8f0;" +
                "}"
        );


        // =========================================================
        // HEADER TITLE
        // =========================================================

        out.println(
                ".header h1 {" +
                "margin: 0;" +
                "font-size: 34px;" +
                "letter-spacing: 2px;" +
                "font-weight: 600;" +
                "color: #1d2d44;" +
                "}"
        );


        // =========================================================
        // WELCOME
        // =========================================================

        out.println(
                ".welcome {" +
                "margin-top: 12px;" +
                "font-size: 19px;" +
                "color: #526579;" +
                "}"
        );


        // =========================================================
        // MAIN CONTAINER
        // =========================================================

        out.println(
                ".container {" +
                "width: 90%;" +
                "max-width: 1100px;" +
                "margin: 40px auto;" +
                "}"
        );


        // =========================================================
        // CARD
        // White card matching add.html
        // =========================================================

        out.println(
                ".card {" +
                "background: rgba(255,255,255,0.97);" +
                "padding: 30px;" +
                "margin-bottom: 28px;" +
                "border-radius: 12px;" +
                "box-shadow: 0 12px 35px rgba(31,60,90,0.12), 0 2px 8px rgba(31,60,90,0.06);" +
                "border: 1px solid rgba(212,220,230,0.7);" +
                "}"
        );


        // =========================================================
        // CARD HEADING
        // =========================================================

        out.println(
                ".card h2 {" +
                "margin-top: 0;" +
                "margin-bottom: 25px;" +
                "color: #1d2d44;" +
                "font-size: 29px;" +
                "letter-spacing: 1px;" +
                "}"
        );


        // =========================================================
        // STATISTICS
        // =========================================================

        out.println(
                ".stats {" +
                "display: flex;" +
                "gap: 20px;" +
                "flex-wrap: wrap;" +
                "}"
        );


        // =========================================================
        // STAT CARD
        // Light professional blue
        // =========================================================

        out.println(
                ".stat {" +
                "flex: 1;" +
                "min-width: 180px;" +
                "padding: 25px 20px;" +
                "border-radius: 8px;" +
                "text-align: center;" +
                "background: #eaf3ff;" +
                "border: 1px solid #d4e3f5;" +
                "color: #1d2d44;" +
                "transition: transform 0.2s, box-shadow 0.2s;" +
                "}"
        );


        // =========================================================
        // STAT CARD HOVER
        // =========================================================

        out.println(
                ".stat:hover {" +
                "transform: translateY(-2px);" +
                "box-shadow: 0 6px 18px rgba(57,120,216,0.10);" +
                "}"
        );


        // =========================================================
        // STAT HEADING
        // =========================================================

        out.println(
                ".stat h3 {" +
                "margin: 0 0 12px 0;" +
                "font-size: 19px;" +
                "letter-spacing: 0.5px;" +
                "color: #526579;" +
                "}"
        );


        // =========================================================
        // NUMBERS
        // =========================================================

        out.println(
                ".number {" +
                "font-size: 34px;" +
                "font-weight: bold;" +
                "margin: 0;" +
                "color: #3978d8;" +
                "}"
        );


        // =========================================================
        // QUICK ACTION MENU
        // =========================================================

        out.println(
                ".menu {" +
                "display: flex;" +
                "gap: 15px;" +
                "flex-wrap: wrap;" +
                "}"
        );


        // =========================================================
        // BUTTON
        // Professional blue
        // Same as ADD ASSIGNMENT button
        // =========================================================

        out.println(
                ".button {" +
                "display: inline-block;" +
                "padding: 14px 20px;" +
                "background: #3978d8;" +
                "color: white;" +
                "text-decoration: none;" +
                "border-radius: 7px;" +
                "font-weight: bold;" +
                "font-size: 16px;" +
                "letter-spacing: 0.5px;" +
                "transition: all 0.2s ease;" +
                "}"
        );


        // =========================================================
        // BUTTON HOVER
        // =========================================================

        out.println(
                ".button:hover {" +
                "background: #2f69c2;" +
                "transform: translateY(-1px);" +
                "box-shadow: 0 5px 14px rgba(57,120,216,0.20);" +
                "}"
        );


        // =========================================================
        // MOBILE RESPONSIVE DESIGN
        // =========================================================

        out.println(
                "@media (max-width: 600px) {" +

                ".header {" +
                "padding: 28px 15px;" +
                "}" +

                ".header h1 {" +
                "font-size: 26px;" +
                "letter-spacing: 1px;" +
                "}" +

                ".welcome {" +
                "font-size: 16px;" +
                "}" +

                ".container {" +
                "width: 94%;" +
                "margin: 25px auto;" +
                "}" +

                ".card {" +
                "padding: 22px;" +
                "}" +

                ".card h2 {" +
                "font-size: 24px;" +
                "}" +

                ".stats {" +
                "flex-direction: column;" +
                "}" +

                ".stat {" +
                "width: 100%;" +
                "}" +

                ".button {" +
                "width: 100%;" +
                "text-align: center;" +
                "}" +

                ".menu {" +
                "flex-direction: column;" +
                "}" +

                "}"
        );


        out.println("</style>");

        out.println("</head>");


        // =========================================================
        // BODY
        // =========================================================

        out.println("<body>");


        // =========================================================
        // HEADER
        // =========================================================

        out.println("<div class='header'>");

        out.println(
                "<h1>" +
                "Student Assignment &amp; Deadline Tracker" +
                "</h1>"
        );

        out.println(
                "<div class='welcome'>" +
                "Welcome, " +
                escapeHtml(userName) +
                "</div>"
        );

        out.println("</div>");


        // =========================================================
        // MAIN CONTAINER
        // =========================================================

        out.println("<div class='container'>");


        // =========================================================
        // DASHBOARD CARD
        // =========================================================

        out.println("<div class='card'>");

        out.println(
                "<h2>Assignment Dashboard</h2>"
        );

        out.println("<div class='stats'>");


        // =========================================================
        // TOTAL ASSIGNMENTS
        // =========================================================

        out.println("<div class='stat'>");

        out.println(
                "<h3>Total Assignments</h3>"
        );

        out.println(
                "<p class='number'>" +
                totalAssignments +
                "</p>"
        );

        out.println("</div>");


        // =========================================================
        // HIGH PRIORITY
        // =========================================================

        out.println("<div class='stat'>");

        out.println(
                "<h3>High Priority</h3>"
        );

        out.println(
                "<p class='number'>" +
                highPriority +
                "</p>"
        );

        out.println("</div>");


        // =========================================================
        // DUE SOON
        // =========================================================

        out.println("<div class='stat'>");

        out.println(
                "<h3>Due Soon</h3>"
        );

        out.println(
                "<p class='number'>" +
                dueSoon +
                "</p>"
        );

        out.println("</div>");


        // =========================================================
        // OVERDUE
        // =========================================================

        out.println("<div class='stat'>");

        out.println(
                "<h3>Overdue</h3>"
        );

        out.println(
                "<p class='number'>" +
                overdue +
                "</p>"
        );

        out.println("</div>");

        out.println("</div>");

        out.println("</div>");


        // =========================================================
        // QUICK ACTIONS
        // =========================================================

        out.println("<div class='card'>");

        out.println(
                "<h2>Quick Actions</h2>"
        );

        out.println("<div class='menu'>");


        // =========================================================
        // ADD ASSIGNMENT
        // =========================================================

        out.println(
                "<a class='button' href='" +
                contextPath +
                "/add.html'>" +
                "Add Assignment</a>"
        );


        // =========================================================
        // VIEW ASSIGNMENTS
        // =========================================================

        out.println(
                "<a class='button' href='" +
                contextPath +
                "/ViewAssignments'>" +
                "View Assignments</a>"
        );


        // =========================================================
        // SEARCH ASSIGNMENTS
        // =========================================================

        out.println(
                "<a class='button' href='" +
                contextPath +
                "/search.html'>" +
                "Search Assignments</a>"
        );


        // =========================================================
        // FILTER BY PRIORITY
        // =========================================================

        out.println(
                "<a class='button' href='" +
                contextPath +
                "/priority.html'>" +
                "Filter by Priority</a>"
        );


        // =========================================================
        // SORT BY DEADLINE
        // =========================================================

        out.println(
                "<a class='button' href='" +
                contextPath +
                "/DeadlineSort'>" +
                "Sort by Deadline</a>"
        );


        out.println("</div>");

        out.println("</div>");


        // =========================================================
        // END CONTAINER
        // =========================================================

        out.println("</div>");

        out.println("</body>");

        out.println("</html>");
    }


    // =============================================================
    // ESCAPE HTML
    // =============================================================

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
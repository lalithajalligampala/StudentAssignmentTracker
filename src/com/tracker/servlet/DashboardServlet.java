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

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String contextPath = request.getContextPath();

        /*
         * Check login session
         */
        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect(
                contextPath + "/login.html"
            );

            return;
        }

        /*
         * Get logged-in user's ID
         */
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

        int totalAssignments = 0;
        int highPriority = 0;
        int dueSoon = 0;
        int overdue = 0;

        /*
         * Load assignments ONLY for the logged-in user.
         *
         * IMPORTANT:
         * Do not remove the WHERE user_id = ? condition.
         */
        try {

            Connection con =
                    DBConnection.getConnection();

            if (con == null) {

                throw new Exception(
                    "Database connection failed."
                );
            }

            String sql =
                    "SELECT priority, deadline " +
                    "FROM assignments " +
                    "WHERE user_id = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, userId);

            ResultSet rs =
                    ps.executeQuery();

            LocalDate today =
                    LocalDate.now();

            while (rs.next()) {

                totalAssignments++;

                String priority =
                        rs.getString("priority");

                if ("High".equalsIgnoreCase(priority)) {

                    highPriority++;
                }

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

                    if (daysRemaining < 0) {

                        overdue++;

                    } else if (daysRemaining <= 3) {

                        dueSoon++;
                    }
                }
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Dashboard Error</title>");

            out.println("<style>");

            out.println(
                "body {" +
                "font-family: Arial, sans-serif;" +
                "background-color: #f4f6f8;" +
                "text-align: center;" +
                "padding-top: 80px;" +
                "}"
            );

            out.println(
                ".error {" +
                "background-color: white;" +
                "width: 80%;" +
                "max-width: 600px;" +
                "margin: auto;" +
                "padding: 30px;" +
                "border-radius: 10px;" +
                "box-shadow: 0 2px 8px rgba(0,0,0,0.12);" +
                "}"
            );

            out.println(
                "h2 { color: #c0392b; }"
            );

            out.println(
                ".button {" +
                "display: inline-block;" +
                "padding: 12px 20px;" +
                "background-color: #2c3e50;" +
                "color: white;" +
                "text-decoration: none;" +
                "border-radius: 6px;" +
                "font-weight: bold;" +
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
                e.getMessage() +
                "</p>"
            );

            out.println(
                "<a class='button' href='" +
                contextPath +
                "/index.html'>" +
                "Back to Home" +
                "</a>"
            );

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

            e.printStackTrace();

            return;
        }

        /*
         * HTML PAGE
         */

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");

        out.println(
            "<meta charset='UTF-8'>"
        );

        out.println(
            "<title>Assignment Dashboard</title>"
        );

        out.println("<style>");

        out.println(
            "body {" +
            "font-family: Arial, sans-serif;" +
            "margin: 0;" +
            "background-color: #f4f6f8;" +
            "color: #333;" +
            "}"
        );

        out.println(
            ".header {" +
            "background-color: #2c3e50;" +
            "color: white;" +
            "padding: 30px;" +
            "text-align: center;" +
            "}"
        );

        out.println(
            ".header h1 {" +
            "margin: 0;" +
            "font-size: 32px;" +
            "}"
        );

        out.println(
            ".container {" +
            "width: 90%;" +
            "max-width: 1100px;" +
            "margin: 35px auto;" +
            "}"
        );

        out.println(
            ".card {" +
            "background-color: white;" +
            "padding: 25px;" +
            "margin-bottom: 25px;" +
            "border-radius: 10px;" +
            "box-shadow: 0 2px 8px rgba(0,0,0,0.12);" +
            "}"
        );

        out.println(
            ".card h2 {" +
            "margin-top: 0;" +
            "color: #2c3e50;" +
            "}"
        );

        out.println(
            ".stats {" +
            "display: flex;" +
            "gap: 20px;" +
            "flex-wrap: wrap;" +
            "}"
        );

        out.println(
            ".stat {" +
            "flex: 1;" +
            "min-width: 180px;" +
            "padding: 20px;" +
            "border-radius: 8px;" +
            "text-align: center;" +
            "background-color: #f8f9fa;" +
            "}"
        );

        out.println(
            ".stat h3 {" +
            "margin: 0 0 10px 0;" +
            "}"
        );

        out.println(
            ".number {" +
            "font-size: 32px;" +
            "font-weight: bold;" +
            "margin: 0;" +
            "}"
        );

        out.println(
            ".menu {" +
            "display: flex;" +
            "gap: 15px;" +
            "flex-wrap: wrap;" +
            "}"
        );

        out.println(
            ".button {" +
            "display: inline-block;" +
            "padding: 12px 18px;" +
            "background-color: #3498db;" +
            "color: white;" +
            "text-decoration: none;" +
            "border-radius: 6px;" +
            "font-weight: bold;" +
            "}"
        );

        out.println(
            ".button:hover {" +
            "background-color: #217dbb;" +
            "}"
        );

        out.println(
            ".home-button {" +
            "background-color: #2c3e50;" +
            "}"
        );

        out.println("</style>");

        out.println("</head>");

        out.println("<body>");

        /*
         * Header
         */

        out.println("<div class='header'>");

        out.println(
            "<h1>" +
            "Student Assignment &amp; Deadline Tracker" +
            "</h1>"
        );

        out.println("</div>");

        /*
         * Main container
         */

        out.println("<div class='container'>");

        /*
         * Dashboard statistics
         */

        out.println("<div class='card'>");

        out.println(
            "<h2>Assignment Dashboard</h2>"
        );

        out.println("<div class='stats'>");

        /*
         * Total Assignments
         */

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

        /*
         * High Priority
         */

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

        /*
         * Due Soon
         */

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

        /*
         * Overdue
         */

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

        /*
         * Quick Actions
         */

        out.println("<div class='card'>");

        out.println(
            "<h2>Quick Actions</h2>"
        );

        out.println("<div class='menu'>");

        out.println(
            "<a class='button' href='" +
            contextPath +
            "/add.html'>" +
            "Add Assignment</a>"
        );

        out.println(
            "<a class='button' href='" +
            contextPath +
            "/ViewAssignments'>" +
            "View Assignments</a>"
        );

        out.println(
            "<a class='button' href='" +
            contextPath +
            "/search.html'>" +
            "Search Assignments</a>"
        );

        out.println(
            "<a class='button' href='" +
            contextPath +
            "/priority.html'>" +
            "Filter by Priority</a>"
        );

        out.println(
            "<a class='button' href='" +
            contextPath +
            "/DeadlineSort'>" +
            "Sort by Deadline</a>"
        );

        out.println("</div>");
        out.println("</div>");

        /*
         * Back to Home
         */

        out.println(
            "<a class='button home-button' href='" +
            contextPath +
            "/index.html'>" +
            "Back to Home</a>"
        );

        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
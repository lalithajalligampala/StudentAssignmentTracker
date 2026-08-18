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

public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        int totalAssignments = 0;
        int highPriority = 0;
        int dueSoon = 0;
        int overdue = 0;

        try {

            Connection con = DBConnection.getConnection();

            String sql = "SELECT priority, deadline FROM assignments";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            LocalDate today = LocalDate.now();

            while (rs.next()) {

                totalAssignments++;

                String priority = rs.getString("priority");

                if ("High".equalsIgnoreCase(priority)) {
                    highPriority++;
                }

                LocalDate deadline =
                        rs.getDate("deadline").toLocalDate();

                long daysRemaining =
                        ChronoUnit.DAYS.between(today, deadline);

                if (daysRemaining < 0) {

                    overdue++;

                } else if (daysRemaining <= 3) {

                    dueSoon++;
                }
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<p>Error loading dashboard: "
                    + e.getMessage() + "</p>");

            return;
        }

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");
        out.println("<title>Assignment Dashboard</title>");

        out.println("<style>");

        out.println("body {");
        out.println("font-family: Arial, sans-serif;");
        out.println("margin: 0;");
        out.println("background-color: #f4f6f8;");
        out.println("color: #333;");
        out.println("}");

        out.println(".header {");
        out.println("background-color: #2c3e50;");
        out.println("color: white;");
        out.println("padding: 30px;");
        out.println("text-align: center;");
        out.println("}");

        out.println(".header h1 {");
        out.println("margin: 0;");
        out.println("font-size: 32px;");
        out.println("}");

        out.println(".container {");
        out.println("width: 90%;");
        out.println("max-width: 1100px;");
        out.println("margin: 35px auto;");
        out.println("}");

        out.println(".card {");
        out.println("background-color: white;");
        out.println("padding: 25px;");
        out.println("margin-bottom: 25px;");
        out.println("border-radius: 10px;");
        out.println("box-shadow: 0 2px 8px rgba(0,0,0,0.12);");
        out.println("}");

        out.println(".card h2 {");
        out.println("margin-top: 0;");
        out.println("color: #2c3e50;");
        out.println("}");

        out.println(".stats {");
        out.println("display: flex;");
        out.println("gap: 20px;");
        out.println("flex-wrap: wrap;");
        out.println("}");

        out.println(".stat {");
        out.println("flex: 1;");
        out.println("min-width: 180px;");
        out.println("padding: 20px;");
        out.println("border-radius: 8px;");
        out.println("text-align: center;");
        out.println("background-color: #f8f9fa;");
        out.println("}");

        out.println(".stat h3 {");
        out.println("margin: 0 0 10px 0;");
        out.println("}");

        out.println(".number {");
        out.println("font-size: 32px;");
        out.println("font-weight: bold;");
        out.println("margin: 0;");
        out.println("}");

        out.println(".menu {");
        out.println("display: flex;");
        out.println("gap: 15px;");
        out.println("flex-wrap: wrap;");
        out.println("}");

        out.println(".button {");
        out.println("display: inline-block;");
        out.println("padding: 12px 18px;");
        out.println("background-color: #3498db;");
        out.println("color: white;");
        out.println("text-decoration: none;");
        out.println("border-radius: 6px;");
        out.println("font-weight: bold;");
        out.println("}");

        out.println(".button:hover {");
        out.println("background-color: #217dbb;");
        out.println("}");

        out.println(".home-button {");
        out.println("background-color: #2c3e50;");
        out.println("}");

        out.println("</style>");
        out.println("</head>");

        out.println("<body>");

        out.println("<div class='header'>");
        out.println("<h1>Student Assignment &amp; Deadline Tracker</h1>");
        out.println("</div>");

        out.println("<div class='container'>");

        out.println("<div class='card'>");
        out.println("<h2>Assignment Dashboard</h2>");

        out.println("<div class='stats'>");

        out.println("<div class='stat'>");
        out.println("<h3>Total Assignments</h3>");
        out.println("<p class='number'>" +
                totalAssignments +
                "</p>");
        out.println("</div>");

        out.println("<div class='stat'>");
        out.println("<h3>High Priority</h3>");
        out.println("<p class='number'>" +
                highPriority +
                "</p>");
        out.println("</div>");

        out.println("<div class='stat'>");
        out.println("<h3>Due Soon</h3>");
        out.println("<p class='number'>" +
                dueSoon +
                "</p>");
        out.println("</div>");

        out.println("<div class='stat'>");
        out.println("<h3>Overdue</h3>");
        out.println("<p class='number'>" +
                overdue +
                "</p>");
        out.println("</div>");

        out.println("</div>");
        out.println("</div>");

        out.println("<div class='card'>");

        out.println("<h2>Quick Actions</h2>");

        out.println("<div class='menu'>");

        out.println(
            "<a class='button' href='add.html'>" +
            "Add Assignment</a>"
        );

        out.println(
            "<a class='button' href='ViewAssignments'>" +
            "View Assignments</a>"
        );

        out.println(
            "<a class='button' href='search.html'>" +
            "Search Assignments</a>"
        );

        out.println(
            "<a class='button' href='priority.html'>" +
            "Filter by Priority</a>"
        );

        out.println(
            "<a class='button' href='DeadlineSort'>" +
            "Sort by Deadline</a>"
        );

        out.println("</div>");
        out.println("</div>");

        out.println(
            "<a class='button home-button' " +
            "href='index.html'>" +
            "Back to Home</a>"
        );

        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
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

public class ViewAssignmentsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");
        out.println("<title>View Assignments</title>");

        out.println("<style>");

        out.println("body {");
        out.println("    font-family: Arial, sans-serif;");
        out.println("    margin: 0;");
        out.println("    background-color: #f4f6f8;");
        out.println("    color: #333;");
        out.println("}");

        out.println(".header {");
        out.println("    background-color: #2c3e50;");
        out.println("    color: white;");
        out.println("    padding: 25px;");
        out.println("    text-align: center;");
        out.println("}");

        out.println(".header h1 {");
        out.println("    margin: 0;");
        out.println("    font-size: 30px;");
        out.println("}");

        out.println(".container {");
        out.println("    width: 92%;");
        out.println("    max-width: 1200px;");
        out.println("    margin: 30px auto;");
        out.println("}");

        out.println(".card {");
        out.println("    background-color: white;");
        out.println("    padding: 25px;");
        out.println("    border-radius: 10px;");
        out.println("    box-shadow: 0 2px 8px rgba(0,0,0,0.12);");
        out.println("    overflow-x: auto;");
        out.println("}");

        out.println(".card h2 {");
        out.println("    margin-top: 0;");
        out.println("    color: #2c3e50;");
        out.println("}");

        out.println("table {");
        out.println("    width: 100%;");
        out.println("    border-collapse: collapse;");
        out.println("    margin-top: 20px;");
        out.println("}");

        out.println("th {");
        out.println("    background-color: #3498db;");
        out.println("    color: white;");
        out.println("    padding: 14px;");
        out.println("    text-align: center;");
        out.println("}");

        out.println("td {");
        out.println("    padding: 12px;");
        out.println("    text-align: center;");
        out.println("    border-bottom: 1px solid #ddd;");
        out.println("}");

        out.println("tr:hover {");
        out.println("    background-color: #f5f5f5;");
        out.println("}");

        out.println(".status {");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".overdue {");
        out.println("    color: #c0392b;");
        out.println("}");

        out.println(".due-soon {");
        out.println("    color: #e67e22;");
        out.println("}");

        out.println(".upcoming {");
        out.println("    color: #27ae60;");
        out.println("}");

        out.println(".edit-btn, .delete-btn {");
        out.println("    display: inline-block;");
        out.println("    padding: 7px 12px;");
        out.println("    border-radius: 5px;");
        out.println("    text-decoration: none;");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".edit-btn {");
        out.println("    background-color: #3498db;");
        out.println("    color: white;");
        out.println("}");

        out.println(".delete-btn {");
        out.println("    background-color: #e74c3c;");
        out.println("    color: white;");
        out.println("}");

        out.println(".edit-btn:hover {");
        out.println("    background-color: #217dbb;");
        out.println("}");

        out.println(".delete-btn:hover {");
        out.println("    background-color: #c0392b;");
        out.println("}");

        out.println(".home-btn {");
        out.println("    display: inline-block;");
        out.println("    margin-top: 20px;");
        out.println("    padding: 12px 20px;");
        out.println("    background-color: #2c3e50;");
        out.println("    color: white;");
        out.println("    text-decoration: none;");
        out.println("    border-radius: 6px;");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".home-btn:hover {");
        out.println("    background-color: #1f2d3a;");
        out.println("}");

        out.println("</style>");
        out.println("</head>");

        out.println("<body>");

        out.println("<div class='header'>");
        out.println("<h1>Student Assignment Tracker</h1>");
        out.println("</div>");

        out.println("<div class='container'>");

        out.println("<div class='card'>");

        out.println("<h2>All Assignments</h2>");

        out.println("<table>");

        out.println("<tr>");
        out.println("<th>S.No.</th>");
        out.println("<th>Assignment Name</th>");
        out.println("<th>Subject</th>");
        out.println("<th>Deadline</th>");
        out.println("<th>Priority</th>");
        out.println("<th>Deadline Status</th>");
        out.println("<th>Action</th>");
        out.println("</tr>");

        try {

            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM assignments";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            int serialNumber = 1;

            while (rs.next()) {

                int id = rs.getInt("id");

                String assignmentName =
                        rs.getString("assignment_name");

                String subject =
                        rs.getString("subject");

                LocalDate deadline =
                        rs.getDate("deadline").toLocalDate();

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

                out.println("<tr>");

                // Display S.No. instead of database ID
                out.println("<td>" +
                        serialNumber +
                        "</td>");

                out.println("<td>" +
                        assignmentName +
                        "</td>");

                out.println("<td>" +
                        subject +
                        "</td>");

                out.println("<td>" +
                        deadline +
                        "</td>");

                out.println("<td>" +
                        priority +
                        "</td>");

                out.println("<td class='status " +
                        statusClass +
                        "'>" +
                        deadlineStatus +
                        "</td>");

                out.println("<td>");

                // Database ID is still used internally
                out.println(
                    "<a class='edit-btn' " +
                    "href='/StudentAssignmentTracker/EditAssignment?id=" +
                    id +
                    "'>Edit</a>"
                );

                out.println("&nbsp;");

                // Database ID is still used internally
                out.println(
                    "<a class='delete-btn' " +
                    "href='/StudentAssignmentTracker/DeleteAssignment?id=" +
                    id +
                    "'>Delete</a>"
                );

                out.println("</td>");

                out.println("</tr>");

                serialNumber++;
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<tr>");

            out.println("<td colspan='7'>");

            out.println("Error: " +
                    e.getMessage());

            out.println("</td>");

            out.println("</tr>");
        }

        out.println("</table>");

        out.println(
            "<a class='home-btn' " +
            "href='/StudentAssignmentTracker/index.html'>" +
            "Back to Home" +
            "</a>"
        );

        out.println("</div>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
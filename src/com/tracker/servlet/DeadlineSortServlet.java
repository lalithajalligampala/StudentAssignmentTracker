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

public class DeadlineSortServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");
        out.println("<title>Assignments by Deadline</title>");

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
        out.println("}");

        out.println(".container {");
        out.println("    width: 92%;");
        out.println("    max-width: 1100px;");
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
        out.println("}");

        out.println("td {");
        out.println("    padding: 12px;");
        out.println("    text-align: center;");
        out.println("    border-bottom: 1px solid #ddd;");
        out.println("}");

        out.println("tr:hover {");
        out.println("    background-color: #f5f5f5;");
        out.println("}");

        out.println(".button {");
        out.println("    display: inline-block;");
        out.println("    margin-top: 20px;");
        out.println("    margin-right: 10px;");
        out.println("    padding: 12px 20px;");
        out.println("    background-color: #2c3e50;");
        out.println("    color: white;");
        out.println("    text-decoration: none;");
        out.println("    border-radius: 6px;");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".button:hover {");
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

        out.println("<h2>Assignments Sorted by Deadline</h2>");

        try {

            Connection con = DBConnection.getConnection();

            String sql =
                "SELECT * FROM assignments ORDER BY deadline ASC";

            PreparedStatement ps =
                con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            out.println("<table>");

            out.println("<tr>");
            out.println("<th>S.No.</th>");
            out.println("<th>Assignment Name</th>");
            out.println("<th>Subject</th>");
            out.println("<th>Deadline</th>");
            out.println("<th>Priority</th>");
            out.println("</tr>");

            boolean found = false;

            int serialNumber = 1;

            while (rs.next()) {

                found = true;

                out.println("<tr>");

                out.println("<td>" +
                        serialNumber +
                        "</td>");

                out.println("<td>" +
                        rs.getString("assignment_name") +
                        "</td>");

                out.println("<td>" +
                        rs.getString("subject") +
                        "</td>");

                out.println("<td>" +
                        rs.getDate("deadline") +
                        "</td>");

                out.println("<td>" +
                        rs.getString("priority") +
                        "</td>");

                out.println("</tr>");

                serialNumber++;
            }

            out.println("</table>");

            if (!found) {

                out.println("<p>No assignments found.</p>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println(
                "<p>Error: " +
                e.getMessage() +
                "</p>"
            );
        }

        out.println(
            "<a class='button' href='ViewAssignments'>" +
            "Back to All Assignments" +
            "</a>"
        );

        out.println(
            "<a class='button' href='index.html'>" +
            "Back to Home" +
            "</a>"
        );

        out.println("</div>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
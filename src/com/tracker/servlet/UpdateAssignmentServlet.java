package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateAssignmentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        // Automatically works locally and on Render
        String contextPath = request.getContextPath();

        String id = request.getParameter("id");
        String assignmentName =
                request.getParameter("assignment_name");
        String subject =
                request.getParameter("subject");
        String deadline =
                request.getParameter("deadline");
        String priority =
                request.getParameter("priority");

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");
        out.println("<title>Update Assignment</title>");

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
        out.println("    width: 90%;");
        out.println("    max-width: 600px;");
        out.println("    margin: 50px auto;");
        out.println("}");

        out.println(".card {");
        out.println("    background-color: white;");
        out.println("    padding: 35px;");
        out.println("    border-radius: 10px;");
        out.println("    box-shadow: 0 2px 8px rgba(0,0,0,0.12);");
        out.println("    text-align: center;");
        out.println("}");

        out.println(".success {");
        out.println("    color: #27ae60;");
        out.println("    margin-bottom: 15px;");
        out.println("}");

        out.println(".error {");
        out.println("    color: #c0392b;");
        out.println("    margin-bottom: 15px;");
        out.println("}");

        out.println(".info {");
        out.println("    color: #555;");
        out.println("    margin-bottom: 25px;");
        out.println("}");

        out.println(".button {");
        out.println("    display: inline-block;");
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
        out.println(
            "<h1>Student Assignment &amp; Deadline Tracker</h1>"
        );
        out.println("</div>");

        out.println("<div class='container'>");

        out.println("<div class='card'>");

        try {

            // Check database connection
            Connection con = DBConnection.getConnection();

            if (con == null) {
                throw new Exception(
                    "Database connection failed."
                );
            }

            // Update assignment
            String sql =
                    "UPDATE assignments SET " +
                    "assignment_name = ?, " +
                    "subject = ?, " +
                    "deadline = ?, " +
                    "priority = ? " +
                    "WHERE id = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, assignmentName);
            ps.setString(2, subject);
            ps.setString(3, deadline);
            ps.setString(4, priority);
            ps.setInt(5, Integer.parseInt(id));

            int rowsUpdated =
                    ps.executeUpdate();

            if (rowsUpdated > 0) {

                out.println(
                    "<h2 class='success'>" +
                    "Assignment Updated Successfully!" +
                    "</h2>"
                );

                out.println(
                    "<p class='info'>" +
                    "Your assignment details have been " +
                    "updated successfully." +
                    "</p>"
                );

            } else {

                out.println(
                    "<h2 class='error'>" +
                    "Assignment Not Found" +
                    "</h2>"
                );

                out.println(
                    "<p class='info'>" +
                    "No assignment was found with " +
                    "the specified ID." +
                    "</p>"
                );
            }

            ps.close();
            con.close();

        } catch (NumberFormatException e) {

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

        } catch (Exception e) {

            out.println(
                "<h2 class='error'>" +
                "Error Updating Assignment" +
                "</h2>"
            );

            out.println(
                "<p class='info'>" +
                e.getMessage() +
                "</p>"
            );
        }

        // Automatically works locally and on Render
        out.println(
            "<a class='button' " +
            "href='" +
            contextPath +
            "/ViewAssignments'>" +
            "Back to Assignments" +
            "</a>"
        );

        out.println("</div>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
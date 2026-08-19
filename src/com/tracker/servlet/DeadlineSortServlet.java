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

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

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

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");

        out.println("<meta charset='UTF-8'>");

        out.println(
            "<title>Assignments by Deadline</title>"
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
            "padding: 25px;" +
            "text-align: center;" +
            "}"
        );

        out.println(
            ".header h1 {" +
            "margin: 0;" +
            "}"
        );

        out.println(
            ".container {" +
            "width: 92%;" +
            "max-width: 1100px;" +
            "margin: 30px auto;" +
            "}"
        );

        out.println(
            ".card {" +
            "background-color: white;" +
            "padding: 25px;" +
            "border-radius: 10px;" +
            "box-shadow: 0 2px 8px rgba(0,0,0,0.12);" +
            "overflow-x: auto;" +
            "}"
        );

        out.println(
            ".card h2 {" +
            "color: #2c3e50;" +
            "}"
        );

        out.println(
            "table {" +
            "width: 100%;" +
            "border-collapse: collapse;" +
            "margin-top: 20px;" +
            "}"
        );

        out.println(
            "th {" +
            "background-color: #3498db;" +
            "color: white;" +
            "padding: 14px;" +
            "}"
        );

        out.println(
            "td {" +
            "padding: 12px;" +
            "text-align: center;" +
            "border-bottom: 1px solid #ddd;" +
            "}"
        );

        out.println(
            "tr:hover {" +
            "background-color: #f5f5f5;" +
            "}"
        );

        out.println(
            ".button {" +
            "display: inline-block;" +
            "margin-top: 20px;" +
            "margin-right: 10px;" +
            "padding: 12px 20px;" +
            "background-color: #2c3e50;" +
            "color: white;" +
            "text-decoration: none;" +
            "border-radius: 6px;" +
            "font-weight: bold;" +
            "}"
        );

        out.println(
            ".button:hover {" +
            "background-color: #1f2d3a;" +
            "}"
        );

        out.println(
            ".error {" +
            "color: #c0392b;" +
            "}"
        );

        out.println("</style>");

        out.println("</head>");

        out.println("<body>");

        out.println("<div class='header'>");

        out.println(
            "<h1>Student Assignment Tracker</h1>"
        );

        out.println("</div>");

        out.println("<div class='container'>");

        out.println("<div class='card'>");

        out.println(
            "<h2>Assignments Sorted by Deadline</h2>"
        );

        try {

            Connection con =
                    DBConnection.getConnection();

            if (con == null) {

                throw new Exception(
                    "Database connection failed."
                );
            }

            /*
             * IMPORTANT:
             * Show ONLY assignments belonging to
             * the currently logged-in user.
             *
             * ORDER BY deadline ASC keeps the
             * assignments sorted by deadline.
             */
            String sql =
                    "SELECT assignment_name, subject, " +
                    "deadline, priority " +
                    "FROM assignments " +
                    "WHERE user_id = ? " +
                    "ORDER BY deadline ASC";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, userId);

            ResultSet rs =
                    ps.executeQuery();

            out.println("<table>");

            out.println("<tr>");

            out.println("<th>S.No.</th>");

            out.println(
                "<th>Assignment Name</th>"
            );

            out.println("<th>Subject</th>");

            out.println("<th>Deadline</th>");

            out.println("<th>Priority</th>");

            out.println("</tr>");

            boolean found = false;

            int serialNumber = 1;

            while (rs.next()) {

                found = true;

                out.println("<tr>");

                out.println(
                    "<td>" +
                    serialNumber +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    rs.getString("assignment_name") +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    rs.getString("subject") +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    rs.getDate("deadline") +
                    "</td>"
                );

                out.println(
                    "<td>" +
                    rs.getString("priority") +
                    "</td>"
                );

                out.println("</tr>");

                serialNumber++;
            }

            out.println("</table>");

            if (!found) {

                out.println(
                    "<p>No assignments found.</p>"
                );
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println(
                "<p class='error'>" +
                "<b>Error loading assignments:</b> " +
                e.getMessage() +
                "</p>"
            );

            e.printStackTrace();
        }

        out.println(
            "<a class='button' href='" +
            contextPath +
            "/ViewAssignments'>" +
            "Back to All Assignments" +
            "</a>"
        );

        out.println(
            "<a class='button' href='" +
            contextPath +
            "/index.html'>" +
            "Back to Home" +
            "</a>"
        );

        out.println("</div>");

        out.println("</div>");

        out.println("</body>");

        out.println("</html>");
    }
}
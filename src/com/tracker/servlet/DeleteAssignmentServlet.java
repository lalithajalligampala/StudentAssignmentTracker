package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DeleteAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // Automatically works locally and on Render
        String contextPath = request.getContextPath();

        // Check whether user is logged in
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(contextPath + "/login.html");
            return;
        }

        // Get logged-in user's ID
        int userId = (Integer) session.getAttribute("userId");

        // Get assignment ID
        String id = request.getParameter("id");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");
        out.println("<title>Delete Assignment</title>");

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
        out.println("}");

        out.println(".error {");
        out.println("    color: #c0392b;");
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
        out.println("<h1>Student Assignment &amp; Deadline Tracker</h1>");
        out.println("</div>");

        out.println("<div class='container'>");
        out.println("<div class='card'>");

        if (id == null || id.trim().isEmpty()) {

            out.println(
                "<h2 class='error'>" +
                "Invalid Assignment ID" +
                "</h2>"
            );

            out.println(
                "<p class='info'>" +
                "No valid assignment ID was provided." +
                "</p>"
            );

        } else {

            Connection con = null;
            PreparedStatement ps = null;

            try {

                int assignmentId = Integer.parseInt(id);

                con = DBConnection.getConnection();

                if (con == null) {
                    throw new Exception(
                        "Database connection failed."
                    );
                }

                /*
                 * IMPORTANT:
                 *
                 * Delete ONLY if the assignment belongs
                 * to the currently logged-in user.
                 */
                String sql =
                    "DELETE FROM assignments " +
                    "WHERE id = ? AND user_id = ?";

                ps = con.prepareStatement(sql);

                ps.setInt(1, assignmentId);
                ps.setInt(2, userId);

                int rowsDeleted = ps.executeUpdate();

                if (rowsDeleted > 0) {

                    out.println(
                        "<h2 class='success'>" +
                        "Assignment Deleted Successfully!" +
                        "</h2>"
                    );

                    out.println(
                        "<p class='info'>" +
                        "The assignment has been removed " +
                        "from your tracker." +
                        "</p>"
                    );

                } else {

                    /*
                     * This happens if:
                     * 1. Assignment does not exist, OR
                     * 2. Assignment belongs to another user.
                     */
                    out.println(
                        "<h2 class='error'>" +
                        "Assignment Not Found" +
                        "</h2>"
                    );

                    out.println(
                        "<p class='info'>" +
                        "The assignment does not exist " +
                        "or does not belong to your account." +
                        "</p>"
                    );
                }

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
                    "Error Deleting Assignment" +
                    "</h2>"
                );

                out.println(
                    "<p class='info'>" +
                    e.getMessage() +
                    "</p>"
                );

            } finally {

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

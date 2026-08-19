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

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String contextPath = request.getContextPath();

        // Check login session
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(contextPath + "/login.html");
            return;
        }

        int userId;

        try {
            userId = (Integer) session.getAttribute("userId");
        } catch (Exception e) {
            response.sendRedirect(contextPath + "/login.html");
            return;
        }

        String id = request.getParameter("id");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Edit Assignment</title>");

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
        out.println("padding: 25px;");
        out.println("text-align: center;");
        out.println("}");

        out.println(".header h1 {");
        out.println("margin: 0;");
        out.println("font-size: 30px;");
        out.println("}");

        out.println(".container {");
        out.println("width: 90%;");
        out.println("max-width: 600px;");
        out.println("margin: 40px auto;");
        out.println("}");

        out.println(".card {");
        out.println("background-color: white;");
        out.println("padding: 30px;");
        out.println("border-radius: 10px;");
        out.println("box-shadow: 0 2px 8px rgba(0,0,0,0.12);");
        out.println("}");

        out.println(".card h2 {");
        out.println("margin-top: 0;");
        out.println("color: #2c3e50;");
        out.println("text-align: center;");
        out.println("}");

        out.println(".form-group {");
        out.println("margin-bottom: 20px;");
        out.println("}");

        out.println("label {");
        out.println("display: block;");
        out.println("margin-bottom: 8px;");
        out.println("font-weight: bold;");
        out.println("}");

        out.println("input[type='text'],");
        out.println("input[type='date'],");
        out.println("select {");
        out.println("width: 100%;");
        out.println("padding: 11px;");
        out.println("border: 1px solid #ccc;");
        out.println("border-radius: 6px;");
        out.println("box-sizing: border-box;");
        out.println("font-size: 15px;");
        out.println("}");

        out.println("input[type='text']:focus,");
        out.println("input[type='date']:focus,");
        out.println("select:focus {");
        out.println("border-color: #3498db;");
        out.println("outline: none;");
        out.println("}");

        out.println("input[type='submit'] {");
        out.println("width: 100%;");
        out.println("padding: 12px;");
        out.println("background-color: #3498db;");
        out.println("color: white;");
        out.println("border: none;");
        out.println("border-radius: 6px;");
        out.println("font-size: 16px;");
        out.println("font-weight: bold;");
        out.println("cursor: pointer;");
        out.println("}");

        out.println("input[type='submit']:hover {");
        out.println("background-color: #217dbb;");
        out.println("}");

        out.println(".back-btn {");
        out.println("display: block;");
        out.println("margin-top: 20px;");
        out.println("padding: 12px;");
        out.println("background-color: #2c3e50;");
        out.println("color: white;");
        out.println("text-decoration: none;");
        out.println("text-align: center;");
        out.println("border-radius: 6px;");
        out.println("font-weight: bold;");
        out.println("}");

        out.println(".back-btn:hover {");
        out.println("background-color: #1f2d3a;");
        out.println("}");

        out.println(".error {");
        out.println("text-align: center;");
        out.println("color: #c0392b;");
        out.println("}");

        out.println("</style>");
        out.println("</head>");

        out.println("<body>");

        out.println("<div class='header'>");
        out.println("<h1>Student Assignment &amp; Deadline Tracker</h1>");
        out.println("</div>");

        out.println("<div class='container'>");
        out.println("<div class='card'>");

        out.println("<h2>Edit Assignment</h2>");

        if (id == null || id.trim().isEmpty()) {

            out.println("<h3 class='error'>Invalid Assignment ID</h3>");

        } else {

            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {

                int assignmentId = Integer.parseInt(id.trim());

                con = DBConnection.getConnection();

                if (con == null) {
                    throw new Exception("Database connection failed.");
                }

                /*
                 * IMPORTANT:
                 *
                 * Only retrieve the assignment if:
                 *
                 * 1. The assignment ID matches
                 * 2. The assignment belongs to the logged-in user
                 *
                 * This prevents User B from editing User A's assignment.
                 */
                String sql =
                        "SELECT id, assignment_name, subject, deadline, priority " +
                        "FROM assignments " +
                        "WHERE id = ? AND user_id = ?";

                ps = con.prepareStatement(sql);

                ps.setInt(1, assignmentId);
                ps.setInt(2, userId);

                rs = ps.executeQuery();

                if (rs.next()) {

                    out.println(
                        "<form action='" +
                        contextPath +
                        "/UpdateAssignment' method='post'>"
                    );

                    out.println(
                        "<input type='hidden' " +
                        "name='id' value='" +
                        rs.getInt("id") +
                        "'>"
                    );

                    out.println("<div class='form-group'>");

                    out.println(
                        "<label for='assignment_name'>" +
                        "Assignment Name:" +
                        "</label>"
                    );

                    out.println(
                        "<input type='text' " +
                        "id='assignment_name' " +
                        "name='assignment_name' value='" +
                        escapeHtml(rs.getString("assignment_name")) +
                        "' required>"
                    );

                    out.println("</div>");

                    out.println("<div class='form-group'>");

                    out.println(
                        "<label for='subject'>" +
                        "Subject:" +
                        "</label>"
                    );

                    out.println(
                        "<input type='text' " +
                        "id='subject' " +
                        "name='subject' value='" +
                        escapeHtml(rs.getString("subject")) +
                        "' required>"
                    );

                    out.println("</div>");

                    out.println("<div class='form-group'>");

                    out.println(
                        "<label for='deadline'>" +
                        "Deadline:" +
                        "</label>"
                    );

                    String deadline = "";

                    if (rs.getDate("deadline") != null) {
                        deadline = rs.getDate("deadline").toString();
                    }

                    out.println(
                        "<input type='date' " +
                        "id='deadline' " +
                        "name='deadline' value='" +
                        deadline +
                        "' required>"
                    );

                    out.println("</div>");

                    out.println("<div class='form-group'>");

                    out.println(
                        "<label for='priority'>" +
                        "Priority:" +
                        "</label>"
                    );

                    out.println(
                        "<select id='priority' " +
                        "name='priority' required>"
                    );

                    String priority = rs.getString("priority");

                    out.println(
                        "<option value='High' " +
                        ("High".equals(priority) ? "selected" : "") +
                        ">High</option>"
                    );

                    out.println(
                        "<option value='Medium' " +
                        ("Medium".equals(priority) ? "selected" : "") +
                        ">Medium</option>"
                    );

                    out.println(
                        "<option value='Low' " +
                        ("Low".equals(priority) ? "selected" : "") +
                        ">Low</option>"
                    );

                    out.println("</select>");

                    out.println("</div>");

                    out.println(
                        "<input type='submit' " +
                        "value='Update Assignment'>"
                    );

                    out.println("</form>");

                } else {

                    /*
                     * This also occurs when:
                     *
                     * - assignment does not exist, OR
                     * - assignment belongs to another user
                     *
                     * We deliberately don't reveal which case it is.
                     */
                    out.println(
                        "<h3 class='error'>" +
                        "Assignment Not Found</h3>"
                    );

                    out.println(
                        "<p style='text-align:center;'>" +
                        "This assignment does not belong to your account." +
                        "</p>"
                    );
                }

            } catch (NumberFormatException e) {

                out.println(
                    "<h3 class='error'>" +
                    "Invalid Assignment ID</h3>"
                );

            } catch (Exception e) {

                e.printStackTrace();

                out.println(
                    "<h3 class='error'>" +
                    "Error loading assignment.</h3>"
                );

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
        }

        out.println(
            "<a class='back-btn' " +
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
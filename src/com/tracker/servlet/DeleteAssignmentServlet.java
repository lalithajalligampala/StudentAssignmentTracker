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

public class DeleteAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String contextPath = request.getContextPath();

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
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");

        out.println("<title>Delete Assignment</title>");

        out.println("<style>");

        out.println("* {");
        out.println("    box-sizing: border-box;");
        out.println("}");

        out.println("body {");
        out.println("    margin: 0;");
        out.println("    min-height: 100vh;");
        out.println("    font-family: Arial, Helvetica, sans-serif;");
        out.println("    background: linear-gradient(135deg, #eef6ff, #f4fbfa);");
        out.println("    color: #1f2d3d;");
        out.println("}");

        out.println("body::before {");
        out.println("    content: '';");
        out.println("    position: fixed;");
        out.println("    width: 420px;");
        out.println("    height: 420px;");
        out.println("    border-radius: 50%;");
        out.println("    background: rgba(74, 144, 226, 0.06);");
        out.println("    top: -150px;");
        out.println("    left: -150px;");
        out.println("    z-index: -1;");
        out.println("}");

        out.println("body::after {");
        out.println("    content: '';");
        out.println("    position: fixed;");
        out.println("    width: 500px;");
        out.println("    height: 500px;");
        out.println("    border-radius: 50%;");
        out.println("    background: rgba(80, 190, 170, 0.06);");
        out.println("    bottom: -200px;");
        out.println("    right: -180px;");
        out.println("    z-index: -1;");
        out.println("}");

        out.println(".container {");
        out.println("    width: 950px;");
        out.println("    max-width: 90%;");
        out.println("    margin: 62px auto;");
        out.println("}");

        out.println(".card {");
        out.println("    background: rgba(255,255,255,0.96);");
        out.println("    border: 1px solid #d4e3f5;");
        out.println("    border-radius: 14px;");
        out.println("    padding: 52px 55px 55px 55px;");
        out.println("    box-shadow: 0 12px 35px rgba(31,60,90,0.12),");
        out.println("                0 2px 8px rgba(31,60,90,0.06);");
        out.println("}");

        out.println(".card h1 {");
        out.println("    margin: 0 0 34px 0;");
        out.println("    text-align: center;");
        out.println("    font-size: 38px;");
        out.println("    font-weight: 500;");
        out.println("    letter-spacing: 3px;");
        out.println("    color: #1d2d44;");
        out.println("}");

        out.println(".success-box {");
        out.println("    text-align: center;");
        out.println("    background: #f0faf6;");
        out.println("    border: 1px solid #bfe6d3;");
        out.println("    border-radius: 10px;");
        out.println("    padding: 28px 20px;");
        out.println("    margin-bottom: 30px;");
        out.println("}");

        out.println(".success-box h2 {");
        out.println("    margin: 0;");
        out.println("    font-size: 38px;");
        out.println("    font-weight: 500;");
        out.println("    letter-spacing: 3px;");
        out.println("    color: #1d2d44;");
        out.println("}");

        out.println(".success-box p {");
        out.println("    margin: 26px 0 0 0;");
        out.println("    color: #526579;");
        out.println("    font-size: 20px;");
        out.println("}");

        out.println(".details-box {");
        out.println("    background: #f4f8ff;");
        out.println("    border: 1px solid #d4e3f5;");
        out.println("    border-radius: 10px;");
        out.println("    padding: 28px;");
        out.println("    margin-bottom: 32px;");
        out.println("}");

        out.println(".details-box p {");
        out.println("    margin: 0 0 18px 0;");
        out.println("    font-size: 20px;");
        out.println("    color: #526579;");
        out.println("}");

        out.println(".details-box p:last-child {");
        out.println("    margin-bottom: 0;");
        out.println("}");

        out.println(".details-box strong {");
        out.println("    color: #1f2d3d;");
        out.println("}");

        out.println(".back-btn {");
        out.println("    display: block;");
        out.println("    width: 100%;");
        out.println("    height: 76px;");
        out.println("    border: 2px solid #3978d8;");
        out.println("    border-radius: 8px;");
        out.println("    background: white;");
        out.println("    color: #3978d8;");
        out.println("    text-decoration: none;");
        out.println("    text-align: center;");
        out.println("    line-height: 72px;");
        out.println("    font-size: 23px;");
        out.println("    font-weight: bold;");
        out.println("    letter-spacing: 4px;");
        out.println("    transition: all 0.2s ease;");
        out.println("}");

        out.println(".back-btn:hover {");
        out.println("    background: #3978d8;");
        out.println("    color: white;");
        out.println("}");

        out.println(".error-box {");
        out.println("    text-align: center;");
        out.println("    background: #fff5f5;");
        out.println("    border: 1px solid #f1c4c4;");
        out.println("    border-radius: 10px;");
        out.println("    padding: 28px;");
        out.println("    margin-bottom: 30px;");
        out.println("}");

        out.println(".error-box h2 {");
        out.println("    margin: 0 0 15px 0;");
        out.println("    color: #d64545;");
        out.println("}");

        out.println(".error-box p {");
        out.println("    margin: 0;");
        out.println("    color: #526579;");
        out.println("    font-size: 18px;");
        out.println("}");

        out.println("@media (max-width: 700px) {");

        out.println("    .container {");
        out.println("        max-width: 92%;");
        out.println("        margin: 30px auto;");
        out.println("    }");

        out.println("    .card {");
        out.println("        padding: 35px 25px 35px 25px;");
        out.println("    }");

        out.println("    .card h1 {");
        out.println("        font-size: 28px;");
        out.println("    }");

        out.println("    .success-box h2 {");
        out.println("        font-size: 28px;");
        out.println("    }");

        out.println("    .success-box p {");
        out.println("        font-size: 17px;");
        out.println("    }");

        out.println("    .details-box p {");
        out.println("        font-size: 17px;");
        out.println("    }");

        out.println("    .back-btn {");
        out.println("        height: 62px;");
        out.println("        line-height: 58px;");
        out.println("        font-size: 17px;");
        out.println("    }");

        out.println("}");

        out.println("</style>");

        out.println("</head>");

        out.println("<body>");

        out.println("<div class='container'>");
        out.println("<div class='card'>");

        out.println("<h1>DELETE ASSIGNMENT</h1>");

        Connection con = null;
        PreparedStatement selectPs = null;
        PreparedStatement deletePs = null;
        ResultSet rs = null;

        try {

            if (id == null || id.trim().isEmpty()) {
                throw new Exception("Assignment ID is missing.");
            }

            int assignmentId = Integer.parseInt(id.trim());

            con = DBConnection.getConnection();

            if (con == null) {
                throw new Exception("Database connection failed.");
            }

            /*
             * First get the assignment details.
             * We do this before deleting so that
             * we can display the deleted assignment.
             */

            String selectSql =
                    "SELECT assignment_name, subject, deadline, " +
                    "deadline_time, priority " +
                    "FROM assignments " +
                    "WHERE id = ? AND user_id = ?";

            selectPs = con.prepareStatement(selectSql);

            selectPs.setInt(1, assignmentId);
            selectPs.setInt(2, userId);

            rs = selectPs.executeQuery();

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

                if (deadlineTime != null &&
                    deadlineTime.length() >= 5) {

                    deadlineTime =
                            deadlineTime.substring(0, 5);
                }

                /*
                 * Delete the assignment
                 */

                String deleteSql =
                        "DELETE FROM assignments " +
                        "WHERE id = ? AND user_id = ?";

                deletePs = con.prepareStatement(deleteSql);

                deletePs.setInt(1, assignmentId);
                deletePs.setInt(2, userId);

                int rowsDeleted =
                        deletePs.executeUpdate();

                if (rowsDeleted > 0) {

                    out.println("<div class='success-box'>");

                    out.println(
                            "<h2>Assignment Deleted Successfully!</h2>"
                    );

                    out.println(
                            "<p>The assignment has been deleted successfully.</p>"
                    );

                    out.println("</div>");

                    out.println("<div class='details-box'>");

                    out.println(
                            "<p><strong>Assignment:</strong> " +
                            escapeHtml(assignmentName) +
                            "</p>"
                    );

                    out.println(
                            "<p><strong>Subject:</strong> " +
                            escapeHtml(subject) +
                            "</p>"
                    );

                    out.println(
                            "<p><strong>Deadline Date:</strong> " +
                            escapeHtml(deadline) +
                            "</p>"
                    );

                    out.println(
                            "<p><strong>Deadline Time:</strong> " +
                            escapeHtml(deadlineTime) +
                            "</p>"
                    );

                    out.println(
                            "<p><strong>Priority:</strong> " +
                            escapeHtml(priority) +
                            "</p>"
                    );

                    out.println("</div>");

                } else {

                    out.println("<div class='error-box'>");

                    out.println("<h2>Delete Failed</h2>");

                    out.println(
                            "<p>The assignment could not be deleted.</p>"
                    );

                    out.println("</div>");
                }

            } else {

                out.println("<div class='error-box'>");

                out.println("<h2>Assignment Not Found</h2>");

                out.println(
                        "<p>The assignment does not exist or does not belong to your account.</p>"
                );

                out.println("</div>");
            }

        } catch (NumberFormatException e) {

            out.println("<div class='error-box'>");

            out.println("<h2>Invalid Assignment ID</h2>");

            out.println(
                    "<p>The assignment ID must be a number.</p>"
            );

            out.println("</div>");

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<div class='error-box'>");

            out.println("<h2>Error Deleting Assignment</h2>");

            out.println(
                    "<p>" +
                    escapeHtml(e.getMessage()) +
                    "</p>"
            );

            out.println("</div>");

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (selectPs != null) {
                    selectPs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (deletePs != null) {
                    deletePs.close();
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

        out.println(
                "<a class='back-btn' href='" +
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
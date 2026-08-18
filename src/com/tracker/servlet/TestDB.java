package com.tracker.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestDB extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DBConnection.getConnection();

            if (con != null) {
                out.println("<h1>Database Connected Successfully!</h1>");
                con.close();
            } else {
                out.println("<h1>Database Connection Failed!</h1>");
            }

        } catch (Exception e) {
            out.println("<h1>Database Connection Failed!</h1>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}
package com.tracker.servlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        // Check DB_URL
        if (url == null || url.trim().isEmpty()) {
            throw new SQLException(
                "DB_URL environment variable is missing in Render."
            );
        }

        // Check DB_USER
        if (user == null || user.trim().isEmpty()) {
            throw new SQLException(
                "DB_USER environment variable is missing in Render."
            );
        }

        // Check DB_PASSWORD
        if (password == null || password.trim().isEmpty()) {
            throw new SQLException(
                "DB_PASSWORD environment variable is missing in Render."
            );
        }

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                "MySQL JDBC Driver is not available.",
                e
            );
        }

        System.out.println("=================================");
        System.out.println("Connecting to MySQL...");
        System.out.println("DB_USER: " + user);
        System.out.println("DB_URL: " + hidePassword(url));
        System.out.println("=================================");

        try {

            Connection connection =
                    DriverManager.getConnection(
                        url,
                        user,
                        password
                    );

            System.out.println(
                "Database connected successfully!"
            );

            return connection;

        } catch (SQLException e) {

            System.err.println(
                "ERROR: MySQL connection failed."
            );

            System.err.println(
                "Reason: " + e.getMessage()
            );

            throw e;
        }
    }

    private static String hidePassword(String url) {

        if (url == null) {
            return "null";
        }

        return url
                .replaceAll(
                    "(password=)[^&]*",
                    "$1********"
                );
    }
}
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
                "DB_URL environment variable is missing."
            );
        }

        // Check DB_USER
        if (user == null || user.trim().isEmpty()) {
            throw new SQLException(
                "DB_USER environment variable is missing."
            );
        }

        // Check DB_PASSWORD
        if (password == null || password.trim().isEmpty()) {
            throw new SQLException(
                "DB_PASSWORD environment variable is missing."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "MySQL JDBC Driver not found.",
                e
            );
        }

        return DriverManager.getConnection(
            url,
            user,
            password
        );
    }
}
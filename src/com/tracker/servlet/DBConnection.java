package com.tracker.servlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        try {

            // Check environment variables
            if (url == null || url.trim().isEmpty()) {
                throw new SQLException("DB_URL environment variable is missing.");
            }

            if (user == null || user.trim().isEmpty()) {
                throw new SQLException("DB_USER environment variable is missing.");
            }

            if (password == null || password.trim().isEmpty()) {
                throw new SQLException("DB_PASSWORD environment variable is missing.");
            }

            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("=================================");
            System.out.println("Connecting to MySQL database...");
            System.out.println("Database User: " + user);
            System.out.println("=================================");

            Connection connection = DriverManager.getConnection(
                    url,
                    user,
                    password
            );

            System.out.println("Database connected successfully!");

            return connection;

        } catch (ClassNotFoundException e) {

            System.err.println("ERROR: MySQL JDBC Driver not found.");
            e.printStackTrace();

        } catch (SQLException e) {

            System.err.println("ERROR: Database connection failed.");
            System.err.println("Reason: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {

            System.err.println("ERROR: Unexpected database error.");
            e.printStackTrace();
        }

        return null;
    }
}
package com.tracker.servlet;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {

            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Read database settings from Render Environment Variables
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            // Check environment variables
            if (url == null || url.trim().isEmpty()) {
                System.err.println("ERROR: DB_URL is not configured.");
                return null;
            }

            if (user == null || user.trim().isEmpty()) {
                System.err.println("ERROR: DB_USER is not configured.");
                return null;
            }

            if (password == null || password.trim().isEmpty()) {
                System.err.println("ERROR: DB_PASSWORD is not configured.");
                return null;
            }

            System.out.println("Connecting to database...");
            System.out.println("Database URL: " + url);
            System.out.println("Database User: " + user);

            Connection connection =
                    DriverManager.getConnection(
                            url,
                            user,
                            password
                    );

            System.out.println("Database connected successfully!");

            return connection;

        } catch (ClassNotFoundException e) {

            System.err.println(
                    "ERROR: MySQL JDBC Driver not found."
            );

            e.printStackTrace();

        } catch (Exception e) {

            System.err.println(
                    "ERROR: Database connection failed."
            );

            e.printStackTrace();
        }

        return null;
    }
}
package com.tracker.servlet;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        Connection connection = null;

        try {

            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            /*
             * Render will provide these environment variables.
             *
             * DB_URL example:
             * jdbc:mysql://your-host:3306/student_tracker?useSSL=false&serverTimezone=UTC
             *
             * DB_USER:
             * your database username
             *
             * DB_PASSWORD:
             * your database password
             */

            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            // Check environment variables
            if (url == null || url.trim().isEmpty()) {
                System.out.println("ERROR: DB_URL environment variable is missing.");
                return null;
            }

            if (user == null || user.trim().isEmpty()) {
                System.out.println("ERROR: DB_USER environment variable is missing.");
                return null;
            }

            if (password == null) {
                System.out.println("ERROR: DB_PASSWORD environment variable is missing.");
                return null;
            }

            // Connect to database
            connection = DriverManager.getConnection(
                    url,
                    user,
                    password
            );

            System.out.println("=================================");
            System.out.println("Database connected successfully!");
            System.out.println("=================================");

        } catch (ClassNotFoundException e) {

            System.out.println("ERROR: MySQL JDBC Driver not found.");
            e.printStackTrace();

        } catch (Exception e) {

            System.out.println("ERROR: Database connection failed.");
            e.printStackTrace();
        }

        return connection;
    }
}
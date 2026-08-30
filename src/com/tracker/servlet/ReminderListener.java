package com.tracker.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class ReminderListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {

        System.out.println("======================================");
        System.out.println("Reminder Scheduler Started");
        System.out.println("======================================");

        // Create scheduler with one background thread
        scheduler = Executors.newScheduledThreadPool(1);

        ReminderScheduler reminderTask =
                new ReminderScheduler();

        /*
         * Run once immediately when Tomcat starts.
         * This allows reminders to be checked immediately
         * without waiting for the first scheduled execution.
         */
        System.out.println("--------------------------------------");
        System.out.println("Running initial reminder check...");
        System.out.println("--------------------------------------");

        try {

            reminderTask.run();

        } catch (Exception e) {

            System.err.println(
                "Error during initial reminder check:"
            );

            e.printStackTrace();
        }


        /*
         * Run the reminder checker every 5 minutes.
         *
         * This is important because we now have:
         *
         * 1. Reminder 1 day before
         * 2. Reminder 1 hour before
         *
         * Checking every 5 minutes makes the 1-hour reminder
         * much more reliable than checking only once per hour.
         */
        scheduler.scheduleAtFixedRate(
                reminderTask,
                5,
                5,
                TimeUnit.MINUTES
        );


        System.out.println("--------------------------------------");
        System.out.println(
            "Reminder scheduler will check every 5 minutes."
        );
        System.out.println("--------------------------------------");
    }


    @Override
    public void contextDestroyed(ServletContextEvent event) {

        System.out.println("--------------------------------------");
        System.out.println("Stopping Reminder Scheduler...");
        System.out.println("--------------------------------------");

        if (scheduler != null) {

            scheduler.shutdown();

            try {

                /*
                 * Give currently running tasks some time
                 * to finish before shutting down completely.
                 */
                if (!scheduler.awaitTermination(
                        10,
                        TimeUnit.SECONDS)) {

                    scheduler.shutdownNow();

                    System.out.println(
                        "Reminder scheduler forced to stop."
                    );

                } else {

                    System.out.println(
                        "Reminder scheduler stopped successfully."
                    );
                }

            } catch (InterruptedException e) {

                scheduler.shutdownNow();

                Thread.currentThread().interrupt();

                System.out.println(
                    "Reminder scheduler interrupted and stopped."
                );
            }
        }
    }
}
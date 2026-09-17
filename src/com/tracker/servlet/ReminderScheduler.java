package com.tracker.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;


/**
 * Checks assignments and sends automatic reminder emails.
 *
 * Reminders:
 * 1. One day before the deadline
 * 2. One hour before the deadline
 *
 * Reminder status is stored in the assignments table:
 * - reminder_day_before_sent
 * - reminder_one_hour_sent
 */
public class ReminderScheduler implements Runnable {

    /*
     * Time zone used for assignment deadlines.
     *
     * Set APP_TIMEZONE in Render if necessary.
     *
     * Example:
     * APP_TIMEZONE=Asia/Kolkata
     *
     * If APP_TIMEZONE is not configured, Asia/Kolkata is used.
     */
    private static final String APP_TIMEZONE =
            System.getenv("APP_TIMEZONE") != null
                    ? System.getenv("APP_TIMEZONE")
                    : "Asia/Kolkata";


    // ============================================================
    // Scheduler entry point
    // ============================================================

    @Override
    public void run() {

        System.out.println(
                "================================================"
        );

        System.out.println(
                "Assignment Reminder Scheduler Started"
        );

        System.out.println(
                "Time Zone: " + APP_TIMEZONE
        );

        System.out.println(
                "================================================"
        );


        try {

            checkAndSendReminders();

        } catch (Exception e) {

            System.err.println(
                    "Error while running reminder scheduler:"
            );

            e.printStackTrace();
        }


        System.out.println(
                "================================================"
        );

        System.out.println(
                "Assignment Reminder Scheduler Finished"
        );

        System.out.println(
                "================================================"
        );
    }


    // ============================================================
    // Check assignments
    // ============================================================

    private void checkAndSendReminders() throws Exception {

        ZoneId zoneId;

        try {

            zoneId = ZoneId.of(APP_TIMEZONE);

        } catch (Exception e) {

            System.err.println(
                    "Invalid APP_TIMEZONE: "
                    + APP_TIMEZONE
            );

            System.err.println(
                    "Using Asia/Kolkata instead."
            );

            zoneId = ZoneId.of("Asia/Kolkata");
        }


        LocalDateTime now =
                LocalDateTime.now(zoneId);

        LocalDate today =
                now.toLocalDate();

        LocalDate tomorrow =
                today.plusDays(1);

        LocalDateTime oneHourLater =
                now.plusHours(1);


        System.out.println(
                "Current date/time: " + now
        );

        System.out.println(
                "Checking assignments for:"
        );

        System.out.println(
                "Today: " + today
        );

        System.out.println(
                "Tomorrow: " + tomorrow
        );

        System.out.println(
                "One hour later: " + oneHourLater
        );


        /*
         * Get assignments from today and tomorrow.
         *
         * We do the exact date/time comparison in Java.
         * This also correctly handles an hour crossing midnight.
         */
        String sql =
                "SELECT "
                + "a.id, "
                + "a.assignment_name, "
                + "a.subject, "
                + "a.deadline, "
                + "a.deadline_time, "
                + "a.reminder_day_before_sent, "
                + "a.reminder_one_hour_sent, "
                + "u.name AS student_name, "
                + "u.email AS student_email "
                + "FROM assignments a "
                + "INNER JOIN users u "
                + "ON a.user_id = u.id "
                + "WHERE a.deadline BETWEEN ? AND ? "
                + "ORDER BY a.deadline ASC, "
                + "a.deadline_time ASC";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(today)
            );

            statement.setDate(
                    2,
                    Date.valueOf(tomorrow)
            );


            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    processAssignment(
                            connection,
                            resultSet,
                            now,
                            today,
                            tomorrow,
                            oneHourLater
                    );
                }
            }
        }
    }


    // ============================================================
    // Process individual assignment
    // ============================================================

    private void processAssignment(
            Connection connection,
            ResultSet resultSet,
            LocalDateTime now,
            LocalDate today,
            LocalDate tomorrow,
            LocalDateTime oneHourLater
    ) throws Exception {


        int assignmentId =
                resultSet.getInt("id");


        String assignmentName =
                resultSet.getString("assignment_name");


        String subject =
                resultSet.getString("subject");


        Date deadlineDateSql =
                resultSet.getDate("deadline");


        Time deadlineTimeSql =
                resultSet.getTime("deadline_time");


        String studentName =
                resultSet.getString("student_name");


        String studentEmail =
                resultSet.getString("student_email");


        boolean dayReminderSent =
                resultSet.getBoolean(
                        "reminder_day_before_sent"
                );


        boolean hourReminderSent =
                resultSet.getBoolean(
                        "reminder_one_hour_sent"
                );


        // --------------------------------------------------------
        // Validate deadline
        // --------------------------------------------------------

        if (deadlineDateSql == null) {

            System.out.println(
                    "Skipping assignment ID "
                    + assignmentId
                    + " because deadline is missing."
            );

            return;
        }


        LocalDate deadlineDate =
                deadlineDateSql.toLocalDate();


        LocalTime deadlineTime;

        if (deadlineTimeSql != null) {

            deadlineTime =
                    deadlineTimeSql.toLocalTime();

        } else {

            /*
             * If no deadline_time exists, we cannot calculate
             * the one-hour reminder.
             *
             * The one-day reminder can still be sent.
             */
            deadlineTime = null;
        }


        // --------------------------------------------------------
        // Build deadline date/time
        // --------------------------------------------------------

        LocalDateTime deadlineDateTime = null;

        if (deadlineTime != null) {

            deadlineDateTime =
                    LocalDateTime.of(
                            deadlineDate,
                            deadlineTime
                    );
        }


        System.out.println(
                "Checking assignment ID "
                + assignmentId
                + " - "
                + assignmentName
        );


        // ========================================================
        // 1 DAY BEFORE REMINDER
        // ========================================================

        if (!dayReminderSent &&
            deadlineDate.equals(tomorrow)) {

            System.out.println(
                    "1-day reminder is due for assignment ID "
                    + assignmentId
            );


            String reminderType =
                    "1 day before deadline";


            String reminderMessage =
                    "Your assignment \""
                    + assignmentName
                    + "\" is due tomorrow. "
                    + "Please make sure you complete and "
                    + "submit it before the deadline.";


            String deadlineText =
                    deadlineDate.toString();


            String deadlineTimeText;

            if (deadlineTime != null) {

                deadlineTimeText =
                        deadlineTime.toString();

            } else {

                deadlineTimeText =
                        "Not specified";
            }


            try {

                EmailUtil.sendReminderEmail(
                        studentEmail,
                        assignmentName,
                        subject,
                        deadlineText,
                        deadlineTimeText,
                        reminderType,
                        reminderMessage
                );


                /*
                 * Only mark the reminder as sent AFTER
                 * EmailJS successfully sends the email.
                 */
                markDayReminderAsSent(
                        connection,
                        assignmentId
                );


                System.out.println(
                        "1-day reminder marked as sent "
                        + "for assignment ID "
                        + assignmentId
                );

            } catch (Exception e) {

                /*
                 * Do NOT update the database flag if
                 * EmailJS failed.
                 *
                 * The scheduler can try again later.
                 */
                System.err.println(
                        "Failed to send 1-day reminder "
                        + "for assignment ID "
                        + assignmentId
                );

                e.printStackTrace();
            }
        }


        // ========================================================
        // 1 HOUR BEFORE REMINDER
        // ========================================================

        if (!hourReminderSent &&
            deadlineDateTime != null) {


            /*
             * Check whether the deadline is:
             *
             *    after NOW
             *    and at or before NOW + 1 hour
             *
             * Example:
             *
             * Current time: 21:35
             * Deadline:     22:30
             *
             * 22:30 is within the next hour,
             * so the reminder is sent.
             */
            boolean withinNextHour =
                    deadlineDateTime.isAfter(now)
                    &&
                    !deadlineDateTime.isAfter(oneHourLater);


            if (withinNextHour) {

                System.out.println(
                        "1-hour reminder is due for "
                        + "assignment ID "
                        + assignmentId
                );


                String reminderType =
                        "1 hour before deadline";


                String reminderMessage =
                        "Your assignment \""
                        + assignmentName
                        + "\" is due in approximately "
                        + "1 hour. Please complete and "
                        + "submit it before the deadline.";


                String deadlineText =
                        deadlineDate.toString();


                String deadlineTimeText =
                        deadlineTime.toString();


                try {

                    EmailUtil.sendReminderEmail(
                            studentEmail,
                            assignmentName,
                            subject,
                            deadlineText,
                            deadlineTimeText,
                            reminderType,
                            reminderMessage
                    );


                    /*
                     * Only mark as sent after successful
                     * EmailJS delivery.
                     */
                    markHourReminderAsSent(
                            connection,
                            assignmentId
                    );


                    System.out.println(
                            "1-hour reminder marked as sent "
                            + "for assignment ID "
                            + assignmentId
                    );

                } catch (Exception e) {

                    /*
                     * Leave the flag as 0 if EmailJS fails.
                     */
                    System.err.println(
                            "Failed to send 1-hour reminder "
                            + "for assignment ID "
                            + assignmentId
                    );

                    e.printStackTrace();
                }
            }
        }
    }


    // ============================================================
    // Mark 1-day reminder as sent
    // ============================================================

    private void markDayReminderAsSent(
            Connection connection,
            int assignmentId
    ) throws Exception {

        String sql =
                "UPDATE assignments "
                + "SET reminder_day_before_sent = 1 "
                + "WHERE id = ?";


        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    assignmentId
            );

            statement.executeUpdate();
        }
    }


    // ============================================================
    // Mark 1-hour reminder as sent
    // ============================================================

    private void markHourReminderAsSent(
            Connection connection,
            int assignmentId
    ) throws Exception {

        String sql =
                "UPDATE assignments "
                + "SET reminder_one_hour_sent = 1 "
                + "WHERE id = ?";


        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    assignmentId
            );

            statement.executeUpdate();
        }
    }
}
package com.tracker.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReminderScheduler implements Runnable {

    @Override
    public void run() {

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalTime now = LocalTime.now();
        LocalTime oneHourLater = now.plusHours(1);

        System.out.println("------------------------------------------");
        System.out.println("Reminder Scheduler Running");
        System.out.println("Today: " + today);
        System.out.println("Current Time: " + now);
        System.out.println("Tomorrow: " + tomorrow);
        System.out.println("Checking assignment reminders...");
        System.out.println("------------------------------------------");

        /*
         * ============================================================
         * FIND ASSIGNMENTS THAT NEED A REMINDER
         * ============================================================
         *
         * 1. DAY-BEFORE REMINDER
         *    Deadline = tomorrow
         *    reminder_day_before_sent = 0
         *
         * 2. ONE-HOUR-BEFORE REMINDER
         *    Deadline = today
         *    Deadline time is between now and one hour from now
         *    reminder_one_hour_sent = 0
         */

        String sql =
            "SELECT a.id, " +
            "a.assignment_name, " +
            "a.subject, " +
            "a.deadline, " +
            "a.deadline_time, " +
            "a.user_id, " +
            "a.reminder_day_before_sent, " +
            "a.reminder_one_hour_sent, " +
            "u.name, " +
            "u.email " +

            "FROM assignments a " +

            "JOIN users u " +
            "ON a.user_id = u.id " +

            "WHERE " +

            "(" +
                "a.deadline = ? " +
                "AND a.reminder_day_before_sent = 0" +
            ") " +

            "OR " +

            "(" +
                "a.deadline = ? " +
                "AND a.deadline_time IS NOT NULL " +
                "AND a.deadline_time > ? " +
                "AND a.deadline_time <= ? " +
                "AND a.reminder_one_hour_sent = 0" +
            ")";


        /*
         * ============================================================
         * UPDATE DAY-BEFORE REMINDER
         * ============================================================
         */

        String updateDayBeforeSql =
            "UPDATE assignments " +
            "SET reminder_day_before_sent = 1 " +
            "WHERE id = ?";


        /*
         * ============================================================
         * UPDATE ONE-HOUR REMINDER
         * ============================================================
         */

        String updateOneHourSql =
            "UPDATE assignments " +
            "SET reminder_one_hour_sent = 1 " +
            "WHERE id = ?";


        try (
            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(sql);

            PreparedStatement updateDayBeforePs =
                con.prepareStatement(updateDayBeforeSql);

            PreparedStatement updateOneHourPs =
                con.prepareStatement(updateOneHourSql)
        ) {


            /*
             * ========================================================
             * SET SQL PARAMETERS
             * ========================================================
             */

            // Parameter 1:
            // Tomorrow's date for day-before reminder
            ps.setDate(
                1,
                Date.valueOf(tomorrow)
            );


            // Parameter 2:
            // Today's date for one-hour reminder
            ps.setDate(
                2,
                Date.valueOf(today)
            );


            // Parameter 3:
            // Current time
            ps.setTime(
                3,
                Time.valueOf(now)
            );


            // Parameter 4:
            // One hour from now
            ps.setTime(
                4,
                Time.valueOf(oneHourLater)
            );


            ResultSet rs =
                ps.executeQuery();


            /*
             * ========================================================
             * PROCESS REMINDERS
             * ========================================================
             */

            while (rs.next()) {

                int assignmentId =
                    rs.getInt("id");


                String name =
                    rs.getString("name");


                String email =
                    rs.getString("email");


                String assignmentName =
                    rs.getString("assignment_name");


                String subject =
                    rs.getString("subject");


                Date deadline =
                    rs.getDate("deadline");


                Time deadlineTime =
                    rs.getTime("deadline_time");


                int dayBeforeSent =
                    rs.getInt(
                        "reminder_day_before_sent"
                    );


                int oneHourSent =
                    rs.getInt(
                        "reminder_one_hour_sent"
                    );


                /*
                 * ====================================================
                 * DETERMINE WHICH REMINDER IS REQUIRED
                 * ====================================================
                 */

                boolean dayBeforeReminder =
                    deadline.toLocalDate()
                        .equals(tomorrow)
                    &&
                    dayBeforeSent == 0;


                boolean oneHourReminder = false;


                if (
                    deadline.toLocalDate().equals(today)
                    &&
                    deadlineTime != null
                    &&
                    oneHourSent == 0
                ) {

                    LocalTime deadlineTimeValue =
                        deadlineTime.toLocalTime();


                    /*
                     * Deadline is within the next hour
                     */

                    if (
                        deadlineTimeValue.isAfter(now)
                        &&
                        !deadlineTimeValue.isAfter(oneHourLater)
                    ) {

                        oneHourReminder = true;
                    }
                }


                /*
                 * ====================================================
                 * SEND DAY-BEFORE REMINDER
                 * ====================================================
                 */

                if (dayBeforeReminder) {

                    System.out.println(
                        "DAY-BEFORE REMINDER REQUIRED"
                    );


                    System.out.println(
                        "Assignment ID: "
                        + assignmentId
                    );


                    System.out.println(
                        "Student: "
                        + name
                    );


                    System.out.println(
                        "Email: "
                        + email
                    );


                    System.out.println(
                        "Assignment: "
                        + assignmentName
                    );


                    System.out.println(
                        "Deadline: "
                        + deadline
                    );


                    if (deadlineTime != null) {

                        System.out.println(
                            "Deadline Time: "
                            + deadlineTime
                        );
                    }


                    try {

                        String emailSubject =
                            "Assignment Reminder - Tomorrow - "
                            + assignmentName;


                        String emailBody =
                            "Hello "
                            + name
                            + ",\n\n"

                            + "This is a reminder that your assignment "
                            + "is due tomorrow.\n\n"

                            + "Assignment: "
                            + assignmentName
                            + "\n"

                            + "Subject: "
                            + subject
                            + "\n"

                            + "Deadline Date: "
                            + deadline
                            + "\n";


                        if (deadlineTime != null) {

                            emailBody +=
                                "Deadline Time: "
                                + deadlineTime
                                + "\n";
                        }


                        emailBody +=
                            "\nPlease make sure you complete and "
                            + "submit your assignment on time.\n\n"

                            + "Regards,\n"
                            + "Student Assignment Tracker";


                        /*
                         * SEND EMAIL
                         */

                        EmailUtil.sendEmail(
                            email,
                            emailSubject,
                            emailBody
                        );


                        System.out.println(
                            "DAY-BEFORE EMAIL SENT SUCCESSFULLY -> "
                            + email
                        );


                        /*
                         * MARK AS SENT ONLY AFTER SUCCESS
                         */

                        updateDayBeforePs.setInt(
                            1,
                            assignmentId
                        );


                        updateDayBeforePs.executeUpdate();


                        System.out.println(
                            "DAY-BEFORE REMINDER MARKED AS SENT -> "
                            + assignmentId
                        );


                    } catch (Exception emailError) {

                        System.err.println(
                            "DAY-BEFORE EMAIL FAILED -> "
                            + email
                        );


                        emailError.printStackTrace();


                        /*
                         * IMPORTANT:
                         * Do NOT update the flag.
                         *
                         * The scheduler can try again later.
                         */
                    }
                }


                /*
                 * ====================================================
                 * SEND ONE-HOUR-BEFORE REMINDER
                 * ====================================================
                 */

                if (oneHourReminder) {

                    System.out.println(
                        "ONE-HOUR-BEFORE REMINDER REQUIRED"
                    );


                    System.out.println(
                        "Assignment ID: "
                        + assignmentId
                    );


                    System.out.println(
                        "Student: "
                        + name
                    );


                    System.out.println(
                        "Email: "
                        + email
                    );


                    System.out.println(
                        "Assignment: "
                        + assignmentName
                    );


                    System.out.println(
                        "Deadline: "
                        + deadline
                    );


                    System.out.println(
                        "Deadline Time: "
                        + deadlineTime
                    );


                    try {

                        String emailSubject =
                            "Assignment Reminder - 1 Hour Left - "
                            + assignmentName;


                        String emailBody =
                            "Hello "
                            + name
                            + ",\n\n"

                            + "This is a reminder that your assignment "
                            + "is due in approximately 1 hour.\n\n"

                            + "Assignment: "
                            + assignmentName
                            + "\n"

                            + "Subject: "
                            + subject
                            + "\n"

                            + "Deadline Date: "
                            + deadline
                            + "\n"

                            + "Deadline Time: "
                            + deadlineTime
                            + "\n\n"

                            + "Please complete and submit your "
                            + "assignment before the deadline.\n\n"

                            + "Regards,\n"
                            + "Student Assignment Tracker";


                        /*
                         * SEND EMAIL
                         */

                        EmailUtil.sendEmail(
                            email,
                            emailSubject,
                            emailBody
                        );


                        System.out.println(
                            "ONE-HOUR EMAIL SENT SUCCESSFULLY -> "
                            + email
                        );


                        /*
                         * MARK AS SENT ONLY AFTER SUCCESS
                         */

                        updateOneHourPs.setInt(
                            1,
                            assignmentId
                        );


                        updateOneHourPs.executeUpdate();


                        System.out.println(
                            "ONE-HOUR REMINDER MARKED AS SENT -> "
                            + assignmentId
                        );


                    } catch (Exception emailError) {

                        System.err.println(
                            "ONE-HOUR EMAIL FAILED -> "
                            + email
                        );


                        emailError.printStackTrace();


                        /*
                         * IMPORTANT:
                         * Do NOT mark it as sent.
                         *
                         * Scheduler can retry.
                         */
                    }
                }
            }


            rs.close();


            System.out.println(
                "Reminder check completed."
            );


        } catch (Exception e) {

            System.err.println(
                "ERROR WHILE CHECKING REMINDERS"
            );


            e.printStackTrace();
        }


        System.out.println("------------------------------------------");
    }
}
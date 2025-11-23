package com.example.buggyapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class DateUtils {

    // DEFECT: SimpleDateFormat is not thread-safe when used as static field
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // DEFECT: Another thread-unsafe SimpleDateFormat
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // DEFECT: Using deprecated Date API instead of java.time
    public static Date getCurrentDate() {
        // DEFECT: Using deprecated Date constructor
        return new Date();
    }

    // DEFECT: Thread-safety issue with static SimpleDateFormat
    public static String formatDate(Date date) {
        // DEFECT: No null check
        // DEFECT: Using static SimpleDateFormat (thread-safety issue)
        return DATE_FORMAT.format(date);
    }

    // DEFECT: ParseException handling
    public static Date parseDate(String dateStr) {
        try {
            // DEFECT: Using thread-unsafe static SimpleDateFormat
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            // DEFECT: Swallowing exception and returning null
            return null;
        }
    }

    // DEFECT: Using deprecated Calendar API
    public static Date addDays(Date date, int days) {
        // DEFECT: No null check
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);

        // DEFECT: Returning mutable Date object
        return calendar.getTime();
    }

    // DEFECT: Incorrect comparison of Date objects
    public static boolean isSameDay(Date date1, Date date2) {
        // DEFECT: Using == instead of proper date comparison
        // DEFECT: No null checks
        return date1.toString().equals(date2.toString());
    }

    // DEFECT: Hard-coded timezone
    public static String formatWithTimezone(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // DEFECT: Hard-coded timezone string
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("America/New_York"));
        return sdf.format(date);
    }

    // DEFECT: Memory leak - storing dates in static collection
    private static java.util.List<Date> dateCache = new java.util.ArrayList<>();

    public static void cacheDate(Date date) {
        // DEFECT: Adding to static collection without removal strategy
        dateCache.add(date);
    }

    // DEFECT: Exposing mutable static collection
    public static java.util.List<Date> getCachedDates() {
        // DEFECT: Returning mutable reference to static collection
        return dateCache;
    }

    // DEFECT: Inefficient date validation
    public static boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            // DEFECT: Creating new SimpleDateFormat on every call
            Date date = sdf.parse(dateStr);

            // DEFECT: Incomplete validation
            return date != null;
        } catch (Exception e) {
            // DEFECT: Catching generic Exception
            return false;
        }
    }

    // DEFECT: Magic numbers in date calculation
    public static long getDaysBetween(Date start, Date end) {
        // DEFECT: No null checks
        long diff = end.getTime() - start.getTime();

        // DEFECT: Magic numbers (milliseconds conversion)
        return diff / (1000 * 60 * 60 * 24);
    }

    // DEFECT: Deprecated Date methods
    @SuppressWarnings("deprecation")
    public static Date createDate(int year, int month, int day) {
        // DEFECT: Using deprecated Date constructor
        return new Date(year - 1900, month - 1, day);
    }

    // DEFECT: Inconsistent date formatting
    public static String formatDateTime(Date date) {
        // DEFECT: Different format than formatDate() - inconsistent API
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }

    // DEFECT: Boolean parameter (code smell)
    public static String formatDate(Date date, boolean includeTime) {
        if (includeTime) {
            return dateTimeFormat.format(date);
        } else {
            return DATE_FORMAT.format(date);
        }
    }
}

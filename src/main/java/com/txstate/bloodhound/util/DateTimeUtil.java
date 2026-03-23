package com.txstate.bloodhound.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Shared date/time helpers for record formatting and filtering.
 */
public final class DateTimeUtil {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private DateTimeUtil() {
    }

    /**
     * Formats epoch milliseconds as an ISO-8601 instant string.
     *
     * @param epochMillis epoch milliseconds to format
     * @return ISO-8601 instant string
     */
    public static String formatEpochMillis(long epochMillis) {
        return ISO_FORMATTER.format(Instant.ofEpochMilli(epochMillis));
    }

    /**
     * Converts epoch milliseconds to a local date using the supplied timezone.
     *
     * @param epochMillis epoch milliseconds to convert
     * @param zoneId target timezone
     * @return local date in the given timezone
     */
    public static LocalDate toLocalDate(long epochMillis, ZoneId zoneId) {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate();
    }
}

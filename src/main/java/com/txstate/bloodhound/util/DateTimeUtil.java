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

    public static String formatEpochMillis(long epochMillis) {
        return ISO_FORMATTER.format(Instant.ofEpochMilli(epochMillis));
    }

    public static LocalDate toLocalDate(long epochMillis, ZoneId zoneId) {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate();
    }
}

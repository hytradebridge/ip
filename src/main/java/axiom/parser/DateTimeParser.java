package axiom.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import axiom.AxiomException;

/**
 * Represents a utility for parsing user date/time input and formatting stored dates for display.
 * This class is not instantiable.
 */
public class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATETIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm").withResolverStyle(ResolverStyle.SMART),
        DateTimeFormatter.ofPattern("d/M/yyyy H:mm").withResolverStyle(ResolverStyle.SMART),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm").withResolverStyle(ResolverStyle.SMART),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").withResolverStyle(ResolverStyle.SMART),
    };

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("d/M/yyyy").withResolverStyle(ResolverStyle.SMART),
        DateTimeFormatter.ISO_LOCAL_DATE,
    };

    private DateTimeParser() {
    }

    /**
     * Parses a date/time string from user input into a {@link LocalDateTime}.
     *
     * @param input Date/time text supplied by the user.
     * @return The parsed date and time.
     * @throws AxiomException If the input does not match any supported format.
     */
    public static LocalDateTime parse(String input) throws AxiomException {
        String trimmed = input.trim();

        LocalDateTime dateTime = tryParseDateTime(trimmed);
        if (dateTime != null) {
            return dateTime;
        }

        LocalDateTime dateOnly = tryParseDate(trimmed);
        if (dateOnly != null) {
            return dateOnly;
        }

        throw new AxiomException("Invalid date/time format: '" + trimmed
                + "'. Use yyyy-MM-dd or d/M/yyyy HHmm.");
    }

    /**
     * Parses a date/time string stored in the data file.
     *
     * @param input ISO-8601 date/time text from the data file.
     * @return The parsed date and time.
     * @throws AxiomException If the stored value is not valid ISO-8601.
     */
    public static LocalDateTime parseStored(String input) throws AxiomException {
        String trimmed = input.trim();
        try {
            return LocalDateTime.parse(trimmed);
        } catch (DateTimeParseException e) {
            throw new AxiomException("Invalid stored date/time: '" + trimmed + "'.");
        }
    }

    /**
     * Formats a date/time for display to the user.
     *
     * @param dateTime Date and time to format.
     * @return A human-readable date or date-time string.
     */
    public static String format(LocalDateTime dateTime) {
        assert dateTime != null : "Cannot format a null date-time";
        if (isAtStartOfDay(dateTime)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATETIME);
    }

    /**
     * Formats a date/time for storage in the data file.
     *
     * @param dateTime Date and time to format.
     * @return An ISO-8601 string suitable for persistence.
     */
    public static String formatStored(LocalDateTime dateTime) {
        assert dateTime != null : "Cannot store a null date-time";
        return dateTime.toString();
    }

    /**
     * Returns a date-time parsed with a date-and-time formatter, or {@code null} if none match.
     *
     * @param input Trimmed date/time text.
     * @return The parsed date and time, or {@code null} if the text is date-only or invalid.
     */
    private static LocalDateTime tryParseDateTime(String input) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                // Try the next supported format.
            }
        }
        return null;
    }

    /**
     * Returns midnight on a parsed date, or {@code null} if no date formatter matches.
     *
     * @param input Trimmed date text.
     * @return The date at start of day, or {@code null} if the text is not a supported date.
     */
    private static LocalDateTime tryParseDate(String input) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(input, formatter).atStartOfDay();
            } catch (DateTimeParseException e) {
                // Try the next supported format.
            }
        }
        return null;
    }

    /**
     * Returns whether {@code dateTime} has no time-of-day component.
     *
     * @param dateTime Date and time to inspect.
     * @return {@code true} if the time is midnight.
     */
    private static boolean isAtStartOfDay(LocalDateTime dateTime) {
        return dateTime.getHour() == 0 && dateTime.getMinute() == 0;
    }
}

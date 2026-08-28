package axiom.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import axiom.AxiomException;

/**
 * Parses user date/time input and formats stored dates for display.
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
     * Parses a date/time string into a {@link LocalDateTime}.
     */
    public static LocalDateTime parse(String input) throws AxiomException {
        String trimmed = input.trim();

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                // Try the next supported format.
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter).atStartOfDay();
            } catch (DateTimeParseException e) {
                // Try the next supported format.
            }
        }

        throw new AxiomException("Invalid date/time format: '" + trimmed
                + "'. Use yyyy-MM-dd or d/M/yyyy HHmm.");
    }

    /**
     * Parses a date/time string stored in the data file.
     */
    public static LocalDateTime parseStored(String input) throws AxiomException {
        try {
            return LocalDateTime.parse(input.trim());
        } catch (DateTimeParseException e) {
            throw new AxiomException("Invalid stored date/time: '" + input.trim() + "'.");
        }
    }

    /**
     * Formats a date/time for display to the user.
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATETIME);
    }

    /**
     * Formats a date/time for storage in the data file.
     */
    public static String formatStored(LocalDateTime dateTime) {
        return dateTime.toString();
    }
}

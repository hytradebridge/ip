package axiom.task;

import java.time.LocalDateTime;

import axiom.parser.DateTimeParser;

/**
 * Represents an event task with a start and end date or time.
 */
public class Event extends Task {

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates a new event task.
     *
     * @param description Description of the event.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start date and time.
     *
     * @return The {@code /from} date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date and time.
     *
     * @return The {@code /to} date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns a string representation with the {@code [E]} type prefix and formatted times.
     *
     * @return Formatted event string.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}

package axiom.task;

import java.time.LocalDateTime;

import axiom.parser.DateTimeParser;

/**
 * Represents a deadline task that must be done by a specific date or time.
 */
public class Deadline extends Task {

    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}

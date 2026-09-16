package axiom.parser;

import axiom.AxiomException;
import axiom.command.Command;
import axiom.task.Deadline;
import axiom.task.Event;
import axiom.task.Task;
import axiom.task.Todo;

/**
 * Represents a utility that makes sense of user commands and converts input into tasks or task indices.
 */
public class Parser {
    private static final String BY_DELIMITER = " /by ";
    private static final String FROM_DELIMITER = " /from ";
    private static final String TO_DELIMITER = " /to ";
    private static final String DEADLINE_USAGE = "Usage: deadline <description> /by <time>";
    private static final String EVENT_USAGE = "Usage: event <description> /from <start> /to <end>";

    /**
     * Returns the command identified in a line of user input.
     *
     * @param input Full command line from the user.
     * @return The matching {@link Command}.
     */
    public Command getCommand(String input) {
        return Command.fromInput(input);
    }

    /**
     * Returns the validated one-based task number extracted from a command.
     *
     * @param command Command being executed (e.g. {@link Command#MARK}).
     * @param input Full command line from the user.
     * @param taskCount Number of tasks currently in the list.
     * @return The validated one-based task number.
     * @throws AxiomException If the argument is missing, non-numeric, or out of range.
     */
    public int parseTaskNumber(Command command, String input, int taskCount) throws AxiomException {
        String argument = requireNonEmpty(command.getArgument(input),
                "Please specify which task to " + command.getKeyword()
                + ". Usage: " + command.getKeyword() + " <task number>");
        try {
            int taskNumber = Integer.parseInt(argument);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new AxiomException("That task number isn't in your list. You currently have "
                        + taskCount + " task(s).");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new AxiomException("'" + argument + "' is not a valid task number. Usage: "
                    + command.getKeyword() + " <task number>");
        }
    }

    /**
     * Parses a {@code todo} command into a {@link Todo} task.
     *
     * @param input Full {@code todo} command line.
     * @return The parsed todo task.
     * @throws AxiomException If the description is missing.
     */
    public Task parseTodo(String input) throws AxiomException {
        String description = requireNonEmpty(Command.TODO.getArgument(input),
                "A todo needs a description. Usage: todo <description>");
        return new Todo(description);
    }

    /**
     * Parses a {@code deadline} command into a {@link Deadline} task.
     *
     * @param input Full {@code deadline} command line.
     * @return The parsed deadline task.
     * @throws AxiomException If the description or {@code /by} time is missing or invalid.
     */
    public Task parseDeadline(String input) throws AxiomException {
        String remainder = requireNonEmpty(Command.DEADLINE.getArgument(input),
                "A deadline needs a description and a /by time. " + DEADLINE_USAGE);
        int byIndex = remainder.indexOf(BY_DELIMITER);
        if (byIndex == -1) {
            throw new AxiomException("A deadline must include /by. " + DEADLINE_USAGE);
        }
        String description = requireNonEmpty(remainder.substring(0, byIndex).trim(),
                "A deadline needs a description. " + DEADLINE_USAGE);
        String by = requireNonEmpty(remainder.substring(byIndex + BY_DELIMITER.length()).trim(),
                "A deadline needs a /by time. " + DEADLINE_USAGE);
        return new Deadline(description, DateTimeParser.parse(by));
    }

    /**
     * Parses an {@code event} command into an {@link Event} task.
     *
     * @param input Full {@code event} command line.
     * @return The parsed event task.
     * @throws AxiomException If the description, {@code /from}, or {@code /to} time is missing or invalid.
     */
    public Task parseEvent(String input) throws AxiomException {
        String remainder = requireNonEmpty(Command.EVENT.getArgument(input),
                "An event needs a description, /from, and /to times. " + EVENT_USAGE);
        int fromIndex = remainder.indexOf(FROM_DELIMITER);
        int toIndex = remainder.indexOf(TO_DELIMITER);
        boolean hasFromAndTo = fromIndex != -1 && toIndex != -1;
        boolean isFromBeforeTo = hasFromAndTo && fromIndex < toIndex;
        if (!isFromBeforeTo) {
            throw new AxiomException("An event must include /from and /to. " + EVENT_USAGE);
        }
        String description = requireNonEmpty(remainder.substring(0, fromIndex).trim(),
                "An event needs a description. " + EVENT_USAGE);
        String from = requireNonEmpty(remainder.substring(fromIndex + FROM_DELIMITER.length(), toIndex).trim(),
                "An event needs a /from time. " + EVENT_USAGE);
        String to = requireNonEmpty(remainder.substring(toIndex + TO_DELIMITER.length()).trim(),
                "An event needs a /to time. " + EVENT_USAGE);
        return new Event(description, DateTimeParser.parse(from), DateTimeParser.parse(to));
    }

    /**
     * Returns the keyword to search for in a {@code find} command.
     *
     * @param input Full {@code find} command line.
     * @return The keyword to search for.
     * @throws AxiomException If the keyword is missing.
     */
    public String parseFindKeyword(String input) throws AxiomException {
        return requireNonEmpty(Command.FIND.getArgument(input),
                "A find needs a keyword. Usage: find <keyword>");
    }

    /**
     * Returns {@code value} if it contains text.
     *
     * @param value User-supplied text to check.
     * @param message Error to throw when the value is empty.
     * @return The original value.
     * @throws AxiomException If {@code value} is empty.
     */
    private String requireNonEmpty(String value, String message) throws AxiomException {
        if (value.isEmpty()) {
            throw new AxiomException(message);
        }
        return value;
    }
}

package axiom.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import axiom.AxiomException;
import axiom.parser.DateTimeParser;
import axiom.task.Deadline;
import axiom.task.Event;
import axiom.task.Task;
import axiom.task.TaskList;
import axiom.task.Todo;

/**
 * Represents a component that loads and saves tasks to a file on disk using OS-independent relative paths.
 */
public class Storage {
    private static final String FIELD_DELIMITER = " | ";
    private static final String FIELD_SPLIT_REGEX = " \\| ";
    private static final String EVENT_TIME_DELIMITER = " to ";
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";
    private static final int MIN_FIELD_COUNT = 3;
    private static final int TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DETAILS_INDEX = 3;

    private final Path filePath;

    /**
     * Creates a storage instance for the given file path.
     *
     * @param filePath Path to the task data file (e.g. {@code data/axiom.txt}).
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the data file.
     *
     * @return The loaded tasks, or an empty list when the file does not exist yet.
     * @throws AxiomException If the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws AxiomException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        if (Files.isDirectory(filePath)) {
            throw new AxiomException("Cannot load tasks because " + filePath + " is a directory.");
        }
        if (!Files.isReadable(filePath)) {
            throw new AxiomException("Cannot read tasks from " + filePath + ".");
        }

        try {
            ArrayList<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) {
                    continue;
                }
                tasks.add(parseTask(line, i + 1));
            }
            return tasks;
        } catch (IOException e) {
            throw new AxiomException("Could not read tasks from " + filePath + ".");
        }
    }

    /**
     * Saves all tasks to the data file, creating parent folders if needed.
     *
     * @param tasks Task list to persist.
     * @throws AxiomException If the file path is invalid or cannot be written.
     */
    public void save(TaskList tasks) throws AxiomException {
        ensureWritablePath();
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks.getTasks()) {
                lines.add(formatTask(task));
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new AxiomException("Could not save tasks to " + filePath + ".");
        }
    }

    /**
     * Verifies that the target path can be written to.
     *
     * @throws AxiomException If the parent path or file path is not writable.
     */
    private void ensureWritablePath() throws AxiomException {
        Path parent = filePath.getParent();
        if (parent != null && Files.exists(parent) && !Files.isDirectory(parent)) {
            throw new AxiomException("Cannot save tasks because " + parent + " is not a folder.");
        }
        if (Files.exists(filePath) && Files.isDirectory(filePath)) {
            throw new AxiomException("Cannot save tasks because " + filePath + " is a directory.");
        }
    }

    /**
     * Parses a single line from the data file into a {@link Task}.
     *
     * @param line Text of the line to parse.
     * @param lineNumber One-based line number, used in error messages.
     * @return The parsed task.
     * @throws AxiomException If the line format is invalid.
     */
    private Task parseTask(String line, int lineNumber) throws AxiomException {
        String[] parts = line.split(FIELD_SPLIT_REGEX, -1);
        if (parts.length < MIN_FIELD_COUNT) {
            throw formatError(lineNumber, "expected format TYPE | STATUS | DESCRIPTION.");
        }

        String type = parts[TYPE_INDEX].trim();
        String status = parts[STATUS_INDEX].trim();
        String description = parts[DESCRIPTION_INDEX].trim();

        if (!status.equals(STATUS_NOT_DONE) && !status.equals(STATUS_DONE)) {
            throw formatError(lineNumber, "status must be 0 or 1.");
        }
        if (description.isEmpty()) {
            throw formatError(lineNumber, "description cannot be empty.");
        }

        boolean isDone = status.equals(STATUS_DONE);
        Task task = createTask(type, description, parts, lineNumber);

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns a task of the appropriate type from parsed file fields.
     *
     * @param type Task type code ({@code T}, {@code D}, or {@code E}).
     * @param description Task description.
     * @param parts All pipe-separated fields from the file line.
     * @param lineNumber One-based line number, used in error messages.
     * @return The created task (not yet marked done).
     * @throws AxiomException If the type is unknown or required fields are missing.
     */
    private Task createTask(String type, String description, String[] parts, int lineNumber)
            throws AxiomException {
        switch (type) {
        case TYPE_TODO:
            return new Todo(description);
        case TYPE_DEADLINE:
            return createDeadline(description, parts, lineNumber);
        case TYPE_EVENT:
            return createEvent(description, parts, lineNumber);
        default:
            throw formatError(lineNumber, "unknown task type '" + type + "'.");
        }
    }

    /**
     * Returns a deadline from parsed file fields.
     *
     * @param description Task description.
     * @param parts All pipe-separated fields from the file line.
     * @param lineNumber One-based line number, used in error messages.
     * @return The created deadline (not yet marked done).
     * @throws AxiomException If the {@code /by} value is missing.
     */
    private Task createDeadline(String description, String[] parts, int lineNumber) throws AxiomException {
        if (parts.length <= DETAILS_INDEX || parts[DETAILS_INDEX].trim().isEmpty()) {
            throw formatError(lineNumber, "deadline is missing a /by value.");
        }
        return new Deadline(description, DateTimeParser.parseStored(parts[DETAILS_INDEX].trim()));
    }

    /**
     * Returns an event from parsed file fields.
     *
     * @param description Task description.
     * @param parts All pipe-separated fields from the file line.
     * @param lineNumber One-based line number, used in error messages.
     * @return The created event (not yet marked done).
     * @throws AxiomException If start or end times are missing.
     */
    private Task createEvent(String description, String[] parts, int lineNumber) throws AxiomException {
        if (parts.length <= DETAILS_INDEX) {
            throw formatError(lineNumber, "event is missing date/time information.");
        }
        String fromTo = parts[DETAILS_INDEX].trim();
        int timeSeparatorIndex = fromTo.indexOf(EVENT_TIME_DELIMITER);
        if (timeSeparatorIndex == -1) {
            throw formatError(lineNumber, "event must contain ' to ' between start and end times.");
        }
        String from = fromTo.substring(0, timeSeparatorIndex).trim();
        String to = fromTo.substring(timeSeparatorIndex + EVENT_TIME_DELIMITER.length()).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw formatError(lineNumber, "event start and end times cannot be empty.");
        }
        return new Event(description, DateTimeParser.parseStored(from), DateTimeParser.parseStored(to));
    }

    /**
     * Returns an {@link AxiomException} for a corrupt data file line.
     *
     * @param lineNumber One-based line number of the error.
     * @param details Short description of what went wrong.
     * @return A formatted exception ready to throw.
     */
    private AxiomException formatError(int lineNumber, String details) {
        return new AxiomException("Problem in " + filePath + " at line " + lineNumber + ": " + details);
    }

    /**
     * Returns the pipe-delimited file format representation of a task.
     *
     * @param task Task to serialize.
     * @return A single line suitable for writing to the data file.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? STATUS_DONE : STATUS_NOT_DONE;
        String description = task.getDescription();
        switch (task) {
        case Todo _:
            return joinFields(TYPE_TODO, status, description);
        case Deadline deadline:
            return joinFields(TYPE_DEADLINE, status, description,
                    DateTimeParser.formatStored(deadline.getBy()));
        case Event event:
            return joinFields(TYPE_EVENT, status, description,
                    DateTimeParser.formatStored(event.getFrom()) + EVENT_TIME_DELIMITER
                            + DateTimeParser.formatStored(event.getTo()));
        default:
            throw new IllegalArgumentException("Unknown task type: " + task.getClass().getName());
        }
    }

    /**
     * Returns a pipe-delimited line from the given fields.
     *
     * @param fields Values to join in file order.
     * @return A single storage line.
     */
    private String joinFields(String... fields) {
        return String.join(FIELD_DELIMITER, fields);
    }
}

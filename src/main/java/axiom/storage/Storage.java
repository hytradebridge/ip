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
 * Loads and saves tasks to a file on disk using OS-independent relative paths.
 */
public class Storage {
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
     * Writes all tasks to the data file, creating parent folders if needed.
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
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            throw formatError(lineNumber, "expected format TYPE | STATUS | DESCRIPTION.");
        }

        String type = parts[0].trim();
        String status = parts[1].trim();
        String description = parts[2].trim();

        if (!status.equals("0") && !status.equals("1")) {
            throw formatError(lineNumber, "status must be 0 or 1.");
        }
        if (description.isEmpty()) {
            throw formatError(lineNumber, "description cannot be empty.");
        }

        boolean isDone = status.equals("1");
        Task task = createTask(type, description, parts, lineNumber);

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates a task of the appropriate type from parsed file fields.
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
        case "T":
            return new Todo(description);
        case "D":
            if (parts.length < 4 || parts[3].trim().isEmpty()) {
                throw formatError(lineNumber, "deadline is missing a /by value.");
            }
            return new Deadline(description, DateTimeParser.parseStored(parts[3].trim()));
        case "E":
            if (parts.length < 4) {
                throw formatError(lineNumber, "event is missing date/time information.");
            }
            String fromTo = parts[3].trim();
            int toIndex = fromTo.indexOf(" to ");
            if (toIndex == -1) {
                throw formatError(lineNumber, "event must contain ' to ' between start and end times.");
            }
            String from = fromTo.substring(0, toIndex).trim();
            String to = fromTo.substring(toIndex + 4).trim();
            if (from.isEmpty() || to.isEmpty()) {
                throw formatError(lineNumber, "event start and end times cannot be empty.");
            }
            return new Event(description, DateTimeParser.parseStored(from), DateTimeParser.parseStored(to));
        default:
            throw formatError(lineNumber, "unknown task type '" + type + "'.");
        }
    }

    /**
     * Builds an {@link AxiomException} for a corrupt data file line.
     *
     * @param lineNumber One-based line number of the error.
     * @param details Short description of what went wrong.
     * @return A formatted exception ready to throw.
     */
    private AxiomException formatError(int lineNumber, String details) {
        return new AxiomException("Problem in data/axiom.txt at line " + lineNumber + ": " + details);
    }

    /**
     * Serializes a task into the pipe-delimited file format.
     *
     * @param task Task to serialize.
     * @return A single line suitable for writing to the data file.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "T | " + status + " | " + task.getDescription();
        }
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + task.getDescription() + " | "
                    + DateTimeParser.formatStored(deadline.getBy());
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + task.getDescription() + " | "
                    + DateTimeParser.formatStored(event.getFrom()) + " to "
                    + DateTimeParser.formatStored(event.getTo());
        }
        throw new IllegalArgumentException("Unknown task type: " + task.getClass().getName());
    }
}

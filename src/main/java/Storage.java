import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads tasks from a file on disk using OS-independent relative paths.
 */
public class Storage {
    private static final Path DATA_DIR = Path.of("data");
    private static final Path FILE_PATH = DATA_DIR.resolve("axiom.txt");

    /**
     * Loads tasks from the data file. Returns an empty list when the file or folder
     * does not exist yet.
     */
    public static ArrayList<Task> load() throws AxiomException {
        if (!Files.exists(FILE_PATH)) {
            return new ArrayList<>();
        }
        if (Files.isDirectory(FILE_PATH)) {
            throw new AxiomException("Cannot load tasks because data/axiom.txt is a directory.");
        }
        if (!Files.isReadable(FILE_PATH)) {
            throw new AxiomException("Cannot read tasks from data/axiom.txt.");
        }

        try {
            ArrayList<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(FILE_PATH);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) {
                    continue;
                }
                tasks.add(parseTask(line, i + 1));
            }
            return tasks;
        } catch (IOException e) {
            throw new AxiomException("Could not read tasks from data/axiom.txt.");
        }
    }

    /**
     * Writes all tasks to the data file, creating the data folder if needed.
     */
    public static void save(ArrayList<Task> tasks) throws AxiomException {
        ensureWritablePath();
        try {
            Files.createDirectories(DATA_DIR);
            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Files.write(FILE_PATH, lines);
        } catch (IOException e) {
            throw new AxiomException("Could not save tasks to data/axiom.txt.");
        }
    }

    static Path getFilePath() {
        return FILE_PATH;
    }

    private static void ensureWritablePath() throws AxiomException {
        if (Files.exists(DATA_DIR) && !Files.isDirectory(DATA_DIR)) {
            throw new AxiomException("Cannot save tasks because data is not a folder.");
        }
        if (Files.exists(FILE_PATH) && Files.isDirectory(FILE_PATH)) {
            throw new AxiomException("Cannot save tasks because data/axiom.txt is a directory.");
        }
    }

    private static Task parseTask(String line, int lineNumber) throws AxiomException {
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

    private static Task createTask(String type, String description, String[] parts, int lineNumber)
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

    private static AxiomException formatError(int lineNumber, String details) {
        return new AxiomException("Problem in data/axiom.txt at line " + lineNumber + ": " + details);
    }

    private static String formatTask(Task task) {
        String status = task.isDone ? "1" : "0";
        if (task instanceof Todo) {
            return "T | " + status + " | " + task.description;
        }
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + task.description + " | "
                    + DateTimeParser.formatStored(deadline.by);
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + task.description + " | "
                    + DateTimeParser.formatStored(event.from) + " to "
                    + DateTimeParser.formatStored(event.to);
        }
        throw new IllegalArgumentException("Unknown task type: " + task.getClass().getName());
    }
}

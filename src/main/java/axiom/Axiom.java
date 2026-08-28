package axiom;

import axiom.command.Command;
import axiom.parser.Parser;
import axiom.storage.Storage;
import axiom.task.Task;
import axiom.task.TaskList;
import axiom.ui.Ui;

/**
 * Entry point and orchestrator for the AXIOM chatbot.
 * Wires together {@link Ui}, {@link Storage}, {@link Parser}, and {@link TaskList}.
 */
public class Axiom {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final String loadError;

    /**
     * Creates a new AXIOM instance and loads tasks from the given file path.
     *
     * @param filePath Path to the task data file (e.g. {@code data/axiom.txt}).
     */
    public Axiom(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        TaskList loadedTasks;
        String errorMessage = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (AxiomException e) {
            errorMessage = e.getMessage();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        loadError = errorMessage;
    }

    /**
     * Starts the interactive command loop.
     */
    public void run() {
        ui.showWelcome();
        if (loadError != null) {
            ui.showError(loadError);
        }
        while (true) {
            ui.showLine();
            String input = ui.readCommand();
            try {
                if (processCommand(input)) {
                    ui.showLine();
                    break;
                }
            } catch (AxiomException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
    }

    /**
     * Dispatches a user command to the appropriate handler.
     *
     * @param input Raw command line from the user.
     * @return {@code true} if the program should exit.
     * @throws AxiomException If the command is invalid or cannot be executed.
     */
    private boolean processCommand(String input) throws AxiomException {
        switch (parser.getCommand(input)) {
        case BYE:
            ui.showGoodbye();
            return true;
        case LIST:
            ui.showTaskList(tasks);
            break;
        case MARK:
            markTask(input);
            break;
        case UNMARK:
            unmarkTask(input);
            break;
        case DELETE:
            deleteTask(input);
            break;
        case TODO:
            addTask(parser.parseTodo(input));
            break;
        case DEADLINE:
            addTask(parser.parseDeadline(input));
            break;
        case EVENT:
            addTask(parser.parseEvent(input));
            break;
        default:
            throw new AxiomException("Sorry, I don't understand that command.");
        }
        return false;
    }

    /**
     * Marks a task as done and persists the updated list.
     *
     * @param input Full {@code mark} command line.
     * @throws AxiomException If the task number is missing or invalid.
     */
    private void markTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.MARK, input, tasks.size());
        tasks.markAsDone(taskNumber - 1);
        ui.showMarked(tasks.get(taskNumber - 1));
        storage.save(tasks);
    }

    /**
     * Marks a task as not done and persists the updated list.
     *
     * @param input Full {@code unmark} command line.
     * @throws AxiomException If the task number is missing or invalid.
     */
    private void unmarkTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.UNMARK, input, tasks.size());
        tasks.markAsNotDone(taskNumber - 1);
        ui.showUnmarked(tasks.get(taskNumber - 1));
        storage.save(tasks);
    }

    /**
     * Deletes a task and persists the updated list.
     *
     * @param input Full {@code delete} command line.
     * @throws AxiomException If the task number is missing or invalid.
     */
    private void deleteTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.DELETE, input, tasks.size());
        Task removed = tasks.delete(taskNumber - 1);
        ui.showDeleted(removed, tasks.size());
        storage.save(tasks);
    }

    /**
     * Adds a task and persists the updated list.
     *
     * @param task Task to add.
     * @throws AxiomException If the task list cannot be saved.
     */
    private void addTask(Task task) throws AxiomException {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks);
    }

    /**
     * Launches AXIOM with the default data file path.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Axiom("data/axiom.txt").run();
    }
}

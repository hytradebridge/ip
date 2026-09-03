package axiom;

import axiom.command.Command;
import axiom.parser.Parser;
import axiom.storage.Storage;
import axiom.task.Task;
import axiom.task.TaskList;
import axiom.ui.Ui;

/**
 * Represents the entry point and orchestrator for the AXIOM chatbot.
 * Wires together {@link Ui}, {@link Storage}, {@link Parser}, and {@link TaskList}.
 */
public class Axiom {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final String loadError;
    private boolean isExit;

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
     * Runs the interactive command loop.
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
                System.out.println(execute(input));
                if (isExit) {
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
     * Returns the greeting shown when the GUI starts.
     *
     * @return Welcome text for the chatbot window.
     */
    public String getWelcomeMessage() {
        return ui.formatGuiWelcome();
    }

    /**
     * Returns the error from loading the data file, if any.
     *
     * @return Load error message, or {@code null} if loading succeeded.
     */
    public String getLoadError() {
        return loadError;
    }

    /**
     * Returns whether the most recent command requested the program to exit.
     *
     * @return {@code true} if the user issued a {@code bye} command.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input Raw command line from the user.
     * @return Reply to show in the GUI, including error messages.
     */
    public String getResponse(String input) {
        try {
            return execute(input);
        } catch (AxiomException e) {
            return e.getMessage();
        }
    }

    /**
     * Executes a user command and returns the reply to display.
     *
     * @param input Raw command line from the user.
     * @return Reply text for the command.
     * @throws AxiomException If the command is invalid or cannot be executed.
     */
    private String execute(String input) throws AxiomException {
        switch (parser.getCommand(input)) {
        case BYE:
            isExit = true;
            return ui.formatGoodbye();
        case LIST:
            return ui.formatTaskList(tasks);
        case FIND:
            return findTasks(input);
        case MARK:
            return markTask(input);
        case UNMARK:
            return unmarkTask(input);
        case DELETE:
            return deleteTask(input);
        case TODO:
            return addTask(parser.parseTodo(input));
        case DEADLINE:
            return addTask(parser.parseDeadline(input));
        case EVENT:
            return addTask(parser.parseEvent(input));
        default:
            throw new AxiomException("Sorry, I don't understand that command.");
        }
    }

    /**
     * Finds tasks matching a keyword in their description.
     *
     * @param input Full {@code find} command line.
     * @return Formatted matching-task text.
     * @throws AxiomException If the keyword is missing.
     */
    private String findTasks(String input) throws AxiomException {
        String keyword = parser.parseFindKeyword(input);
        return ui.formatMatchingTasks(tasks, tasks.findMatchingTaskNumbers(keyword));
    }

    /**
     * Marks a task as done and persists the updated list.
     *
     * @param input Full {@code mark} command line.
     * @return Formatted mark confirmation.
     * @throws AxiomException If the task number is missing or invalid.
     */
    private String markTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.MARK, input, tasks.size());
        tasks.markAsDone(taskNumber - 1);
        storage.save(tasks);
        return ui.formatMarked(tasks.get(taskNumber - 1));
    }

    /**
     * Marks a task as not done and persists the updated list.
     *
     * @param input Full {@code unmark} command line.
     * @return Formatted unmark confirmation.
     * @throws AxiomException If the task number is missing or invalid.
     */
    private String unmarkTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.UNMARK, input, tasks.size());
        tasks.markAsNotDone(taskNumber - 1);
        storage.save(tasks);
        return ui.formatUnmarked(tasks.get(taskNumber - 1));
    }

    /**
     * Deletes a task and persists the updated list.
     *
     * @param input Full {@code delete} command line.
     * @return Formatted delete confirmation.
     * @throws AxiomException If the task number is missing or invalid.
     */
    private String deleteTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.DELETE, input, tasks.size());
        Task removed = tasks.delete(taskNumber - 1);
        storage.save(tasks);
        return ui.formatDeleted(removed, tasks.size());
    }

    /**
     * Adds a task and persists the updated list.
     *
     * @param task Task to add.
     * @return Formatted add-task confirmation.
     * @throws AxiomException If the task list cannot be saved.
     */
    private String addTask(Task task) throws AxiomException {
        tasks.add(task);
        storage.save(tasks);
        return ui.formatTaskAdded(task, tasks.size());
    }

    /**
     * Runs AXIOM with the default data file path.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Axiom("data/axiom.txt").run();
    }
}

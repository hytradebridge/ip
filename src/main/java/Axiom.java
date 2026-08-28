/**
 * Entry point for the AXIOM chatbot.
 */
public class Axiom {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final String loadError;

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
     * Processes a user command. Returns true if the program should exit.
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

    private void markTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.MARK, input, tasks.size());
        tasks.markAsDone(taskNumber - 1);
        ui.showMarked(tasks.get(taskNumber - 1));
        storage.save(tasks);
    }

    private void unmarkTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.UNMARK, input, tasks.size());
        tasks.markAsNotDone(taskNumber - 1);
        ui.showUnmarked(tasks.get(taskNumber - 1));
        storage.save(tasks);
    }

    private void deleteTask(String input) throws AxiomException {
        int taskNumber = parser.parseTaskNumber(Command.DELETE, input, tasks.size());
        Task removed = tasks.delete(taskNumber - 1);
        ui.showDeleted(removed, tasks.size());
        storage.save(tasks);
    }

    private void addTask(Task task) throws AxiomException {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks);
    }

    public static void main(String[] args) {
        new Axiom("data/axiom.txt").run();
    }
}

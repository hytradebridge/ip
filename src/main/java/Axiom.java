import java.util.ArrayList;
import java.util.Scanner;

public class Axiom {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        String banner = "     _    __  _____ ___  __  __ \n"
                      + "    / \\   \\ \\/ /_ _/ _ \\|  \\/  |\n"
                      + "   / _ \\   \\  / | | | | | |\\/| |\n"
                      + "  / ___ \\  /  \\ | | |_| | |  | |\n"
                      + " /_/   \\_\\/_/\\_\\___\\___/|_|  |_|\n";
        System.out.println(banner);
        System.out.println("Hello! I'm AXIOM.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        Scanner in = new Scanner(System.in);
        ArrayList<Task> tasks;
        try {
            tasks = Storage.load();
        } catch (AxiomException e) {
            System.out.println(" " + e.getMessage());
            tasks = new ArrayList<>();
        }

        while (true) {
            String input = in.nextLine();
            System.out.println(LINE);
            try {
                if (processCommand(input, tasks)) {
                    System.out.println(LINE);
                    break;
                }
            } catch (AxiomException e) {
                System.out.println(" " + e.getMessage());
            }
            System.out.println(LINE);
        }
    }

    /**
     * Processes a user command. Returns true if the program should exit.
     */
    private static boolean processCommand(String input, ArrayList<Task> tasks) throws AxiomException {
        switch (Command.fromInput(input)) {
        case BYE:
            System.out.println(" Bye. Hope to see you again soon!");
            return true;
        case LIST:
            printList(tasks);
            break;
        case MARK:
            markTask(input, tasks);
            break;
        case UNMARK:
            unmarkTask(input, tasks);
            break;
        case DELETE:
            deleteTask(input, tasks);
            break;
        case TODO:
            addTodo(input, tasks);
            break;
        case DEADLINE:
            addDeadline(input, tasks);
            break;
        case EVENT:
            addEvent(input, tasks);
            break;
        default:
            throw new AxiomException("Sorry, I don't understand that command.");
        }
        return false;
    }

    private static void printList(ArrayList<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    private static void markTask(String input, ArrayList<Task> tasks) throws AxiomException {
        int taskNumber = parseTaskNumber(Command.MARK, input, tasks.size());
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
        saveTasks(tasks);
    }

    private static void unmarkTask(String input, ArrayList<Task> tasks) throws AxiomException {
        int taskNumber = parseTaskNumber(Command.UNMARK, input, tasks.size());
        Task task = tasks.get(taskNumber - 1);
        task.markAsNotDone();
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
        saveTasks(tasks);
    }

    private static void deleteTask(String input, ArrayList<Task> tasks) throws AxiomException {
        int taskNumber = parseTaskNumber(Command.DELETE, input, tasks.size());
        Task removed = tasks.remove(taskNumber - 1);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removed);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        saveTasks(tasks);
    }

    private static int parseTaskNumber(Command command, String input, int taskCount) throws AxiomException {
        String argument = command.getArgument(input);
        if (argument.isEmpty()) {
            throw new AxiomException("Please specify which task to " + command.getKeyword()
                    + ". Usage: " + command.getKeyword() + " <task number>");
        }
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

    private static void addTodo(String input, ArrayList<Task> tasks) throws AxiomException {
        String description = Command.TODO.getArgument(input);
        if (description.isEmpty()) {
            throw new AxiomException("A todo needs a description. Usage: todo <description>");
        }
        addTask(tasks, new Todo(description));
    }

    private static void addDeadline(String input, ArrayList<Task> tasks) throws AxiomException {
        String remainder = Command.DEADLINE.getArgument(input);
        if (remainder.isEmpty()) {
            throw new AxiomException("A deadline needs a description and a /by time. "
                    + "Usage: deadline <description> /by <time>");
        }
        int byIndex = remainder.indexOf(" /by ");
        if (byIndex == -1) {
            throw new AxiomException("A deadline must include /by. "
                    + "Usage: deadline <description> /by <time>");
        }
        String description = remainder.substring(0, byIndex).trim();
        String by = remainder.substring(byIndex + 5).trim();
        if (description.isEmpty()) {
            throw new AxiomException("A deadline needs a description. "
                    + "Usage: deadline <description> /by <time>");
        }
        if (by.isEmpty()) {
            throw new AxiomException("A deadline needs a /by time. "
                    + "Usage: deadline <description> /by <time>");
        }
        addTask(tasks, new Deadline(description, DateTimeParser.parse(by)));
    }

    private static void addEvent(String input, ArrayList<Task> tasks) throws AxiomException {
        String remainder = Command.EVENT.getArgument(input);
        if (remainder.isEmpty()) {
            throw new AxiomException("An event needs a description, /from, and /to times. "
                    + "Usage: event <description> /from <start> /to <end>");
        }
        int fromIndex = remainder.indexOf(" /from ");
        int toIndex = remainder.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new AxiomException("An event must include /from and /to. "
                    + "Usage: event <description> /from <start> /to <end>");
        }
        String description = remainder.substring(0, fromIndex).trim();
        String from = remainder.substring(fromIndex + 7, toIndex).trim();
        String to = remainder.substring(toIndex + 5).trim();
        if (description.isEmpty()) {
            throw new AxiomException("An event needs a description. "
                    + "Usage: event <description> /from <start> /to <end>");
        }
        if (from.isEmpty()) {
            throw new AxiomException("An event needs a /from time. "
                    + "Usage: event <description> /from <start> /to <end>");
        }
        if (to.isEmpty()) {
            throw new AxiomException("An event needs a /to time. "
                    + "Usage: event <description> /from <start> /to <end>");
        }
        addTask(tasks, new Event(description, DateTimeParser.parse(from), DateTimeParser.parse(to)));
    }

    private static void addTask(ArrayList<Task> tasks, Task task) throws AxiomException {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        saveTasks(tasks);
    }

    private static void saveTasks(ArrayList<Task> tasks) throws AxiomException {
        Storage.save(tasks);
    }
}

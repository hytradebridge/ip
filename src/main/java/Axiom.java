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
        ArrayList<Task> tasks = new ArrayList<>();

        while (true) {
            String input = in.nextLine();
            System.out.println(LINE);
            try {
                if (input.equals("bye")) {
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                }
                processCommand(input, tasks);
            } catch (AxiomException e) {
                System.out.println(" " + e.getMessage());
            }
            System.out.println(LINE);
        }
    }

    private static void processCommand(String input, ArrayList<Task> tasks) throws AxiomException {
        if (input.equals("list")) {
            printList(tasks);
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            markTask(input, tasks);
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            unmarkTask(input, tasks);
        } else if (input.equals("delete") || input.startsWith("delete ")) {
            deleteTask(input, tasks);
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            addTodo(input, tasks);
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            addDeadline(input, tasks);
        } else if (input.equals("event") || input.startsWith("event ")) {
            addEvent(input, tasks);
        } else {
            throw new AxiomException("Sorry, I don't understand that command.");
        }
    }

    private static void printList(ArrayList<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    private static void markTask(String input, ArrayList<Task> tasks) throws AxiomException {
        int taskNumber = parseTaskNumber(input, "mark", tasks.size());
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    private static void unmarkTask(String input, ArrayList<Task> tasks) throws AxiomException {
        int taskNumber = parseTaskNumber(input, "unmark", tasks.size());
        Task task = tasks.get(taskNumber - 1);
        task.markAsNotDone();
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    private static void deleteTask(String input, ArrayList<Task> tasks) throws AxiomException {
        int taskNumber = parseTaskNumber(input, "delete", tasks.size());
        Task removed = tasks.remove(taskNumber - 1);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removed);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    private static int parseTaskNumber(String input, String command, int taskCount) throws AxiomException {
        String argument = input.equals(command) ? "" : input.substring(command.length() + 1).trim();
        if (argument.isEmpty()) {
            throw new AxiomException("Please specify which task to " + command
                    + ". Usage: " + command + " <task number>");
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
                    + command + " <task number>");
        }
    }

    private static void addTodo(String input, ArrayList<Task> tasks) throws AxiomException {
        String description = input.equals("todo") ? "" : input.substring(5).trim();
        if (description.isEmpty()) {
            throw new AxiomException("A todo needs a description. Usage: todo <description>");
        }
        addTask(tasks, new Todo(description));
    }

    private static void addDeadline(String input, ArrayList<Task> tasks) throws AxiomException {
        if (input.equals("deadline")) {
            throw new AxiomException("A deadline needs a description and a /by time. "
                    + "Usage: deadline <description> /by <time>");
        }
        String remainder = input.substring(9);
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
        addTask(tasks, new Deadline(description, by));
    }

    private static void addEvent(String input, ArrayList<Task> tasks) throws AxiomException {
        if (input.equals("event")) {
            throw new AxiomException("An event needs a description, /from, and /to times. "
                    + "Usage: event <description> /from <start> /to <end>");
        }
        String remainder = input.substring(6);
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
        addTask(tasks, new Event(description, from, to));
    }

    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }
}

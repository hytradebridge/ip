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
        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (true) {
            String input = in.nextLine();
            System.out.println(LINE);
            try {
                if (input.equals("bye")) {
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                }
                taskCount = processCommand(input, tasks, taskCount);
            } catch (AxiomException e) {
                System.out.println(" " + e.getMessage());
            }
            System.out.println(LINE);
        }
    }

    private static int processCommand(String input, Task[] tasks, int taskCount) throws AxiomException {
        if (input.equals("list")) {
            printList(tasks, taskCount);
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            markTask(input, tasks, taskCount);
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            unmarkTask(input, tasks, taskCount);
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            return addTodo(input, tasks, taskCount);
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            return addDeadline(input, tasks, taskCount);
        } else if (input.equals("event") || input.startsWith("event ")) {
            return addEvent(input, tasks, taskCount);
        } else {
            throw new AxiomException("Sorry, I don't understand that command.");
        }
        return taskCount;
    }

    private static void printList(Task[] tasks, int taskCount) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
    }

    private static void markTask(String input, Task[] tasks, int taskCount) throws AxiomException {
        int taskNumber = parseTaskNumber(input, "mark", taskCount);
        Task task = tasks[taskNumber - 1];
        task.markAsDone();
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    private static void unmarkTask(String input, Task[] tasks, int taskCount) throws AxiomException {
        int taskNumber = parseTaskNumber(input, "unmark", taskCount);
        Task task = tasks[taskNumber - 1];
        task.markAsNotDone();
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
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

    private static int addTodo(String input, Task[] tasks, int taskCount) throws AxiomException {
        String description = input.equals("todo") ? "" : input.substring(5).trim();
        if (description.isEmpty()) {
            throw new AxiomException("A todo needs a description. Usage: todo <description>");
        }
        return addTask(tasks, taskCount, new Todo(description));
    }

    private static int addDeadline(String input, Task[] tasks, int taskCount) throws AxiomException {
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
        return addTask(tasks, taskCount, new Deadline(description, by));
    }

    private static int addEvent(String input, Task[] tasks, int taskCount) throws AxiomException {
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
        return addTask(tasks, taskCount, new Event(description, from, to));
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) throws AxiomException {
        if (taskCount >= tasks.length) {
            throw new AxiomException("Your task list is full (maximum " + tasks.length + " tasks).");
        }
        tasks[taskCount] = task;
        taskCount++;
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }
}

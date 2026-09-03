package axiom.ui;

import java.util.ArrayList;
import java.util.Scanner;

import axiom.task.Task;
import axiom.task.TaskList;

/**
 * Represents a component that handles interactions with the user via standard input and output.
 */
public class Ui {
    private static final String LINE = "__________________________________________";
    private static final String BANNER = "     _    __  _____ ___  __  __ \n"
                                       + "    / \\   \\ \\/ /_ _/ _ \\|  \\/  |\n"
                                       + "   / _ \\   \\  / | | | | | |\\/| |\n"
                                       + "  / ___ \\  /  \\ | | |_| | |  | |\n"
                                       + " /_/   \\_\\/_/\\_\\___\\___/|_|  |_|\n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome banner and greeting.
     */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Hello! I'm AXIOM.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Displays a horizontal separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Returns the next command entered by the user.
     *
     * @return The command line entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error description.
     */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /**
     * Returns the greeting shown in the GUI, including the ASCII banner and separator line.
     *
     * @return Welcome text for the chatbot window.
     */
    public String formatGuiWelcome() {
        return BANNER + "\nHello! I'm AXIOM.\nWhat can I do for you?\n" + LINE;
    }

    /**
     * Returns the goodbye message.
     *
     * @return Goodbye text.
     */
    public String formatGoodbye() {
        return " Bye. Hope to see you again soon!";
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        System.out.println(formatGoodbye());
    }

    /**
     * Returns all tasks in the list with their one-based indices.
     *
     * @param tasks Task list to display.
     * @return Formatted task list text.
     */
    public String formatTaskList(TaskList tasks) {
        StringBuilder builder = new StringBuilder(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            builder.append('\n').append(" ").append(i + 1).append('.').append(tasks.get(i));
        }
        return builder.toString();
    }

    /**
     * Displays all tasks in the list with their one-based indices.
     *
     * @param tasks Task list to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(formatTaskList(tasks));
    }

    /**
     * Returns tasks whose descriptions match the search keyword.
     *
     * @param tasks Task list to search.
     * @param matchingNumbers One-based indices of matching tasks.
     * @return Formatted matching-task text.
     */
    public String formatMatchingTasks(TaskList tasks, ArrayList<Integer> matchingNumbers) {
        StringBuilder builder = new StringBuilder(" Here are the matching tasks in your list:");
        for (int number : matchingNumbers) {
            builder.append('\n').append(" ").append(number).append('.').append(tasks.get(number - 1));
        }
        return builder.toString();
    }

    /**
     * Displays tasks whose descriptions match the search keyword.
     *
     * @param tasks Task list to search.
     * @param matchingNumbers One-based indices of matching tasks.
     */
    public void showMatchingTasks(TaskList tasks, ArrayList<Integer> matchingNumbers) {
        System.out.println(formatMatchingTasks(tasks, matchingNumbers));
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param taskCount Total number of tasks after the addition.
     * @return Formatted add-task confirmation.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return " Got it. I've added this task:\n   " + task
                + "\n Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param taskCount Total number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(formatTaskAdded(task, taskCount));
    }

    /**
     * Returns confirmation that a task was marked as done.
     *
     * @param task The task that was marked.
     * @return Formatted mark confirmation.
     */
    public String formatMarked(Task task) {
        return " Nice! I've marked this task as done:\n   " + task;
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task The task that was marked.
     */
    public void showMarked(Task task) {
        System.out.println(formatMarked(task));
    }

    /**
     * Returns confirmation that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     * @return Formatted unmark confirmation.
     */
    public String formatUnmarked(Task task) {
        return " OK, I've marked this task as not done yet:\n   " + task;
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     */
    public void showUnmarked(Task task) {
        System.out.println(formatUnmarked(task));
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task The task that was removed.
     * @param taskCount Total number of tasks after the deletion.
     * @return Formatted delete confirmation.
     */
    public String formatDeleted(Task task, int taskCount) {
        return " Noted. I've removed this task:\n   " + task
                + "\n Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task The task that was removed.
     * @param taskCount Total number of tasks after the deletion.
     */
    public void showDeleted(Task task, int taskCount) {
        System.out.println(formatDeleted(task, taskCount));
    }
}

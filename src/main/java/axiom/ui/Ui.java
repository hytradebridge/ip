package axiom.ui;

import java.util.ArrayList;
import java.util.Scanner;

import axiom.task.Task;
import axiom.task.TaskList;

/**
 * Represents a component that handles interactions with the user via standard input and output.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
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
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Displays all tasks in the list with their one-based indices.
     *
     * @param tasks Task list to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays tasks whose descriptions match the search keyword.
     *
     * @param tasks Task list to search.
     * @param matchingNumbers One-based indices of matching tasks.
     */
    public void showMatchingTasks(TaskList tasks, ArrayList<Integer> matchingNumbers) {
        System.out.println(" Here are the matching tasks in your list:");
        for (int number : matchingNumbers) {
            System.out.println(" " + number + "." + tasks.get(number - 1));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param taskCount Total number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task The task that was marked.
     */
    public void showMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     */
    public void showUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task The task that was removed.
     * @param taskCount Total number of tasks after the deletion.
     */
    public void showDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}

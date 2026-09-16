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
    private static final String TASK_DETAIL_INDENT = "   ";
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
        System.out.println(formatWelcome());
    }

    /**
     * Displays a horizontal separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Displays a message to the user.
     *
     * @param message Text to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
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
     * Returns the next command entered by the user.
     *
     * @return The command line entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns the welcome banner and greeting.
     *
     * @return Welcome text for the chatbot.
     */
    public String formatWelcome() {
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
     * Returns all tasks in the list with their one-based indices.
     *
     * @param tasks Task list to display.
     * @return Formatted task list text.
     */
    public String formatTaskList(TaskList tasks) {
        StringBuilder builder = new StringBuilder(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            appendNumberedTask(builder, i + 1, tasks.get(i));
        }
        return builder.toString();
    }

    /**
     * Returns tasks whose descriptions match the search keyword.
     *
     * @param tasks Task list to search.
     * @param matchingIndexes Zero-based indexes of matching tasks.
     * @return Formatted matching-task text.
     */
    public String formatMatchingTasks(TaskList tasks, ArrayList<Integer> matchingIndexes) {
        assert tasks != null : "Task list should not be null";
        assert matchingIndexes != null : "Matching indexes should not be null";
        StringBuilder builder = new StringBuilder(" Here are the matching tasks in your list:");
        for (int index : matchingIndexes) {
            assert index >= 0 && index < tasks.size()
                    : "Matching index should be a valid 0-based task index";
            appendNumberedTask(builder, index + 1, tasks.get(index));
        }
        return builder.toString();
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param taskCount Total number of tasks after the addition.
     * @return Formatted add-task confirmation.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return " Got it. I've added this task:" + formatIndentedTask(task)
                + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Returns confirmation that a task was marked as done.
     *
     * @param task The task that was marked.
     * @return Formatted mark confirmation.
     */
    public String formatMarked(Task task) {
        return " Nice! I've marked this task as done:" + formatIndentedTask(task);
    }

    /**
     * Returns confirmation that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     * @return Formatted unmark confirmation.
     */
    public String formatUnmarked(Task task) {
        return " OK, I've marked this task as not done yet:" + formatIndentedTask(task);
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task The task that was removed.
     * @param taskCount Total number of tasks after the deletion.
     * @return Formatted delete confirmation.
     */
    public String formatDeleted(Task task, int taskCount) {
        return " Noted. I've removed this task:" + formatIndentedTask(task)
                + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Returns the line that reports how many tasks remain in the list.
     *
     * @param taskCount Current number of tasks.
     * @return Formatted task-count text.
     */
    private String formatTaskCount(int taskCount) {
        return " Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns a task on its own indented line.
     *
     * @param task Task to display.
     * @return Formatted task line.
     */
    private String formatIndentedTask(Task task) {
        return "\n" + TASK_DETAIL_INDENT + task;
    }

    /**
     * Appends a numbered task line to {@code builder}.
     *
     * @param builder Output being built.
     * @param taskNumber One-based task number to display.
     * @param task Task to display.
     */
    private void appendNumberedTask(StringBuilder builder, int taskNumber, Task task) {
        builder.append('\n').append(" ").append(taskNumber).append('.').append(task);
    }
}

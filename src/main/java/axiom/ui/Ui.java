package axiom.ui;

import java.util.Scanner;

import axiom.task.Task;
import axiom.task.TaskList;

/**
 * Handles interactions with the user.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "     _    __  _____ ___  __  __ \n"
                                       + "    / \\   \\ \\/ /_ _/ _ \\|  \\/  |\n"
                                       + "   / _ \\   \\  / | | | | | |\\/| |\n"
                                       + "  / ___ \\  /  \\ | | |_| | |  | |\n"
                                       + " /_/   \\_\\/_/\\_\\___\\___/|_|  |_|\n";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Hello! I'm AXIOM.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showError(String message) {
        System.out.println(" " + message);
    }

    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    public void showMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    public void showUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    public void showDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}

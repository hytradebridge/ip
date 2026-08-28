import java.util.Scanner;

public class Axiom {
    public static void main(String[] args) {
        String banner = "     _    __  _____ ___  __  __ \n"
                      + "    / \\   \\ \\/ /_ _/ _ \\|  \\/  |\n"
                      + "   / _ \\   \\  / | | | | | |\\/| |\n"
                      + "  / ___ \\  /  \\ | | |_| | |  | |\n"
                      + " /_/   \\_\\/_/\\_\\___\\___/|_|  |_|\n";
        System.out.println(banner);
        System.out.println("Hello! I'm AXIOM.");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner in = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (true) {
            String input = in.nextLine();
            System.out.println("____________________________________________________________");
            if (input.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            } else if (input.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5).trim());
                Task task = tasks[taskNumber - 1];
                task.markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + task);
            } else if (input.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(input.substring(7).trim());
                Task task = tasks[taskNumber - 1];
                task.markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + task);
            } else if (input.startsWith("todo ")) {
                Task task = new Todo(input.substring(5).trim());
                tasks[taskCount] = task;
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + task);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else if (input.startsWith("deadline ")) {
                String remainder = input.substring(9);
                int byIndex = remainder.indexOf(" /by ");
                String description = remainder.substring(0, byIndex).trim();
                String by = remainder.substring(byIndex + 5).trim();
                Task task = new Deadline(description, by);
                tasks[taskCount] = task;
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + task);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else if (input.startsWith("event ")) {
                String remainder = input.substring(6);
                int fromIndex = remainder.indexOf(" /from ");
                int toIndex = remainder.indexOf(" /to ");
                String description = remainder.substring(0, fromIndex).trim();
                String from = remainder.substring(fromIndex + 7, toIndex).trim();
                String to = remainder.substring(toIndex + 5).trim();
                Task task = new Event(description, from, to);
                tasks[taskCount] = task;
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + task);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            }
            System.out.println("____________________________________________________________");
        }
    }
}

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
                    System.out.println(" " + (i + 1) + ".[" + tasks[i].getStatusIcon() + "] "
                            + tasks[i].description);
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
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println(" added: " + input);
            }
            System.out.println("____________________________________________________________");
        }
    }
}

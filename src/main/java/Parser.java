/**
 * Makes sense of user commands and converts input into tasks or task indices.
 */
public class Parser {

    public Command getCommand(String input) {
        return Command.fromInput(input);
    }

    public int parseTaskNumber(Command command, String input, int taskCount) throws AxiomException {
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

    public Task parseTodo(String input) throws AxiomException {
        String description = Command.TODO.getArgument(input);
        if (description.isEmpty()) {
            throw new AxiomException("A todo needs a description. Usage: todo <description>");
        }
        return new Todo(description);
    }

    public Task parseDeadline(String input) throws AxiomException {
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
        return new Deadline(description, DateTimeParser.parse(by));
    }

    public Task parseEvent(String input) throws AxiomException {
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
        return new Event(description, DateTimeParser.parse(from), DateTimeParser.parse(to));
    }
}

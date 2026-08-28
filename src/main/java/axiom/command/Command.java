package axiom.command;

/**
 * Represents the commands supported by the AXIOM chatbot.
 */
public enum Command {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    UNKNOWN("");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword that identifies this command in user input.
     *
     * @return The command keyword (e.g. {@code "todo"}).
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the argument portion of the user input after the command keyword.
     *
     * @param input Full command line from the user.
     * @return The argument text, or an empty string if none was provided.
     */
    public String getArgument(String input) {
        if (input.equals(keyword)) {
            return "";
        }
        return input.substring(keyword.length() + 1).trim();
    }

    /**
     * Returns the command keyword from a line of user input.
     *
     * @param input Full command line from the user.
     * @return The matching command, or {@link #UNKNOWN} if no command matches.
     */
    public static Command fromInput(String input) {
        for (Command command : values()) {
            if (command == UNKNOWN) {
                continue;
            }
            if (input.equals(command.keyword) || input.startsWith(command.keyword + " ")) {
                return command;
            }
        }
        return UNKNOWN;
    }
}

package axiom.command;

import java.util.Arrays;

/**
 * Represents the commands supported by the AXIOM chatbot.
 */
public enum Command {
    BYE("bye"),
    LIST("list"),
    SORT("sort"),
    FIND("find"),
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
     * Returns whether {@code input} is this command, with or without arguments.
     *
     * @param input Full command line from the user.
     * @return {@code true} if the input is exactly the keyword or starts with the keyword and a space.
     */
    public boolean matches(String input) {
        return input.equals(keyword) || input.startsWith(keyword + " ");
    }

    /**
     * Returns the argument portion of the user input after the command keyword.
     *
     * @param input Full command line from the user.
     * @return The argument text, or an empty string if none was provided.
     */
    public String getArgument(String input) {
        assert this != UNKNOWN : "UNKNOWN has no keyword from which to extract an argument";
        assert matches(input) : "getArgument should only be used after this command was matched";
        return input.substring(keyword.length()).trim();
    }

    /**
     * Returns the command keyword from a line of user input.
     *
     * @param input Full command line from the user.
     * @return The matching command, or {@link #UNKNOWN} if no command matches.
     */
    public static Command fromInput(String input) {
        return Arrays.stream(values())
                .filter(command -> command != UNKNOWN)
                .filter(command -> command.matches(input))
                .findFirst()
                .orElse(UNKNOWN);
    }
}

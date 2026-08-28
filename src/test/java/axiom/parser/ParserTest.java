package axiom.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import axiom.AxiomException;
import axiom.command.Command;
import axiom.task.Deadline;
import axiom.task.Event;
import axiom.task.Task;
import axiom.task.Todo;

class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void getCommand_validInput_returnsCommand() {
        assertEquals(Command.LIST, parser.getCommand("list"));
        assertEquals(Command.TODO, parser.getCommand("todo read book"));
    }

    @Test
    void getCommand_unknownInput_returnsUnknown() {
        assertEquals(Command.UNKNOWN, parser.getCommand("blah"));
    }

    @Test
    void parseTaskNumber_validNumber_returnsOneBasedIndex() throws AxiomException {
        assertEquals(2, parser.parseTaskNumber(Command.MARK, "mark 2", 3));
    }

    @Test
    void parseTaskNumber_missingArgument_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.MARK, "mark", 3));
        assertEquals("Please specify which task to mark. Usage: mark <task number>",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_nonNumericArgument_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.DELETE, "delete abc", 3));
        assertEquals("'abc' is not a valid task number. Usage: delete <task number>",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_outOfRangeArgument_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.UNMARK, "unmark 99", 2));
        assertEquals("That task number isn't in your list. You currently have 2 task(s).",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_zeroTaskCount_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.MARK, "mark 1", 0));
        assertEquals("That task number isn't in your list. You currently have 0 task(s).",
                exception.getMessage());
    }

    @Test
    void parseTodo_validInput_returnsTodo() throws AxiomException {
        Task task = parser.parseTodo("todo read book");
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    void parseTodo_emptyDescription_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTodo("todo"));
        assertEquals("A todo needs a description. Usage: todo <description>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_validInput_returnsDeadline() throws AxiomException {
        Task task = parser.parseDeadline("deadline return book /by 2019-06-06");
        assertInstanceOf(Deadline.class, task);
        assertEquals("return book", task.getDescription());
    }

    @Test
    void parseDeadline_missingBy_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline homework"));
        assertEquals("A deadline must include /by. Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_emptyInput_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline"));
        assertEquals("A deadline needs a description and a /by time. "
                        + "Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_missingDescription_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline /by 2019-06-06"));
        assertEquals("A deadline must include /by. Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_missingByTime_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline homework /by"));
        assertEquals("A deadline must include /by. Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_invalidDate_exceptionThrown() {
        assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline homework /by no idea"));
    }

    @Test
    void parseEvent_validInput_returnsEvent() throws AxiomException {
        Task task = parser.parseEvent(
                "event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600");
        assertInstanceOf(Event.class, task);
        assertEquals("project meeting", task.getDescription());
    }

    @Test
    void parseEvent_emptyInput_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event"));
        assertEquals("An event needs a description, /from, and /to times. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingFromAndTo_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event meeting"));
        assertEquals("An event must include /from and /to. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingDescription_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event /from 2019-08-06 1400 /to 2019-08-06 1600"));
        assertEquals("An event must include /from and /to. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingFromTime_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event meeting /from  /to 2019-08-06 1600"));
        assertEquals("An event needs a /from time. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingToTime_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event meeting /from 2019-08-06 1400 /to"));
        assertEquals("An event must include /from and /to. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }
}

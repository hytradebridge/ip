package axiom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AxiomTest {
    @TempDir
    private Path tempDir;
    private Axiom axiom;

    @BeforeEach
    void setUp() {
        axiom = new Axiom(tempDir.resolve("axiom.txt").toString());
    }

    @Test
    void getWelcomeMessage_newSession_containsGreeting() {
        String welcome = axiom.getWelcomeMessage();
        assertTrue(welcome.contains("AXIOM"));
        assertTrue(welcome.contains("What can I do for you?"));
        assertTrue(welcome.contains("_____"));
        assertTrue(welcome.contains("____________________________________________________________"));
    }

    @Test
    void getResponse_todoCommand_returnsAddedMessage() {
        String response = axiom.getResponse("todo read book");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("read book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
        assertFalse(axiom.isExit());
    }

    @Test
    void getResponse_listAfterAdding_returnsTask() {
        axiom.getResponse("todo read book");
        String response = axiom.getResponse("list");
        assertTrue(response.contains("Here are the tasks in your list:"));
        assertTrue(response.contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_unknownCommand_returnsErrorMessage() {
        String response = axiom.getResponse("blargh");
        assertTrue(response.contains("Sorry, I don't understand that command."));
        assertFalse(axiom.isExit());
    }

    @Test
    void getResponse_bye_setsExitFlag() {
        String response = axiom.getResponse("bye");
        assertTrue(response.contains("Bye. Hope to see you again soon!"));
        assertTrue(axiom.isExit());
    }
}

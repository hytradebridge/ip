package axiom.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskListTest {
    private TaskList taskList;
    private Todo firstTask;
    private Todo secondTask;

    @BeforeEach
    void setUp() {
        taskList = new TaskList();
        firstTask = new Todo("read book");
        secondTask = new Todo("return book");
    }

    @Test
    void add_singleTask_increasesSize() {
        taskList.add(firstTask);
        assertEquals(1, taskList.size());
        assertEquals(firstTask, taskList.get(0));
    }

    @Test
    void add_multipleTasks_preservesOrder() {
        taskList.add(firstTask);
        taskList.add(secondTask);
        assertEquals(2, taskList.size());
        assertEquals(firstTask, taskList.get(0));
        assertEquals(secondTask, taskList.get(1));
    }

    @Test
    void delete_existingTask_returnsRemovedTask() {
        taskList.add(firstTask);
        taskList.add(secondTask);
        Task removed = taskList.delete(0);
        assertEquals(firstTask, removed);
        assertEquals(1, taskList.size());
        assertEquals(secondTask, taskList.get(0));
    }

    @Test
    void getTasks_returnsInternalList() {
        taskList.add(firstTask);
        ArrayList<Task> tasks = taskList.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(firstTask, tasks.get(0));
    }

    @Test
    void markAsDone_validIndex_marksTaskDone() {
        taskList.add(firstTask);
        taskList.markAsDone(0);
        assertTrue(taskList.get(0).isDone());
    }

    @Test
    void markAsNotDone_doneTask_marksTaskNotDone() {
        taskList.add(firstTask);
        taskList.markAsDone(0);
        taskList.markAsNotDone(0);
        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void constructor_withExistingList_usesProvidedTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(firstTask);
        TaskList loadedList = new TaskList(tasks);
        assertEquals(1, loadedList.size());
        assertEquals(firstTask, loadedList.get(0));
    }

    @Test
    void findMatchingTaskNumbers_matchingKeyword_returnsOriginalTaskNumbers() {
        taskList.add(firstTask);
        taskList.add(new Todo("buy bread"));
        taskList.add(secondTask);
        ArrayList<Integer> matches = taskList.findMatchingTaskNumbers("book");
        assertEquals(2, matches.size());
        assertEquals(1, matches.get(0));
        assertEquals(3, matches.get(1));
    }

    @Test
    void findMatchingTaskNumbers_caseInsensitive_returnsMatches() {
        taskList.add(new Todo("Read Book"));
        ArrayList<Integer> matches = taskList.findMatchingTaskNumbers("book");
        assertEquals(1, matches.size());
        assertEquals(1, matches.get(0));
    }

    @Test
    void findMatchingTaskNumbers_noMatch_returnsEmptyList() {
        taskList.add(firstTask);
        assertTrue(taskList.findMatchingTaskNumbers("xyz").isEmpty());
    }
}

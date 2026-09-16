package axiom.task;

import java.util.ArrayList;

/**
 * Contains the list of tasks and supports operations to modify it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the given collection.
     *
     * @param tasks Existing tasks to wrap (typically loaded from storage).
     */
    public TaskList(ArrayList<Task> tasks) {
        // Storage.load() always returns a list (possibly empty); null would be a caller bug.
        assert tasks != null : "Task collection should not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add. Must not be {@code null}.
     */
    public void add(Task task) {
        // Parser constructs a concrete Task before Axiom calls add.
        assert task != null : "Cannot add a null task";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Zero-based index of the task to remove. Must be in range {@code [0, size())}.
     * @return The removed task.
     */
    public Task delete(int index) {
        // Parser.parseTaskNumber already rejected missing/out-of-range user input.
        assert isValidIndex(index) : "Delete index should be within list bounds";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index of the task. Must be in range {@code [0, size())}.
     * @return The task at that index.
     */
    public Task get(int index) {
        // Callers convert a Parser-validated 1-based number to a 0-based index.
        assert isValidIndex(index) : "Get index should be within list bounds";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The current list size.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return The backing {@link ArrayList} (used by {@link axiom.storage.Storage}).
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index Zero-based index of the task. Must be in range {@code [0, size())}.
     */
    public void markAsDone(int index) {
        assert isValidIndex(index) : "Mark index should be within list bounds";
        tasks.get(index).markAsDone();
        assert tasks.get(index).isDone() : "Task should be done after markAsDone";
    }

    /**
     * Returns the one-based indices of tasks whose description contains the keyword.
     *
     * @param keyword Keyword to search for (case-insensitive). Must not be empty.
     * @return One-based task numbers of matching tasks, in list order.
     */
    public ArrayList<Integer> findMatchingTaskNumbers(String keyword) {
        // Parser.parseFindKeyword already rejected a missing keyword.
        assert keyword != null && !keyword.isBlank() : "Find keyword should not be null or blank";
        ArrayList<Integer> matchingNumbers = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase().contains(lowerKeyword)) {
                matchingNumbers.add(i + 1);
            }
        }
        for (int number : matchingNumbers) {
            assert number >= 1 && number <= tasks.size()
                    : "Matching task numbers should be valid 1-based indices";
        }
        return matchingNumbers;
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index Zero-based index of the task. Must be in range {@code [0, size())}.
     */
    public void markAsNotDone(int index) {
        assert isValidIndex(index) : "Unmark index should be within list bounds";
        tasks.get(index).markAsNotDone();
        assert !tasks.get(index).isDone() : "Task should not be done after markAsNotDone";
    }

    /**
     * Returns whether {@code index} is a valid 0-based position in this list.
     *
     * @param index Candidate index.
     * @return {@code true} if the index is in range.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}

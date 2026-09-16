package axiom.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Contains the list of tasks and supports operations to modify it.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the given collection.
     *
     * @param tasks Existing tasks to wrap (typically loaded from storage).
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index of the task.
     * @return The task at that index.
     */
    public Task get(int index) {
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
     * {@inheritDoc}
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index Zero-based index of the task.
     */
    public void markAsDone(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index Zero-based index of the task.
     */
    public void markAsNotDone(int index) {
        tasks.get(index).markAsNotDone();
    }

    /**
     * Returns the zero-based indexes of tasks whose description contains the keyword.
     *
     * @param keyword Keyword to search for (case-insensitive).
     * @return Zero-based indexes of matching tasks, in list order.
     */
    public ArrayList<Integer> findMatchingIndexes(String keyword) {
        ArrayList<Integer> matchingIndexes = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase().contains(lowerKeyword)) {
                matchingIndexes.add(i);
            }
        }
        return matchingIndexes;
    }
}

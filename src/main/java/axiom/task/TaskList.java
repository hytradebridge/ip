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
        this.tasks = tasks;
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
     * @param index Zero-based index of the task.
     */
    public void markAsDone(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Returns the one-based indices of tasks whose description contains the keyword.
     *
     * @param keyword Keyword to search for (case-insensitive).
     * @return One-based task numbers of matching tasks, in list order.
     */
    public ArrayList<Integer> findMatchingTaskNumbers(String keyword) {
        ArrayList<Integer> matchingNumbers = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase().contains(lowerKeyword)) {
                matchingNumbers.add(i + 1);
            }
        }
        return matchingNumbers;
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index Zero-based index of the task.
     */
    public void markAsNotDone(int index) {
        tasks.get(index).markAsNotDone();
    }
}

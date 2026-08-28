package axiom.task;

/**
 * Represents a task with a description and done status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new task that is initially not done.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return The description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the status icon shown in task output.
     *
     * @return {@code "X"} if done, or a single space if not done.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns a string representation of this task's status and description.
     *
     * @return Formatted status and description (e.g. {@code "[ ] read book"}).
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}

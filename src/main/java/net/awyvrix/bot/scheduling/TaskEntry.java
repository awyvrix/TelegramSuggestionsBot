package net.awyvrix.bot.scheduling;

public class TaskEntry {
    private final ScheduledTask task;
    private long nextExecution;

    public TaskEntry(ScheduledTask task, long nextExecution) {
        this.task = task;
        this.nextExecution = nextExecution;
    }

    public void execute() {
        task.execute();
    }

    public void update() {
        Duration duration = task.duration();
        nextExecution = System.nanoTime() + duration.timeUnit().toNanos(duration.period());
    }

    public long nextExecution() {
        return nextExecution;
    }
}

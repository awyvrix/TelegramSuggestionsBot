package net.awyvrix.bot.scheduling;

import java.util.ArrayList;
import java.util.List;

import static net.awyvrix.bot.Main.LOGGER;

public class TaskScheduler {
    private final List<TaskEntry> tasks = new ArrayList<>();
    private long delta = 15_000_000_000L;
    private boolean initialised = false;

    private volatile int completed = 0;

    public void register(ScheduledTask task) {
        if (task.id() == null || task.id().isBlank()) throw new IllegalStateException("ID cannot be null or blank: " + task.getClass());
        if (initialised) throw new IllegalStateException("Cannot register task after initialisation" + task.id());
        if (task.duration() == null) throw new IllegalStateException("Duration cannot be null: " + task.id());
        tasks.add(new TaskEntry(task, System.nanoTime()));
        LOGGER.info("Task registered: {} with duration: {} {}", task.id(), task.duration().period(), task.duration().timeUnit().toString());
    }

    public void tick() {
        long now = System.nanoTime();
        long next = Long.MAX_VALUE;

        for (TaskEntry entry : tasks) {
            if (now >= entry.nextExecution()) {
                try {
                    entry.execute();
                    completed++;
                } catch (RuntimeException e) {
                    LOGGER.error("Scheduled task failed", e);
                } finally {
                    entry.update();
                }
            }
            next = Math.min(next, entry.nextExecution());
        }
        if (next == Long.MAX_VALUE) return;
        delta = Math.max(0, next - System.nanoTime());
    }

    public long delta() {
        return delta;
    }

    public void setInitialised() {
        initialised = true;
    }

    public int completed() {
        return completed;
    }
}
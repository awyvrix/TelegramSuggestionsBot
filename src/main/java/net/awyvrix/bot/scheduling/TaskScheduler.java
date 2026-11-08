package net.awyvrix.bot.scheduling;

import net.awyvrix.bot.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TaskScheduler {
    private final List<TaskEntry> tasks = new ArrayList<>();
    private long delta = 15_000_000_000L;
    private boolean initialised = false;

    public volatile int completed = 0;

    public void register(ScheduledTask task) {
        if (task.id() == null || task.id().isBlank()) throw new IllegalStateException("ID cannot be null or blank: " + task.getClass());
        if (initialised) throw new IllegalStateException("Cannot register task after initialisation" + task.id());
        if (task.duration() == null) throw new IllegalStateException("Duration cannot be null: " + task.id());
        tasks.add(new TaskEntry(task, System.nanoTime()));
        Main.LOGGER.info("Task registered: {} with duration: {} {}", task.id(), task.duration().period(), task.duration().timeUnit().toString());
    }

    public void tick() {
        long now = System.nanoTime();
        long min = Long.MAX_VALUE;

        for (TaskEntry entry : tasks) {
            if (now >= entry.nextExecution()) {
                completed++;
                entry.execute();
                entry.update();
                min = Math.min(min, entry.nextExecution());
            }
        }
        if (min == Long.MAX_VALUE) return;
        delta = Math.max(0, min - System.nanoTime());
        System.out.println(">>>" + delta);
    }

    public long delta() {
        return delta;
    }

    public void setInitialised() {
        initialised = true;
    }
}
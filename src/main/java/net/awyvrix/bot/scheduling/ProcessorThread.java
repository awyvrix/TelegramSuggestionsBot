package net.awyvrix.bot.scheduling;

import java.util.concurrent.locks.LockSupport;

import static net.awyvrix.bot.Main.LOGGER;

public final class ProcessorThread extends Thread {
    private volatile boolean running = false;
    private final TaskScheduler taskScheduler;

    public ProcessorThread(TaskScheduler taskScheduler) {
        super("Bot-Processor");
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void run() {
        while (running && !isInterrupted()) {
            try {
                taskScheduler.tick();
            } catch (RuntimeException e) {
                LOGGER.error("Task caused an exception: {}", String.valueOf(e));
            }

            long delta = taskScheduler.delta();
            if (delta == Long.MAX_VALUE) {
                LockSupport.park();
            } else if (delta > 0) {
                LockSupport.parkNanos(delta);
            }
        }
    }

    public void shutdown() {
        LOGGER.info("Stopping ProcessorThread...");
        running = false;
        interrupt();
    }

    @Override
    public synchronized void start() {
        LOGGER.info("Starting ProcessorThread...");
        running = true;
        super.start();
    }
}
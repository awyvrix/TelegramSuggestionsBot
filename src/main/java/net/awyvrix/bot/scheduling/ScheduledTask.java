package net.awyvrix.bot.scheduling;

public interface ScheduledTask {
    String id();
    Duration duration();
    void execute();
}

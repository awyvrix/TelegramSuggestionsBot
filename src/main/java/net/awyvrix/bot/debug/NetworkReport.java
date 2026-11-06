package net.awyvrix.bot.debug;

import java.time.LocalDateTime;

public record NetworkReport(LocalDateTime startTime, LocalDateTime endTime, long duration) {}

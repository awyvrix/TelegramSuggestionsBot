package net.awyvrix.bot.scheduling;

import java.util.concurrent.TimeUnit;

public record Duration(TimeUnit timeUnit, long period) {}

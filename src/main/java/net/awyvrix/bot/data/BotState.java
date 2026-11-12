package net.awyvrix.bot.data;

/** Serializes mutations and snapshots of the bot's persistent in-memory state */
public final class BotState {
    private BotState() {}
    public static final Object LOCK = new Object();
}

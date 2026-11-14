package net.awyvrix.bot.commands;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CommandManager {
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void register(Command command) {
        String id = command.id();
        if (id == null || id.isEmpty()) throw new IllegalStateException("Id cannot be blank or null");
        if (commands.containsKey(id)) throw new IllegalStateException("Id cannot be registered twice");
        commands.put(id, command);
    }

    public Map<String, Command> commands() {
        return commands;
    }
}
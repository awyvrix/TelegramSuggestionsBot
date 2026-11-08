package net.awyvrix.bot.commands;

import net.awyvrix.bot.Main;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CommandRegistry {
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private boolean initialised = false;

    public void register(Command command) {
        String id = command.id();
        if (id == null || id.isEmpty()) throw new IllegalStateException("Id cannot be blank or null");
        if (initialised) throw new IllegalStateException("Cannot register command after initialisation" + command.id());
        if (commands.containsKey(id)) throw new IllegalStateException("Id cannot be registered twice");
        commands.put(id, command);
        Main.LOGGER.info("Command registered: {}", command.id());
    }

    public Map<String, Command> commands() {
        return commands;
    }

    public void setInitialised() {
        initialised = true;
    }
}
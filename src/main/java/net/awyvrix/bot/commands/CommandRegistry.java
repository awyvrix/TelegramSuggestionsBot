package net.awyvrix.bot.commands;

import net.awyvrix.bot.Main;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class CommandRegistry {
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Set<String> temporalCommands = new HashSet<>();

    public void register(Command command) {
        String id = command.id();
        if (id == null || id.isEmpty()) throw new IllegalStateException("Id cannot be blank or null");
        if (commands.containsKey(id)) throw new IllegalStateException("Id cannot be registered twice");
        //if (isTemporal) temporalCommands.add(command.id());
        //if (!isTemporal) Main.LOGGER.info("Command registered: {}", command.id());
        commands.put(id, command);
    }

    public void clearTemp(Command command) {
        String id = command.id();
        if (temporalCommands.contains(id)) {
            temporalCommands.remove(id);
            commands.remove(id);
        }
    }

    public Map<String, Command> commands() {
        return commands;
    }
}
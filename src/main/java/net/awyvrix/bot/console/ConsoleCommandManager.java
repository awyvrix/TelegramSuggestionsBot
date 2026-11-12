package net.awyvrix.bot.console;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConsoleCommandManager {
    public final Map<String, ConsoleCommand> commands = new HashMap<>();

    public void register(ConsoleCommand command) {
        commands.put(command.name().toLowerCase(), command);
    }

    public void execute(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) return;

        ConsoleCommand command = commands.get(parts[0].toLowerCase());
        if (command == null) {
            System.out.println("Unknown command. Use 'help'.");
            return;
        }

        try {
            command.execute(Arrays.copyOfRange(parts, 1, parts.length));
        } catch (Exception e) {
            System.out.println("Command error: " + e.getMessage());
        }
    }
}
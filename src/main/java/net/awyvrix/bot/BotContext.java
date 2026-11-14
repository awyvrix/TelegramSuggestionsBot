package net.awyvrix.bot;

import net.awyvrix.bot.commands.CommandManager;
import net.awyvrix.bot.console.ConsoleCommandManager;
import net.awyvrix.bot.data.DataManager;
import net.awyvrix.bot.data.SaveManager;

public final class BotContext {
    private final CommandManager commandManager;
    private final ConsoleCommandManager consoleCommandManager;

    private final DataManager botDataManager;
    private final SaveManager saveManager;

    public BotContext() {
        this.commandManager = new CommandManager();
        this.consoleCommandManager = new ConsoleCommandManager();

        this.saveManager = new SaveManager();
        this.botDataManager = new DataManager(saveManager);
    }

    public CommandManager commandManager() {
        return commandManager;
    }

    public ConsoleCommandManager consoleCommandManager() {
        return consoleCommandManager;
    }

    public SaveManager saveManager() {
        return saveManager;
    }

    public DataManager botDataManager() {
        return botDataManager;
    }
}

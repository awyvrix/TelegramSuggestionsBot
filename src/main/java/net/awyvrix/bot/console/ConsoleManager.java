package net.awyvrix.bot.console;

import java.util.Scanner;

public class ConsoleManager {
    private final ConsoleCommandManager commandManager;
    private volatile boolean running = true;

    public ConsoleManager(ConsoleCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void start() {
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (running && scanner.hasNextLine()) {
                System.out.print("> ");
                commandManager.execute(scanner.nextLine());
            }
        }, "Console-Thread");

        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
    }
}
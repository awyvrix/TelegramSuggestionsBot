package net.awyvrix.bot.commands.custom.developer;

import net.awyvrix.bot.commands.Argument;
import net.awyvrix.bot.commands.ArgumentCommand;
import net.awyvrix.bot.commands.CommandManager;
import net.awyvrix.bot.commands.CommandType;
import net.awyvrix.bot.data.user.UserData;
import net.awyvrix.bot.data.user.UserTag;
import net.awyvrix.bot.debug.ErrorHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.awyvrix.bot.Util.executeRights;
import static net.awyvrix.bot.Util.sendMessage;

public final class Logs implements ArgumentCommand {
    private final Pattern DATE_PATTERN = Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}\\b");
    private final CommandManager commandManager;
    private final UserData data;
    private final TelegramClient client;

    public Logs(CommandManager commandManager, UserData data, TelegramClient client) {
        this.commandManager = commandManager;
        this.data = data;
        this.client = client;
    }

    @Override
    public String id() {
        return "/logs";
    }

    @Override
    public String description() {
        return "Получить логи";
    }

    @Override
    public Set<CommandType> types() {
        return Set.of(CommandType.DEVELOPER);
    }

    @Override
    public void execute(Update update) throws TelegramApiException {
        if (executeRights(client, data, update, UserTag.DEVELOPER, UserTag.ADMIN)) {
            sendMessage(client, update,"Please choose date: ");
            commandManager.addArgument(update.getMessage().getFrom().getId(), new Argument(this, 0, List.of()));
        }
    }

    @Override
    public void execute(Update update, Argument argument) throws TelegramApiException {
        if (executeRights(client, data, update, UserTag.DEVELOPER, UserTag.ADMIN)) {
            if (argument.stage() == 0) {
                if (argument.arguments().isEmpty()) return;
                String arg1 = argument.arguments().getFirst();

                if (arg1.equals("current") || arg1.equals("latest")) {
                    Path path = Path.of("/logs/current.log");
                    client.execute(SendDocument.builder()
                            .chatId(update.getMessage().getChatId())
                            .document(new InputFile(path.toFile()))
                            .build());
                    return;
                }
                LocalDate date = extractDate(arg1);

                if (date == null) {
                    sendMessage(client, update, "❌ Не удалось определить дату");
                    return;
                }
                try {
                    LogFilesInfo info = getLogFilesInfo(date);
                    if (info.files().isEmpty()) {
                        sendMessage(client, update, "⚠ Логи за " + date + " не найдены");
                        return;
                    }

                    sendMessage(client, update,"Please choose file:\n" + info.description());
                    commandManager.addArgument(update.getMessage().getFrom().getId(), argument.incrementStage().clearArguments(arg1));
                } catch (Exception e) {
                    ErrorHandler.report("Failed to get log file info", e,
                            "date", date,
                            "arg", arg1);
                }
            } if (argument.stage() == 1) {
                if (argument.arguments().size() < 2) return;
                LogSelection selection = parseLogSelection(argument.arguments().get(1));
                LocalDate date = extractDate(argument.arguments().getFirst());

                if (date == null) return;
                try {
                    LogFilesInfo info = getLogFilesInfo(date);

                    if (selection == null) {
                        sendMessage(client, update, "❌ Неверный диапазон");
                        return;
                    }
                    List<Path> files = info.getFiles(selection);
                    sendLogFiles(update, files);
                } catch (Exception e) {
                    ErrorHandler.report("Failed to get log file info", e, "date", date);
                }
            }
        }
    }

    private LocalDate extractDate(String message) {
        Matcher matcher = DATE_PATTERN.matcher(message);
        while (matcher.find()) {
            try {
                return LocalDate.parse(matcher.group());
            } catch (Exception e) {
                ErrorHandler.report("Failed to extract date", e, "msg", message);
            }
        }
        return null;
    }

    private LogFilesInfo getLogFilesInfo(LocalDate date) throws IOException {
        Path directory = Path.of("logs", date.toString());

        if (!Files.isDirectory(directory)) return new LogFilesInfo(Map.of());
        Pattern pattern = Pattern.compile(Pattern.quote(date + "-") + "(\\d+)\\.log");
        Map<Integer, Path> files = new TreeMap<>();

        try (Stream<Path> stream = Files.list(directory)) {
            for (Path path : stream.toList()) {
                if (!Files.isRegularFile(path)) continue;
                Matcher matcher = pattern.matcher(path.getFileName().toString());

                if (!matcher.matches()) continue;
                try {
                    files.put(Integer.parseInt(matcher.group(1)), path);
                } catch (NumberFormatException ignored) {}
            }
        }

        return new LogFilesInfo(files);
    }

    private LogSelection parseLogSelection(String input) {
        Matcher matcher = Pattern.compile("^(\\d+)(?:\\s*-\\s*(\\d+))?$").matcher(input.trim());

        if (!matcher.matches()) return null;
        int from = Integer.parseInt(matcher.group(1));
        int to = matcher.group(2) == null ? from : Integer.parseInt(matcher.group(2));
        return from <= to ? new LogSelection(from, to) : null;
    }

    private record LogFilesInfo(Map<Integer, Path> files) {
        public String description() {
            if (files.isEmpty()) return "No log files found.";
            int min = Collections.min(files.keySet());
            int max = Collections.max(files.keySet());

            return "Files: " + files.size() + "\nRange: " + min + "-" + max;
        }

        public List<Path> getFiles(LogSelection selection) {
            List<Path> result = new ArrayList<>();

            for (int i = selection.from(); i <= selection.to(); i++) {
                Path path = files.get(i);

                if (path == null) return List.of();
                result.add(path);
            }

            return result;
        }
    }

    private void sendLogFiles(Update update, List<Path> files) throws TelegramApiException {
        for (Path path : files) {
            client.execute(SendDocument.builder()
                    .chatId(update.getMessage().getChatId())
                    .document(new InputFile(path.toFile()))
                    .build());
        }
    }

    private record LogSelection(int from, int to) {}
}
package net.awyvrix.bot.commands;

import java.util.ArrayList;
import java.util.List;

// 0 = awaiting data
// 1 = sent data
public record Argument(ArgumentCommand command, int stage, List<String> arguments) {
    public Argument incrementStage() {
        return new Argument(command, stage+1, arguments);
    }

    public Argument clearArguments(String... args) {
        return new Argument(command, stage, List.of(args));
    }

    public Argument addAll(List<String> strings) {
        List<String> s = new ArrayList<>(arguments);
        s.addAll(strings);
        return new Argument(command, stage, s);
    }
}
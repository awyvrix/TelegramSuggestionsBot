package net.awyvrix.bot.data.user;

import net.awyvrix.bot.commands.CommandType;

public enum UserTag {
    ANONYMOUS(),
    ADMIN(CommandType.ADMIN),
    DEVELOPER(CommandType.DEVELOPER);

    final CommandType type;
    UserTag(CommandType type) {
        this.type = type;
    }

    UserTag() {
        this.type = null;
    }

    public CommandType allowedType() {
        return type;
    }
}
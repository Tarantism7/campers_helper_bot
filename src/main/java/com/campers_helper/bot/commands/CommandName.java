package com.campers_helper.bot.commands;

import lombok.Getter;

@Getter
public enum CommandName {
    ABOUT("ABOUT_COMMAND"),
    LANGUAGE("LANGUAGE_COMMAND"),
    START("START_COMMAND"),
    CONVERT("CONVERT_COMMAND");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }
}

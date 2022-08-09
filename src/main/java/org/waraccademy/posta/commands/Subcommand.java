package org.waraccademy.posta.commands;

import org.bukkit.command.CommandSender;

public interface Subcommand {
    boolean onCommand(CommandSender sender, String[] args);

    String getName();

    String getUsage();

    default boolean isAllowedConsole() {
        return true;
    }

    default String getPermission() {
        return "";
    }
}

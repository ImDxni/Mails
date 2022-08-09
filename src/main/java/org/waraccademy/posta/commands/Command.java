package org.waraccademy.posta.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public abstract class Command implements CommandExecutor {

    private final String label;

    public Command(JavaPlugin plugin, String label) {
        this.label = label;
        Objects.requireNonNull(plugin.getCommand(label)).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] strings) {
        exec(sender, label, strings);
        return true;
    }

    protected abstract void exec(CommandSender sender, String label, String[] args);

    public String getLabel() {
        return label;
    }
}

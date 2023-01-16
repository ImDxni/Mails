package org.waraccademy.posta.commands.complex;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.waraccademy.posta.commands.Command;
import org.waraccademy.posta.commands.Subcommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.waraccademy.posta.utils.Utils.color;

public abstract class ComplexCommand extends Command implements TabExecutor {
    private final Map<String, Subcommand> commands = new HashMap<>();

    public ComplexCommand(JavaPlugin plugin, String name, Subcommand... subcommands) {
        super(plugin, name);
        for (Subcommand subcommand : subcommands)
            commands.put(subcommand.getName(), subcommand);
    }

    protected void exec(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            noArgs(sender);
            return;
        }

        Subcommand sub = commands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(color("&c&lERRORE &7Comando sconosciuto"));
            return;
        }

        if (!sub.isAllowedConsole() && !(sender instanceof Player)) {
            sender.sendMessage(color("&cEsegui questo comando in gioco!"));
            return;
        }

        if (!sub.getPermission().equals("") && !sender.hasPermission(sub.getPermission()) && !sender.hasPermission(getLabel() + ".*")) {
            sender.sendMessage(color("&c&lERRORE &7Non possiedi il &fpermesso &7adeguato!"));
            return;
        }

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);

        if (!sub.onCommand(sender, subArgs))
            sender.sendMessage(color("&c&lERRORE &7Utilizzo scorretto! Usa: &f/" + getLabel() + " " + sub.getUsage()));
    }

    public abstract void noArgs(CommandSender sender);

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (args.length < 2) return commands.keySet().stream().filter(cmd -> cmd.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
    }
}

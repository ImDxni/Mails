package org.waraccademy.posta.commands.impl.packs;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.waraccademy.posta.commands.complex.ComplexCommand;
import org.waraccademy.posta.commands.impl.packs.subcommands.CreateSubcommand;
import org.waraccademy.posta.commands.impl.packs.subcommands.SignSubcommand;
import org.waraccademy.posta.commands.impl.packs.subcommands.TrackSubcommand;
import org.waraccademy.posta.commands.impl.packs.subcommands.UpdateSubcommand;

public class PackCommand extends ComplexCommand {

    public PackCommand(JavaPlugin plugin) {
        super(plugin, "pacco", new CreateSubcommand(),
                new SignSubcommand(),
                new TrackSubcommand(),
                new UpdateSubcommand());
    }

    @Override
    public void noArgs(CommandSender sender) {

    }
}

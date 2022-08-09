package org.waraccademy.posta.commands.impl.mailboxes;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.waraccademy.posta.commands.complex.ComplexCommand;
import org.waraccademy.posta.commands.impl.mailboxes.subcommands.SearchSubcommand;
import org.waraccademy.posta.commands.impl.mailboxes.subcommands.SpecialCreateSubcommand;
import org.waraccademy.posta.commands.impl.packs.subcommands.CreateSubcommand;

public class MailboxCommand extends ComplexCommand {

    public MailboxCommand(JavaPlugin plugin) {
        super(plugin, "cassetta", new SearchSubcommand(),
                new CreateSubcommand(),
                new SpecialCreateSubcommand());
    }

    @Override
    public void noArgs(CommandSender sender) {

    }
}

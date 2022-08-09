package org.waraccademy.posta.commands.impl.mailboxes.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.commands.Subcommand;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;
import org.waraccademy.posta.utils.Triple;

import java.util.List;

import static org.waraccademy.posta.utils.Utils.color;

public class SearchSubcommand implements Subcommand {
    private final MailboxService service = Posta.getInstance().getMailboxService();
    private final YamlConfiguration config = Posta.getInstance().getConfig();
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;

        Player p = (Player) sender;

        String owner = args[0];
        List<Triple<Integer>> mailboxList = service.getLocations(owner);


        if(mailboxList.isEmpty()){
            p.sendMessage(color(config.getString("messages.no-mailboxes")));
            return true;
        }

        StringBuilder builder = new StringBuilder(color(config.getString("messages.mailbox.list.prefix").replace("%target%",owner)));

        for (Triple<Integer> loc : mailboxList) {
            builder.append(color(config.getString("messages.mailbox.list.element")
                    .replace("%target%",owner)
                    .replace("%location%", loc.getFirst() + " " + loc.getSecond() + " " + loc.getThird())));
        }

        p.sendMessage(builder.toString());
        return true;
    }

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getUsage() {
        return "search <proprietario>";
    }

    @Override
    public boolean isAllowedConsole() {
        return false;
    }

    @Override
    public String getPermission() {
        return "metropolis.posta.cassettasearch";
    }
}

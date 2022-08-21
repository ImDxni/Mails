package org.waraccademy.posta.commands.impl.mailboxes.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.commands.Subcommand;
import org.waraccademy.posta.database.sql.MySQLManager;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;
import java.util.Optional;

import static org.waraccademy.posta.utils.Utils.color;

public class SpecialCreateSubcommand implements Subcommand {
    private final MailboxService service = Posta.getInstance().getMailboxService();
    private final MySQLManager manager = Posta.getInstance().getSqlManager();
    private final YamlConfiguration config = Posta.getInstance().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length < 2) return false;

        Player p = (Player) sender;
        String target = args[0];

        manager.ownerExists(target).whenComplete((exists,error) -> {
            if(error != null){
                error.printStackTrace();
                return;
            }

            if(!exists) {
                manager.insertOwner(target);
            }

            String color = args[1].toLowerCase();

            Optional<Integer> optionalID = service.getItemID(color);

            if(optionalID.isPresent()){
                int id = optionalID.get();

                p.getInventory().addItem(service.getItemStack(id,target,true));
            } else {
                p.sendMessage(color(config.getString("messages.color-not-found")));
            }
        });

        return true;
    }

    @Override
    public String getName() {
        return "creaazienda";
    }

    @Override
    public String getUsage() {
        return "creaazienda <nome> <colore>";
    }

    @Override
    public boolean isAllowedConsole() {
        return false;
    }

    @Override
    public String getPermission() {
        return "metropolis.posta.cassettacrea.azienda";
    }
}

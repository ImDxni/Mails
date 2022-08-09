package org.waraccademy.posta.commands.impl.mailboxes.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.waraccademy.libs.nbtapi.NBTItem;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.commands.Subcommand;
import org.waraccademy.posta.database.sql.MySQLManager;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;
import org.waraccademy.posta.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.waraccademy.posta.utils.Utils.color;

public class CreateSubcommand implements Subcommand {
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
                p.sendMessage(color(config.getString("messages.player-not-found")));
                return;
            }

            String color = args[1].toLowerCase();

            Optional<Integer> optionalID = service.getItemID(color);

            if(optionalID.isPresent()){
                int id = optionalID.get();

                p.getInventory().addItem(service.getItemStack(id,target,false));
            } else {
                p.sendMessage(color(config.getString("messages.color-not-found")));
            }
        });

        return true;
    }


    @Override
    public String getName() {
        return "crea";
    }

    @Override
    public String getUsage() {
        return "crea <nome> <colore>";
    }

    @Override
    public boolean isAllowedConsole() {
        return Subcommand.super.isAllowedConsole();
    }

    @Override
    public String getPermission() {
        return Subcommand.super.getPermission();
    }
}

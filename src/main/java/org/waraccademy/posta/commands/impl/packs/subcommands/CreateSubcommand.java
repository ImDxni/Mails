package org.waraccademy.posta.commands.impl.packs.subcommands;

import me.lolok.containers.ContainersPlugin;
import me.lolok.containers.containers.types.ContainerType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.commands.Subcommand;

import static org.waraccademy.posta.utils.Utils.color;

public class CreateSubcommand implements Subcommand {
    private final YamlConfiguration config = Posta.getInstance().getConfig();
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;

        Player p = (Player) sender;

        String type = "pacco-"+args[0];

        ContainerType containerType = ContainersPlugin.getInstance().getContainersTypeManager().getType(type);

        if(containerType == null)
            return false;

        if(p.getInventory().firstEmpty() != -1){
            p.getInventory().addItem(containerType.getItem(-1));
            p.sendMessage(color(config.getString("messages.package-created")));
        }

        return true;
    }

    @Override
    public String getName() {
        return "crea";
    }

    @Override
    public String getUsage() {
        return "crea (9/18/27/36)";
    }

    @Override
    public String getPermission() {
        return "metropolis.posta.paccocrea";
    }

    @Override
    public boolean isAllowedConsole() {
        return false;
    }
}

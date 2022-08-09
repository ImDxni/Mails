package org.waraccademy.posta.commands.impl.packs.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.commands.Subcommand;
import org.waraccademy.posta.services.impl.packages.Package;
import org.waraccademy.posta.services.impl.packages.PackageService;

import java.util.List;
import java.util.Locale;

import static org.waraccademy.posta.utils.Utils.color;
public class ListSubcommand implements Subcommand {
    private final PackageService service = Posta.getInstance().getPackageService();
    private final YamlConfiguration config = Posta.getInstance().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;

        Player p = (Player) sender;
        String target = args[1];

        List<Package> packages = service.getPackages(target);
        if(packages.isEmpty()){
            p.sendMessage(color(config.getString("messages.no-packages")));
            return true;
        }

        StringBuilder builder = new StringBuilder(color(config.getString("messages.pack.list.prefix").replace("%target%",target)));

        for (Package pack : packages) {
            builder.append(color(config.getString("messages.pack.list.element")
                    .replace("%id%",String.valueOf(pack.getId()))
                    .replace("%sender%",pack.getSender())
                    .replace("%status%",config.getString("messages.pack.list.status."+pack.getStatus().name().toLowerCase(Locale.ROOT)))));
        }

        p.sendMessage(builder.toString());

        return false;
    }

    @Override
    public String getName() {
        return "lista";
    }

    @Override
    public String getUsage() {
        return "lista <destinatario>";
    }

    @Override
    public boolean isAllowedConsole() {
        return false;
    }

    @Override
    public String getPermission() {
        return "metropolis.pacco.lista";
    }
}

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

public class TrackSubcommand implements Subcommand {

    private final PackageService service = Posta.getInstance().getPackageService();
    private final YamlConfiguration config = Posta.getInstance().getConfig();
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;

        Player p = (Player) sender;
        String target = args[0];

        List<Package> packages = service.getPackages(target,p.getName());

        if(packages.isEmpty()){
            p.sendMessage(color(config.getString("messages.no-packages")));
            return true;
        }

        StringBuilder builder = new StringBuilder(color(config.getString("messages.pack.track.prefix").replace("%target%",target)));

        for (Package pack : packages) {
            builder.append(color(config.getString("messages.pack.track.element")
                    .replace("%id%",String.valueOf(pack.getId()))
                    .replace("%status%",config.getString("messages.pack.track.status."+pack.getStatus().name().toLowerCase(Locale.ROOT)))));
        }

        p.sendMessage(builder.toString());
        return true;
    }

    @Override
    public String getName() {
        return "traccia";
    }

    @Override
    public String getUsage() {
        return "traccia <destinatario>";
    }

    @Override
    public boolean isAllowedConsole() {
        return false;
    }
}

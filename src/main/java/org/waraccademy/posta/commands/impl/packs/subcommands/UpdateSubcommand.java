package org.waraccademy.posta.commands.impl.packs.subcommands;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.commands.Subcommand;
import org.waraccademy.posta.services.impl.packages.Package;
import org.waraccademy.posta.services.impl.packages.PackageService;

import java.util.Locale;

import static org.waraccademy.posta.utils.Utils.color;

public class UpdateSubcommand implements Subcommand {
    private final PackageService service = Posta.getInstance().getPackageService();
    private final YamlConfiguration config = Posta.getInstance().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length < 2) return false;

        if(!NumberUtils.isNumber(args[0]))
            return false;


        int id = Integer.parseInt(args[0]);
        Package.STATUS status;

        switch(args[1].toLowerCase(Locale.ROOT)){
            case "fermoposta":
                status = Package.STATUS.STOP;
                break;
            case "consegna":
                status = Package.STATUS.DELIVERING;
                break;
            case "consegnato":
                status = Package.STATUS.DELIVERED;
                break;
            default:
                return false;
        }

        service.updateStatus(id,status);
        sender.sendMessage(color(config.getString("messages.package-updated")));


        return true;
    }

    @Override
    public String getName() {
        return "aggiorna";
    }

    @Override
    public String getUsage() {
        return "aggiorna <id> <fermoposta/consegna/consegnato>";
    }

    @Override
    public boolean isAllowedConsole() {
        return true;
    }

    @Override
    public String getPermission() {
        return "metropolis.pacco.aggiorna";
    }
}

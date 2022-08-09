package org.waraccademy.posta.commands.impl.packs.subcommands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.waraccademy.libs.nbtapi.NBTItem;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.commands.Subcommand;
import org.waraccademy.posta.database.sql.MySQLManager;
import org.waraccademy.posta.services.impl.packages.PackageService;

import java.util.List;
import java.util.stream.Collectors;

import static org.waraccademy.posta.utils.Utils.color;

public class SignSubcommand implements Subcommand {

    private final PackageService service = Posta.getInstance().getPackageService();
    private final YamlConfiguration config = Posta.getInstance().getConfig();
    private final MySQLManager sqlManager = Posta.getInstance().getSqlManager();
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length < 2) return false;

        Player p = (Player) sender;

        String packSender = args[0];
        String packTarget = args[1];


        sqlManager.ownerExists(packSender).thenCombine(sqlManager.ownerExists(packTarget),(s,t) -> {
            if(!s)
                p.sendMessage(color(config.getString("messages.no-sender")));
            if(!t)
                p.sendMessage(color(config.getString("messages.no-target")));

            return s && t;
        }).whenComplete((result,error) -> {
            if(error != null){
                error.printStackTrace();
                return;
            }

            if(!result)
                return;

            ItemStack item = p.getInventory().getItemInMainHand();
            if(item == null || item.getType() == Material.AIR)
                return;

            NBTItem nbtItem = new NBTItem(item);
            if(nbtItem.hasKey("Signed")) {
                service.createPackage(packSender, packTarget).whenComplete((id, throwable) -> {
                    if(throwable != null){
                        throwable.printStackTrace();
                        return;
                    }

                    nbtItem.setBoolean("Signed",true);
                    ItemStack packItem = nbtItem.getItem();
                    String name = color(config.getString("pack.name").replace("%id%",String.valueOf(id)));
                    List<String> lore = config.getStringList("pack.lore")
                            .stream()
                            .map(s -> color(s.replace("%sender%", packSender)
                                    .replace("%target%", packTarget)
                                    .replace("%id%",String.valueOf(id))))
                            .collect(Collectors.toList());

                    ItemMeta meta = packItem.getItemMeta();
                    meta.setLore(lore);
                    meta.setDisplayName(name);
                    packItem.setItemMeta(meta);

                    p.getInventory().setItemInMainHand(packItem);
                    p.sendMessage(color(config.getString("messages.pack-signed")));
                });

            }
        });

        return true;
    }

    @Override
    public String getName() {
        return "firma";
    }

    @Override
    public String getUsage() {
        return "firma <mittente> <destinatario>";
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

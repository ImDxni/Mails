package org.waraccademy.posta.listeners.packages;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.waraccademy.libs.nbtapi.NBTItem;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.services.impl.mailboxes.Mailbox;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;
import org.waraccademy.posta.services.impl.packages.PackageService;

import java.util.Optional;

public class InteractListener implements Listener {
    private final YamlConfiguration config = Posta.getInstance().getConfig();
    private final MailboxService mailboxService = Posta.getInstance().getMailboxService();

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getHand() == EquipmentSlot.HAND && e.getAction() == Action.RIGHT_CLICK_BLOCK){
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
            NBTItem nbtItem = new NBTItem(item);
            if(nbtItem.hasKey("Container") && nbtItem.hasKey("Signed") && nbtItem.getBoolean("Signed")){
                int packID = nbtItem.getInteger("Container");

                Location clickedLoc = e.getClickedBlock().getLocation();

                Optional<Mailbox> optional = mailboxService.getMailbox(clickedLoc);

                if(optional.isPresent()){
                    Mailbox box = optional.get();

                    int boxID = box.getId();

                    e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));

                    mailboxService.insertItems(boxID,packID);

                    box.setPackages(true);
                }
            }
        }
    }
}

package org.waraccademy.posta.listeners.packages;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.services.impl.mailboxes.Mailbox;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;
import org.waraccademy.posta.utils.Utils;

import java.util.Locale;
import java.util.Optional;

import static org.waraccademy.posta.utils.Utils.color;


public class InteractListener implements Listener {
    private final YamlConfiguration config = Posta.getInstance().getConfig();
    private final MailboxService mailboxService = Posta.getInstance().getMailboxService();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.HAND && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location clickedLoc = e.getClickedBlock().getLocation();
            Player player = e.getPlayer();

            Optional<Mailbox> optional = mailboxService.getMailbox(clickedLoc);


            if (optional.isPresent()) {
                Mailbox box = optional.get();

                ItemStack item = player.getInventory().getItemInMainHand();
                if(item == null || item.getType() == Material.AIR){
                    item = new ItemStack(Material.STONE); //brutta roba per bypassare nbtapi
                }


                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.hasKey("Container") && nbtItem.hasKey("Signed") && nbtItem.getBoolean("Signed")) {
                    if(nbtItem.getString("packTarget").equalsIgnoreCase(box.getOwner())) {
                        int boxID = box.getId();

                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

                        mailboxService.insertItems(boxID, item);

                        box.setPackages(true);

                        String message = color(config.getString("messages.packages-available")
                                .replace("%x%",String.valueOf(clickedLoc.getBlockX()))
                                .replace("%y%",String.valueOf(clickedLoc.getBlockY()))
                                .replace("%z%",String.valueOf(clickedLoc.getBlockZ())));

                        if(box.isLocked()) {
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                if(onlinePlayer.hasPermission("metropolis.posta.owner."+ box.getOwner().toLowerCase(Locale.ROOT))){
                                    onlinePlayer.sendMessage(message);
                                }
                            }
                            return;
                        }

                        Player target = Bukkit.getPlayer(box.getOwner());
                        if(target == null || !target.isOnline()) return;

                        target.sendMessage(message);

                    } else {
                        player.sendMessage(color(config.getString("messages.wrong-mailbox")));
                    }
                } else {
                    String owner = box.getOwner();
                    if(owner.equalsIgnoreCase(player.getName()) || player.hasPermission("metropolis.posta.owner."+owner.toLowerCase(Locale.ROOT))){
                        if(box.hasPackages()){
                            mailboxService.getItems(box.getId()).whenComplete((items,error) -> {
                                if(error != null){
                                    error.printStackTrace();
                                    return;
                                }

                                int size = items.size();

                                int slots = Utils.getEmptySlots(player.getInventory());

                                if(size > slots){
                                    player.sendMessage(color(config.getString("messages.no-space")));
                                    return;
                                }

                                player.getInventory().addItem(items.toArray(new ItemStack[0]));
                                mailboxService.clearItems(box.getId());

                                box.setPackages(false);
                            });
                        }
                    }
                }
            }
        }
    }
}

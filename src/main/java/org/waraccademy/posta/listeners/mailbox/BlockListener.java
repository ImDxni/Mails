package org.waraccademy.posta.listeners.mailbox;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.services.impl.mailboxes.Mailbox;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;

import java.util.Locale;
import java.util.Optional;

import static org.waraccademy.posta.utils.Utils.color;

public class BlockListener implements Listener {
    private final MailboxService service = Posta.getInstance().getMailboxService();
    private final YamlConfiguration config = Posta.getInstance().getConfig();

    @EventHandler(ignoreCancelled = true,priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e){
        ItemStack item = e.getItemInHand();

        NBTItem nbtItem = new NBTItem(item);

        if(nbtItem.hasKey("Mailbox")){
            String owner = nbtItem.getString("MailboxOwner");
            boolean locked = nbtItem.getBoolean("Mailbox");
            int itemID = nbtItem.getInteger("itemID");

            service.createMailbox(e.getBlock().getLocation(),owner,locked,itemID);

            e.getPlayer().sendMessage(color(config.getString("messages.mailbox-placed")));
        }
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e){
        Location loc = e.getBlock().getLocation();

        Optional<Mailbox> mailboxOptional = service.getMailbox(loc);
        if(mailboxOptional.isPresent()){
            Player p = e.getPlayer();
            Mailbox box = mailboxOptional.get();

            String owner = box.getOwner();
            if(owner.equalsIgnoreCase(p.getName()) || p.hasPermission("metropolis.posta.owner."+owner.toLowerCase(Locale.ROOT))){
                if(p.isSneaking()){
                    if(box.hasPackages()){
                        p.sendMessage(color(config.getString("messages.has-packages")));
                        e.setCancelled(true);
                        return;
                    }

                    e.getBlock().setType(Material.AIR);
                    boolean locked = box.isLocked();

                    loc.getWorld().dropItem(loc,service.getItemStack(box.getItem(),owner,locked));

                    service.deleteMailbox(loc);

                    e.getPlayer().sendMessage(color(config.getString("messages.mailbox-removed")));
                }
            }

            e.setCancelled(true);
        }
    }
}

package org.waraccademy.posta.listeners.packages;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.lolok.containers.containers.objects.Container;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.container.container.PackageContainer;
import org.waraccademy.posta.services.impl.mailboxes.Mailbox;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;

import java.util.Optional;

public class OpenListener implements Listener {
    private final MailboxService mailboxService = Posta.getInstance().getMailboxService();

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent e){
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!e.getAction().name().startsWith("RIGHT_CLICK")) return;

        if(item == null || item.getType() == Material.AIR) return;

        if(e.getClickedBlock() != null) {
            Location clickedLoc = e.getClickedBlock().getLocation();
            Optional<Mailbox> optional = mailboxService.getMailbox(clickedLoc);

            if (optional.isPresent()) return;
        }

        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasKey("Container")) return;

        if(!nbtItem.hasKey("Signed")) return;


        e.setCancelled(true);
        int id = nbtItem.getInteger("Container");
        Container container = new PackageContainer(id);
        container.open(player);
    }
}

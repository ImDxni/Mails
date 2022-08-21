package org.waraccademy.posta.listeners.packages;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.lolok.containers.containers.objects.Container;
import me.lolok.containers.containers.objects.inventory.ContainerInventoryHolder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.waraccademy.posta.container.container.PackageContainer;
import org.waraccademy.posta.database.mongo.MongoDBManager;
import org.waraccademy.posta.utils.Utils;

public class InventoryListener implements Listener {
    private final MongoDBManager manager;

    public InventoryListener(MongoDBManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent e){
        Inventory inventory = e.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if(holder instanceof ContainerInventoryHolder){

            ContainerInventoryHolder inventoryHolder = (ContainerInventoryHolder) holder;
            Container container = inventoryHolder.getContainer();

            if(container instanceof PackageContainer){
                ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                if(item == null || item.getType() == Material.AIR)
                    return;
                NBTItem nbtItem = new NBTItem(item);
                if(nbtItem.hasKey("Signed") && nbtItem.getBoolean("Signed")) {
                    if (Utils.getEmptySlots(inventory) == inventory.getSize()) {
                        e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        manager.deletePackage(container.getId());
                    }
                }
            }
        }
    }
}

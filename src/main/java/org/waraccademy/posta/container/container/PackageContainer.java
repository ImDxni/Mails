package org.waraccademy.posta.container.container;

import me.lolok.containers.containers.objects.DefaultContainer;
import me.lolok.containers.containers.types.ContainerType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.waraccademy.libs.nbtapi.NBTItem;

public class PackageContainer extends DefaultContainer {

    public PackageContainer(int id, ContainerType type) {
        super(id, type);
    }

    @Override
    public void open(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(item);

        if(nbtItem.getBoolean("Signed"))
            return;

        super.open(player);
    }
}

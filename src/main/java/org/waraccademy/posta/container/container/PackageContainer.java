package org.waraccademy.posta.container.container;

import me.lolok.containers.containers.objects.DefaultContainer;
import me.lolok.containers.containers.types.ContainerType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.tr7zw.changeme.nbtapi.NBTItem;

import java.util.Locale;

public class PackageContainer extends DefaultContainer {

    public PackageContainer(int id, ContainerType type) {
        super(id, type);
    }

    public PackageContainer(int id) {
        super(id);
    }

    @Override
    public void open(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(item);

		String target = nbtItem.hasKey("packTarget") ? nbtItem.getString("packTarget") : "";
		
        if(nbtItem.getBoolean("Signed") && !(target.equals(player.getName()) || player.hasPermission("metropolis.posta.owner."+target.toLowerCase(Locale.ROOT))))
            return;

        super.open(player);
    }
}

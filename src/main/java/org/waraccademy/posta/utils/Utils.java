package org.waraccademy.posta.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utils {

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    public static int getEmptySlots(Inventory inventory){
        int count = 0;
        for (ItemStack itemStack : inventory.getStorageContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR)
                count++;
        }

        return count;
    }
}

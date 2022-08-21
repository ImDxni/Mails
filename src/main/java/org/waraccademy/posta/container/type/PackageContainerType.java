package org.waraccademy.posta.container.type;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.lolok.containers.ContainersPlugin;
import me.lolok.containers.containers.ContainersManager;
import me.lolok.containers.containers.types.ContainerType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.waraccademy.items.items.builder.ItemBuilder;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.container.container.PackageContainer;

public class PackageContainerType implements ContainerType {
    private final String id;
    private final int data;
    private final Material material;
    private final String name;
    private final String title;
    private final int size;

    public PackageContainerType(String id, int data, Material material, String name, String title, int size) {
        this.id = id;
        this.data = data;
        this.material = material;
        this.name = name;
        this.title = title;
        this.size = size;
    }

    @Override
    public ItemStack getItem(int i) {
        ItemStack item;
        if(material == Material.SKULL){
            item = Posta.getInstance().getHeadAPI().getItemHead(String.valueOf(data));
        } else {
            item = ItemBuilder.create()
                    .material(material)
                    .data(data).build();
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        item.setItemMeta(meta);

        int container = i >= 0 ? i : ContainersPlugin.getInstance().getContainersManager().getCount() + 1;
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger("Container", container);
        nbtItem.setString("ContainerType", id);
        nbtItem.setBoolean("Signed",false);

        item = nbtItem.getItem();
        if (i < 0) {
            create(container);
        }
        return item;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Object getSize() {
        return size;
    }

    @Override
    public boolean isContainersAllowed() {
        return false;
    }

    private void create(int id){
        PackageContainer container = new PackageContainer(id,this);

        ContainersManager manager = (ContainersManager) ContainersPlugin.getInstance().getContainersManager();
        manager.save(container,null);
        manager.setCount(manager.getCount()+1);
    }
}

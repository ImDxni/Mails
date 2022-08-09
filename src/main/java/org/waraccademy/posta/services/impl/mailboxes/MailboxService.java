package org.waraccademy.posta.services.impl.mailboxes;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.waraccademy.libs.nbtapi.NBTItem;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.database.mongo.MongoDBManager;
import org.waraccademy.posta.database.sql.MySQLManager;
import org.waraccademy.posta.services.Service;
import org.waraccademy.posta.utils.Triple;
import org.waraccademy.posta.utils.Utils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.waraccademy.posta.utils.Utils.color;

public class MailboxService implements Service {
    private final Map<Triple<Integer>,Mailbox> mailboxes = new HashMap<>();
    
    private final Map<String,Integer> items = new HashMap<>();
    private final MySQLManager sqlManager;
    private final MongoDBManager mongoDBManager;
    private final YamlConfiguration config = Posta.getInstance().getConfig();

    public MailboxService(MySQLManager sqlManager, MongoDBManager mongoDBManager) {
        this.sqlManager = sqlManager;
        this.mongoDBManager = mongoDBManager;
    }

    @Override
    public void onLoad(Posta plugin) {
        sqlManager.getAllMailboxes((resultSet -> {
            try {
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                int z = resultSet.getInt("z");

                Triple<Integer> loc = Triple.of(x,y,z);

                int id = resultSet.getInt("id");
                boolean packages = resultSet.getBoolean("packages");
                boolean locked = resultSet.getBoolean("private");

                String owner = resultSet.getString("name");
                Mailbox mailbox = new Mailbox(owner,locked);
                mailbox.setPackages(packages);
                mailbox.setId(id);

                mailboxes.put(loc,mailbox);

                System.out.println("§2Caricata Mailbox a: " + loc);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }));

        ConfigurationSection section = config.getConfigurationSection("mailboxes");

        for (String key : section.getKeys(false)) {
            int id = section.getInt(key+".id");

            items.put(key, id);
        }
    }
    
    public Optional<Integer> getItemID(String key){
        return Optional.ofNullable(items.get(key));
    }


    public void createMailbox(Location location, String owner, boolean locked){
        Mailbox mailbox = new Mailbox(owner,locked);

        sqlManager.insertMailbox(location,owner).whenComplete((id,error) -> {
            if(error != null){
                error.printStackTrace();
                return;
            }

            mailbox.setId(id);
            mongoDBManager.createMailbox(id);
        });

        mailboxes.put(Triple.of(location.getBlockX(), location.getBlockY(), location.getBlockZ()),mailbox);

    }


    public void deleteMailbox(Location location){
        Triple<Integer> loc = Triple.of(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        Mailbox box = mailboxes.remove(loc);

        sqlManager.deleteMailbox(location);

        if(box != null)
            mongoDBManager.deleteMailbox(box.getId());
    }

    public Optional<Mailbox> getMailbox(Location loc){
        Triple<Integer> triple = Triple.of(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());

        return Optional.ofNullable(mailboxes.get(triple));
    }

    public CompletableFuture<List<ItemStack>> getItems(int id){
        return mongoDBManager.getMailboxItems(id);
    }

    public void insertItems(int id, int packageID) {
        getItems(id).thenCombine(mongoDBManager.getPackageItems(packageID),
                (mailbox, pack) -> {
                    List<ItemStack> result = new ArrayList<>(mailbox);

                    result.addAll(pack);

                    return result;
                }).thenAccept((result) -> mongoDBManager.saveMailbox(id, result));
    }

    public void clearItems(int id){
        mongoDBManager.saveMailbox(id,Collections.emptyList());
    }

    public List<Triple<Integer>> getLocations(String owner){
        List<Triple<Integer>> list = new ArrayList<>();

        for (Map.Entry<Triple<Integer>, Mailbox> entry : mailboxes.entrySet()) {
            if(entry.getValue().getOwner().equals(owner))
                list.add(entry.getKey());
        }

        return list;
    }

    public List<Mailbox> getMailboxes(String owner){
        List<Mailbox> list = new ArrayList<>();

        for (Map.Entry<Triple<Integer>, Mailbox> entry : mailboxes.entrySet()) {
            if(entry.getValue().getOwner().equals(owner))
                list.add(entry.getValue());
        }

        return list;
    }

    @NotNull
    public ItemStack getItemStack(int id, String target, boolean locked) {
        ItemStack item = Posta.getInstance().getHeadAPI().getItemHead(String.valueOf(id));

        String name = color(config.getString("mailbox.name").replace("%owner%", target));
        List<String> lore = config.getStringList("mailbox.lore")
                .stream()
                .map(Utils::color)
                .collect(Collectors.toList());

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);

        nbtItem.setBoolean("Mailbox",locked);
        nbtItem.setString("MailboxOwner", target);
        return nbtItem.getItem();
    }

}

package org.waraccademy.posta.services.impl.packages;

import me.lolok.containers.ContainersPlugin;
import me.lolok.containers.containers.types.ContainersTypeManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryType;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.container.type.PackageContainerType;
import org.waraccademy.posta.database.mongo.MongoDBManager;
import org.waraccademy.posta.database.sql.MySQLManager;
import org.waraccademy.posta.services.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PackageService implements Service {
    private final List<Package> packages = new ArrayList<>();

    private final MySQLManager sqlManager;

    public PackageService(MySQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    @Override
    public void onLoad(Posta plugin) {
        sqlManager.checkTTL().thenRun(() -> sqlManager.getAllPackages().whenComplete((result,error) -> {
            if(error != null){
                error.printStackTrace();
                return;
            }

            packages.addAll(result);
        }));

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("packages");

        for(String key : section.getKeys(false)){
            int data = section.getInt(key + ".data", 0);
            Material material = Material.getMaterial(section.getString(key + ".material").toUpperCase());
            String name = ChatColor.translateAlternateColorCodes('&', section.getString(key + ".name"));
            String title = ChatColor.translateAlternateColorCodes('&', section.getString(key + ".title"));
            int size = section.getInt(key + ".size");

            ContainersTypeManager manager = (ContainersTypeManager) ContainersPlugin.getInstance().getContainersTypeManager();
            manager.addType(new PackageContainerType(key,data,material,name,title,size));
        }
    }

    public CompletableFuture<Integer> createPackage(String sender, String target){
        Package pack = new Package(sender,target);

        return sqlManager.insertPackage(sender,target).thenApply((id) -> {
            pack.setId(id);
            packages.add(pack);

            return id;
        });

    }

    public void deletePackage(int id){
        packages.removeIf(pack -> pack.getId() == id);

        sqlManager.deletePackage(id);
    }
    public Optional<Package> getPackage(int id){
        return packages.stream().filter(pack -> pack.getId() == id).findAny();
    }

    public List<Package> getPackages(String target){
        return packages.stream()
                .filter(pack -> pack.getTarget().equals(target))
                .collect(Collectors.toList());
    }

    public List<Package> getPackages(String target, String sender){
        return packages.stream()
                .filter(pack -> pack.getTarget().equals(target) && pack.getSender().equals(sender))
                .collect(Collectors.toList());
    }


    public void updateStatus(int id,Package.STATUS status){
        Optional<Package> optionalPack = getPackage(id);

        if(optionalPack.isPresent()){
            Package pack = optionalPack.get();

            pack.setStatus(status);
        }
    }


}

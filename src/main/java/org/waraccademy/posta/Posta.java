package org.waraccademy.posta;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.waraccademy.mongo.database.IMongoDBManager;
import org.waraccademy.posta.commands.impl.mailboxes.MailboxCommand;
import org.waraccademy.posta.commands.impl.packs.PackCommand;
import org.waraccademy.posta.configuration.FileManager;
import org.waraccademy.posta.database.mongo.MongoDBManager;
import org.waraccademy.posta.database.sql.MySQLManager;
import org.waraccademy.posta.listeners.HeadDatabaseListener;
import org.waraccademy.posta.listeners.JoinListener;
import org.waraccademy.posta.listeners.mailbox.BlockListener;
import org.waraccademy.posta.listeners.packages.InteractListener;
import org.waraccademy.posta.listeners.packages.InventoryListener;
import org.waraccademy.posta.listeners.packages.OpenListener;
import org.waraccademy.posta.services.Service;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;
import org.waraccademy.posta.services.impl.packages.PackageService;

import java.util.ArrayList;
import java.util.List;

public final class Posta extends JavaPlugin {
    private static Posta instance;

    private final List<Service> services = new ArrayList<>();
    private IMongoDBManager mongo;
    private YamlConfiguration config;
    private HeadDatabaseAPI headAPI;

    private MySQLManager sqlManager;

    private MailboxService mailboxService;
    private PackageService packageService;

    @Override
    public void onEnable() {
        instance = this;
        mongo = Bukkit.getServicesManager().getRegistration(IMongoDBManager.class).getProvider();
        FileManager manager = new FileManager("config", this);
        manager.saveDefault();
        config = manager.getConfig();

        sqlManager = new MySQLManager(config);

        MongoDBManager mongoDBManager = new MongoDBManager(this);
        mailboxService = new MailboxService(sqlManager, mongoDBManager);
        packageService = new PackageService(sqlManager);

        services.add(mailboxService);
        services.add(packageService);

        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new HeadDatabaseListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(mongoDBManager), this);
        Bukkit.getPluginManager().registerEvents(new OpenListener(),this);

        new PackCommand(this);
        new MailboxCommand(this);


        services.forEach((service -> service.onLoad(this)));
    }

    public static Posta getInstance() {
        return instance;
    }

    public IMongoDBManager getMongo() {
        return mongo;
    }

    public HeadDatabaseAPI getHeadAPI() {
        return headAPI;
    }

    public MySQLManager getSqlManager() {
        return sqlManager;
    }

    public MailboxService getMailboxService() {
        return mailboxService;
    }

    public PackageService getPackageService() {
        return packageService;
    }

    @Override
    public YamlConfiguration getConfig() {
        return config;
    }

    public void setHeadAPI(HeadDatabaseAPI headAPI) {
        this.headAPI = headAPI;
    }
}

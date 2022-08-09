package org.waraccademy.posta.listeners;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.waraccademy.posta.Posta;

public class HeadDatabaseListener implements Listener {

    @EventHandler
    public void onLoad(DatabaseLoadEvent e){
        Posta.getInstance().setHeadAPI(new HeadDatabaseAPI());
    }
}

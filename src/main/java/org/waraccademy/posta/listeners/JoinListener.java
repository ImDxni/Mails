package org.waraccademy.posta.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.database.sql.MySQLManager;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;

public class JoinListener implements Listener {
    private final MySQLManager manager = Posta.getInstance().getSqlManager();
    private final MailboxService mailboxService = Posta.getInstance().getMailboxService();

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        String target = e.getPlayer().getName();
        manager.ownerExists(target).whenComplete((exists,error) -> {
            if (error != null) {
                error.printStackTrace();
                return;
            }

            if (!exists)
                manager.insertOwner(target);

        });

        //TODO avviso x box con items
    }
}

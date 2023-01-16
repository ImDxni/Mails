package org.waraccademy.posta.listeners;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.database.sql.MySQLManager;
import org.waraccademy.posta.services.impl.mailboxes.Mailbox;
import org.waraccademy.posta.services.impl.mailboxes.MailboxService;
import org.waraccademy.posta.utils.Triple;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.waraccademy.posta.utils.Utils.color;

public class JoinListener implements Listener {
    private final MySQLManager manager = Posta.getInstance().getSqlManager();
    private final MailboxService mailboxService = Posta.getInstance().getMailboxService();
    private final YamlConfiguration config = Posta.getInstance().getConfig();

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


        for (Triple<Integer> location : mailboxService.getLocations(target)) {
            Optional<Mailbox> box = mailboxService.getMailbox(location);
            if(!box.isPresent()) continue;
            Mailbox mailbox = box.get();

            if(mailbox.hasPackages()){
                e.getPlayer().sendMessage(color(config.getString("messages.packages-available")
                        .replace("%x%",String.valueOf(location.getFirst()))
                        .replace("%y%",String.valueOf(location.getSecond()))
                        .replace("%z%",String.valueOf(location.getThird()))));
            }
        }

        for (Map.Entry<Triple<Integer>, Mailbox> entry : mailboxService.getMailboxes().entrySet()) {
            Triple<Integer> location = entry.getKey();
            Mailbox mailbox = entry.getValue();

            if(!mailbox.isLocked()) continue;

            if(mailbox.hasPackages()) {
                if (e.getPlayer().hasPermission("metropolis.posta.owner." + mailbox.getOwner().toLowerCase(Locale.ROOT))) {
                    e.getPlayer().sendMessage(color(config.getString("messages.packages-available")
                            .replace("%x%", String.valueOf(location.getFirst()))
                            .replace("%y%", String.valueOf(location.getSecond()))
                            .replace("%z%", String.valueOf(location.getThird()))));
                }
            }
        }
    }
}

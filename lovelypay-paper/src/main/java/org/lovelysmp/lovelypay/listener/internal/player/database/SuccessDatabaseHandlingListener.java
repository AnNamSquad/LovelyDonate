package org.lovelysmp.lovelypay.listener.internal.player.database;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.event.PaymentSuccessEvent;
import org.lovelysmp.lovelypay.service.DatabaseService;
import org.lovelysmp.lovelypay.service.cache.CacheDataService;

// fking ass class name, longgg
public class SuccessDatabaseHandlingListener implements Listener {
    public SuccessDatabaseHandlingListener(LPPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void updateDBz(PaymentSuccessEvent event) {
        LPPlugin plugin = LPPlugin.getInstance();

        plugin.getFoliaLib().getScheduler().runAsync(task -> {
            LPPlugin.getService(DatabaseService.class).getPaymentLogService().addPayment(event.getPayment());
        });
    }

    @EventHandler
    public void updateQueue(PaymentSuccessEvent event) {
        LPPlugin plugin = LPPlugin.getInstance();

        plugin.getFoliaLib().getScheduler().runAsync(task -> {
            LPPlugin.getService(CacheDataService.class).addPlayerToQueue(event.getPlayerUUID());
        });
    }
}

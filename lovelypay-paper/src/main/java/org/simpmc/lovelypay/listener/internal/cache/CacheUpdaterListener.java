package org.simpmc.lovelypay.listener.internal.cache;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.event.PaymentSuccessEvent;
import org.simpmc.lovelypay.service.DatabaseService;
import org.simpmc.lovelypay.service.PaymentService;
import org.simpmc.lovelypay.service.cache.CacheDataService;

public class CacheUpdaterListener implements Listener {
    public CacheUpdaterListener(LPPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getFoliaLib().getScheduler().runLaterAsync(() -> {
                    LPPlugin.getService(CacheDataService.class).updateServerDataCache();
                }, 1
        );
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LPPlugin plugin = LPPlugin.getInstance();
        plugin.getFoliaLib().getScheduler().runAsync(task2 -> LPPlugin.getService(DatabaseService.class).getPlayerService().createPlayer(event.getPlayer()));
        LPPlugin.getService(CacheDataService.class).addPlayerToQueue(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        LPPlugin.getService(CacheDataService.class).clearPlayerCache(event.getPlayer().getUniqueId());
        LPPlugin.getService(PaymentService.class).clearPlayerBankCache(event.getPlayer().getUniqueId());
        LPPlugin.getService(PaymentService.class).cancelBankPayment(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        LPPlugin plugin = LPPlugin.getInstance();
        LPPlugin.getService(CacheDataService.class).addPlayerToQueue(event.getPlayerUUID());
        plugin.getFoliaLib().getScheduler().runAsync(task2 -> LPPlugin.getService(CacheDataService.class).updateServerDataCache());
    }
}

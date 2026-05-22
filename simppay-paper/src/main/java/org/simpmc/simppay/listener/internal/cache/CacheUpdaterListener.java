package org.simpmc.simppay.listener.internal.cache;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.simpmc.simppay.SPPlugin;
import org.simpmc.simppay.event.PaymentSuccessEvent;
import org.simpmc.simppay.service.DatabaseService;
import org.simpmc.simppay.service.PaymentService;
import org.simpmc.simppay.service.cache.CacheDataService;

public class CacheUpdaterListener implements Listener {
    public CacheUpdaterListener(SPPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getFoliaLib().getScheduler().runLaterAsync(() -> {
                    SPPlugin.getService(CacheDataService.class).updateServerDataCache();
                }, 1
        );
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SPPlugin plugin = SPPlugin.getInstance();
        plugin.getFoliaLib().getScheduler().runAsync(task2 -> SPPlugin.getService(DatabaseService.class).getPlayerService().createPlayer(event.getPlayer()));
        SPPlugin.getService(CacheDataService.class).addPlayerToQueue(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SPPlugin.getService(CacheDataService.class).clearPlayerCache(event.getPlayer().getUniqueId());
        SPPlugin.getService(PaymentService.class).clearPlayerBankCache(event.getPlayer().getUniqueId());
        SPPlugin.getService(PaymentService.class).cancelBankPayment(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        SPPlugin plugin = SPPlugin.getInstance();
        SPPlugin.getService(CacheDataService.class).addPlayerToQueue(event.getPlayerUUID());
        plugin.getFoliaLib().getScheduler().runAsync(task2 -> SPPlugin.getService(CacheDataService.class).updateServerDataCache());
    }
}

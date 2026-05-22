package org.simpmc.lovelypay.listener.internal.player;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.config.types.NaplandauConfig;
import org.simpmc.lovelypay.database.entities.SPPlayer;
import org.simpmc.lovelypay.event.PaymentSuccessEvent;
import org.simpmc.lovelypay.service.DatabaseService;
import org.simpmc.lovelypay.service.database.PlayerService;

import java.sql.SQLException;


public class NaplandauListener implements Listener {
    public NaplandauListener(LPPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFirstPayment(PaymentSuccessEvent event) {
        LPPlugin.getInstance().getFoliaLib().getScheduler().runAsync(task -> {
            PlayerService playerService = LPPlugin.getService(DatabaseService.class).getPlayerService();
            SPPlayer player = playerService.findByUuid(event.getPlayerUUID());
            String value;
            try {
                value = LPPlugin.getService(DatabaseService.class).getPlayerDataService().getValue(player, "first_charge");
            } catch (SQLException e) {
                e.printStackTrace();
                value = null;
            }
            if (value == null) {
                playerService.setFirstCharge(player);
                return;
            }
            if (!value.equalsIgnoreCase("true")) {
                NaplandauConfig naplandauConfig = LPPlugin.getInstance().getConfigManager().getConfig(NaplandauConfig.class);
                for (String command : naplandauConfig.commands) {
                    LPPlugin.getInstance().getFoliaLib().getScheduler().runLater(task2 -> {
                        String formattedCommand = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(event.getPlayerUUID()), command);
                        LPPlugin.getInstance().getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), formattedCommand);
                    }, 1);
                }
            }
        });
    }
}

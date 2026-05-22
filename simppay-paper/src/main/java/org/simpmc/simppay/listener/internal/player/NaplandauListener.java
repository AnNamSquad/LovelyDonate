package org.simpmc.simppay.listener.internal.player;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.simpmc.simppay.SPPlugin;
import org.simpmc.simppay.config.types.NaplandauConfig;
import org.simpmc.simppay.database.entities.SPPlayer;
import org.simpmc.simppay.event.PaymentSuccessEvent;
import org.simpmc.simppay.service.DatabaseService;
import org.simpmc.simppay.service.database.PlayerService;

import java.sql.SQLException;


public class NaplandauListener implements Listener {
    public NaplandauListener(SPPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFirstPayment(PaymentSuccessEvent event) {
        SPPlugin.getInstance().getFoliaLib().getScheduler().runAsync(task -> {
            PlayerService playerService = SPPlugin.getService(DatabaseService.class).getPlayerService();
            SPPlayer player = playerService.findByUuid(event.getPlayerUUID());
            String value;
            try {
                value = SPPlugin.getService(DatabaseService.class).getPlayerDataService().getValue(player, "first_charge");
            } catch (SQLException e) {
                e.printStackTrace();
                value = null;
            }
            if (value == null) {
                playerService.setFirstCharge(player);
                return;
            }
            if (!value.equalsIgnoreCase("true")) {
                NaplandauConfig naplandauConfig = SPPlugin.getInstance().getConfigManager().getConfig(NaplandauConfig.class);
                for (String command : naplandauConfig.commands) {
                    SPPlugin.getInstance().getFoliaLib().getScheduler().runLater(task2 -> {
                        String formattedCommand = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(event.getPlayerUUID()), command);
                        SPPlugin.getInstance().getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), formattedCommand);
                    }, 1);
                }
            }
        });
    }
}

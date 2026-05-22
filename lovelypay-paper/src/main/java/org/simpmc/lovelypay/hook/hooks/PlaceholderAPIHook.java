package org.simpmc.lovelypay.hook.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.CoinsConfig;
import org.simpmc.lovelypay.config.types.MessageConfig;
import org.simpmc.lovelypay.service.cache.CacheDataService;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final LPPlugin plugin;

    public PlaceholderAPIHook(LPPlugin plugin) {
        this.plugin = plugin;
        register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lovelypay";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Typical";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; //
    }


    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {

        CacheDataService cacheDataService = LPPlugin.getService(CacheDataService.class);

        // get server_total
        // %lovelypay_server_total%
        if (identifier.equalsIgnoreCase("server_total")) {
            return cacheDataService.getServerTotalValue().toString(); // cached
        }
        // %lovelypay_server_total_formatted%
        if (identifier.equalsIgnoreCase("server_total_formatted")) {
            return String.format("%,d", cacheDataService.getServerTotalValue().get());
        }
        // %lovelypay_bank_total_formatted%
        if (identifier.equalsIgnoreCase("bank_total_formatted")) {
            return String.format("%,d", cacheDataService.getBankTotalValue().get());
        }
        // %lovelypay_card_total_formatted%
        if (identifier.equalsIgnoreCase("card_total_formatted")) {
            return String.format("%,d", cacheDataService.getCardTotalValue().get());
        }

        // %lovelypay_end_promo%
        if (identifier.equalsIgnoreCase("end_promo")) {
            CoinsConfig coinsConfig = ConfigManager.getInstance().getConfig(CoinsConfig.class);
            MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);
            
            // check promo time
            try {
                LocalDateTime promoEndTime = LocalDateTime.parse(coinsConfig.promoEndTimeString, coinsConfig.formatter);
                if (promoEndTime.isBefore(LocalDateTime.now())) {
                    return messageConfig.noPromo;
                } else {
                    return coinsConfig.promoEndTimeString;
                }
            } catch (Exception e) {
                // Parse lỗi thời gian -> coi như không có khuyến mãi
                return messageConfig.noPromo;
            }
        }

        if (player == null) {
            return null;
        }

        UUID uuid = player.getUniqueId();
        if (!cacheDataService.getPlayerTotalValue().containsKey(uuid)) {
            cacheDataService.addPlayerToQueue(uuid);
            return "0";
        }

        // %lovelypay_total%
        if (identifier.equalsIgnoreCase("total")) {
            return cacheDataService.getPlayerTotalValue().get(uuid).toString();
        }
        // %lovelypay_total_formatted%
        if (identifier.equalsIgnoreCase("total_formatted")) {
            return String.format("%,d", cacheDataService.getPlayerTotalValue().get(uuid).get());
        }

        return null;
    }

}

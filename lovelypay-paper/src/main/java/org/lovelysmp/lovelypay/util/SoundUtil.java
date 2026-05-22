package org.lovelysmp.lovelypay.util;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.lovelysmp.lovelypay.LPPlugin;

import java.util.UUID;

public class SoundUtil {
    public static void sendSound(Player player, Sound sound) {
        LPPlugin.getInstance().getFoliaLib().getScheduler().runAtEntity(player, task -> {
            player.playSound(sound, Sound.Emitter.self());
        });
    }

    public static void sendSound(UUID playerUUID, Sound sound) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            return;
        }
        LPPlugin.getInstance().getFoliaLib().getScheduler().runAtEntity(player, task -> {
            player.playSound(sound, Sound.Emitter.self());
        });
    }
}

package org.simpmc.simppay.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMetaUtils {
    public static List<String> colorizeLore(List<String> lore) {
        return lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }
}

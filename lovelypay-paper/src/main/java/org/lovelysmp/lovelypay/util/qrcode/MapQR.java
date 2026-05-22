package org.lovelysmp.lovelypay.util.qrcode;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMapData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapPalette;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.BankingConfig;
import org.lovelysmp.lovelypay.util.MessageUtil;
import org.lovelysmp.lovelypay.util.qrcode.fastqrcodegen.QrCode;

import java.util.Arrays;

public class MapQR {
    private static final int MAP_ID = 999;
    private static final int MAP_SIZE = 128;

    public static byte[] encodeTextToMapBytes(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("QR text is empty");
        }

        // 1) Generate a Version-1 QR at low ECC (21×21 modules)
        QrCode qr = QrCode.encodeText(text, QrCode.Ecc.LOW);
        int mSize = qr.size;  // 21 for version 1
        if (mSize > MAP_SIZE) {
            throw new IllegalArgumentException("QR code is too large for a Minecraft map: " + mSize + "x" + mSize);
        }

        // 2) Compute scale and border so modules fill 128×128 exactly
        int scale = Math.max(1, MAP_SIZE / mSize);                   // floor(128/21)=6
        int border = Math.max(0, (MAP_SIZE - mSize * scale) / 2);     // (128-21*6)/2 = 1

        // 3) Allocate the map-data array
        byte[] mapBytes = new byte[MAP_SIZE * MAP_SIZE];
        Arrays.fill(mapBytes, (byte) 0);

        // 4) Walk every map-pixel, sample the QR module, map to palette index
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                // Convert canvas-pixel to QR-module coords
                int mx = (x - border) / scale;
                int my = (y - border) / scale;

                boolean black = false;
                if (mx >= 0 && mx < mSize && my >= 0 && my < mSize) {
                    black = qr.getModule(mx, my);
                }

                // Black→(0,0,0), White→(255,255,255)
                int r = black ? 0 : 255;
                int g = black ? 0 : 255;
                int b = black ? 0 : 255;

                mapBytes[x + y * MAP_SIZE] = MapPalette.matchColor(r, g, b); //noinspection deprecation
            }
        }

        return mapBytes;
    }

    public static void sendPacketQRMap(byte[] mapBytes, Player player) {
        LPPlugin.getInstance().getFoliaLib().getScheduler().runAtEntity(player, task -> {
            try {
                sendPacketQRMapNow(mapBytes, player);
            } catch (RuntimeException e) {
                MessageUtil.warn("[MapQR] Failed to send QR map to " + player.getName() + ": " + e.getMessage());
            }
        });
    }

    private static void sendPacketQRMapNow(byte[] mapBytes, Player player) {
        if (mapBytes == null || mapBytes.length != MAP_SIZE * MAP_SIZE) {
            throw new IllegalArgumentException("Map QR data must be exactly " + (MAP_SIZE * MAP_SIZE) + " bytes");
        }

        int slotIndex = getMapSlot(player);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSetSlot(
                -2,
                0,
                slotIndex,
                createMapItem()
        ));

        // Create a new map data packet
        WrapperPlayServerMapData mapDataPacket = new WrapperPlayServerMapData(
                MAP_ID,
                (byte) 0,
                false,
                true,
                null,
                128,
                128,
                0,
                0,
                mapBytes
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, mapDataPacket);
    }

    private static int getMapSlot(Player player) {
        if (ConfigManager.getInstance().getConfig(BankingConfig.class).showQrOnLeftHand) {
            return 45; // offhand
        }
        return 36 + player.getInventory().getHeldItemSlot();
    }

    @SuppressWarnings("deprecation")
    private static ItemStack createMapItem() {
        org.bukkit.inventory.ItemStack bukkitMap = new org.bukkit.inventory.ItemStack(Material.FILLED_MAP, 1);
        MapMeta meta = (MapMeta) bukkitMap.getItemMeta();
        if (meta != null) {
            meta.setMapId(MAP_ID);
            bukkitMap.setItemMeta(meta);
        }
        return SpigotConversionUtil.fromBukkitItemStack(bukkitMap);
    }
}

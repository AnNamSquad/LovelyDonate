package org.simpmc.simppay.util.qrcode;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMapData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;
import org.simpmc.simppay.config.ConfigManager;
import org.simpmc.simppay.config.types.BankingConfig;
import org.simpmc.simppay.util.qrcode.fastqrcodegen.QrCode;

import java.util.Arrays;

public class MapQR {
    public static byte[] encodeTextToMapBytes(String text) {
        // 1) Generate a Version-1 QR at low ECC (21×21 modules)
        QrCode qr = QrCode.encodeText(text, QrCode.Ecc.LOW);
        int mSize = qr.size;  // 21 for version 1

        // 2) Compute scale and border so modules fill 128×128 exactly
        final int TARGET = 128;
        int scale = TARGET / mSize;                   // floor(128/21)=6
        int border = (TARGET - mSize * scale) / 2;     // (128-21*6)/2 = 1

        // 3) Allocate the map-data array
        byte[] mapBytes = new byte[TARGET * TARGET];
        Arrays.fill(mapBytes, (byte) 0);

        // 4) Walk every map-pixel, sample the QR module, map to palette index
        for (int y = 0; y < TARGET; y++) {
            for (int x = 0; x < TARGET; x++) {
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

                mapBytes[x + y * TARGET] = MapPalette.matchColor(r, g, b); //noinspection deprecation
            }
        }

        return mapBytes;
    }

    public static void sendPacketQRMap(byte[] mapBytes, Player player) {
        // Create a new map data packet
        WrapperPlayServerMapData mapDataPacket = new WrapperPlayServerMapData(
                999,
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

        // Send the map item with fake data to player
        // TODO: Check server verison => create respective packet, nbt or component
        NBTCompound compound = new NBTCompound();
        compound.setTag("map", new NBTInt(999));
        int slotIndex = ConfigManager.getInstance().getConfig(BankingConfig.class).showQrOnLeftHand ? 45 : 36; // 45 for left hand, 36 for main hand
        WrapperPlayServerSetSlot setSlotPacket = new WrapperPlayServerSetSlot(
                0,
                0,
                slotIndex,
                ItemStack.builder().type(ItemTypes.FILLED_MAP).nbt(compound).component(ComponentTypes.MAP_ID, 999).build()
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, setSlotPacket);
    }

}

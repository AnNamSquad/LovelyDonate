package org.lovelysmp.lovelypay.listener.internal.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.MessageConfig;
import org.lovelysmp.lovelypay.event.PaymentBankPromptEvent;
import org.lovelysmp.lovelypay.handler.banking.data.BankingData;
import org.lovelysmp.lovelypay.service.PaymentService;
import org.lovelysmp.lovelypay.util.MessageUtil;
import org.lovelysmp.lovelypay.util.qrcode.MapQR;
import org.lovelysmp.lovelypay.util.qrcode.vietqr.VietQr;

public class BankPromptListener implements Listener {

    public BankPromptListener(LPPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void paymentPrompt(PaymentBankPromptEvent event) {

        MessageConfig config = ConfigManager.getInstance().getConfig(MessageConfig.class);
        BankingData bankingData = event.getBankingData();
        if (bankingData.getUrl() != null) {
            MessageUtil.sendMessage(event.getPlayerUUID(), config.promptPaymentLink.replace("<link>", bankingData.getUrl()));
        }

        // Sending packet map to player

        Player player = Bukkit.getPlayer(event.getPlayerUUID());
        if (player == null) {
            return;
        }
        String qrCode;
        if (bankingData.getQrString() != null) {
            qrCode = bankingData.getQrString();
        } else {
            qrCode = VietQr.getVietQr(
                    bankingData.bin,
                    bankingData.accountNumber,
                    String.valueOf(bankingData.amount),
                    bankingData.desc
            );
        }
        MessageUtil.debug("BankPrompt: " + qrCode);

        try {
            byte[] mapBytes = MapQR.encodeTextToMapBytes(qrCode);

            LPPlugin.getService(PaymentService.class).getPlayerBankQRCode().put(event.getPlayerUUID(), mapBytes);
            // PacketEvents
            // Forge a fake mapData
            MapQR.sendPacketQRMap(mapBytes, player);
        } catch (RuntimeException e) {
            MessageUtil.warn("[BankPrompt] Failed to generate QR map for " + player.getName() + ": " + e.getMessage());
        }


    }
}

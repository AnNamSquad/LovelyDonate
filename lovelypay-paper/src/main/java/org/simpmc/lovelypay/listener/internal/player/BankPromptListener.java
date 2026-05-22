package org.simpmc.lovelypay.listener.internal.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.MessageConfig;
import org.simpmc.lovelypay.event.PaymentBankPromptEvent;
import org.simpmc.lovelypay.handler.banking.data.BankingData;
import org.simpmc.lovelypay.service.PaymentService;
import org.simpmc.lovelypay.util.MessageUtil;
import org.simpmc.lovelypay.util.qrcode.MapQR;
import org.simpmc.lovelypay.util.qrcode.vietqr.VietQr;

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

        byte[] mapBytes = MapQR.encodeTextToMapBytes(qrCode);

        LPPlugin.getService(PaymentService.class).getPlayerBankQRCode().put(event.getPlayerUUID(), mapBytes);
        // PacketEvents
        // Forge a fake mapData
        MapQR.sendPacketQRMap(mapBytes, player);


    }
}

package org.lovelysmp.lovelypay.forms;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.database.dto.PaymentRecord;
import org.lovelysmp.lovelypay.database.entities.SPPlayer;
import org.lovelysmp.lovelypay.service.DatabaseService;

import java.util.List;


public class ViewHistoryForm {
    public static SimpleForm getHistoryForm(Player player) {
        List<PaymentRecord> paymentRecords = fetchPaymentRecordsAsync(player);
        SimpleForm.Builder simpleForm = SimpleForm.builder();
        for (PaymentRecord paymentRecord : paymentRecords) {
            String message = String.format(ChatColor.DARK_GREEN + "Số tiền: %s",
                    String.format("%,.0f", paymentRecord.getAmount()) + "đ");
            simpleForm.button(message); // TODO: make form configurable
        }
        return simpleForm.build();
    }

    private static List<PaymentRecord> fetchPaymentRecordsAsync(Player player) {
        SPPlayer spPlayer;
        spPlayer = LPPlugin.getService(DatabaseService.class).getPlayerService().findByUuid(player.getUniqueId());
        Preconditions.checkNotNull(spPlayer, "Player not found");
        List<PaymentRecord> paymentRecords = LPPlugin.getService(DatabaseService.class).getPaymentLogService().getPaymentsByPlayer(spPlayer);
        return paymentRecords;
    }
}


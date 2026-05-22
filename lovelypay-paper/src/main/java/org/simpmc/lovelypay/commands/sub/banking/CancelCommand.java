package org.simpmc.lovelypay.commands.sub.banking;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.MessageConfig;
import org.simpmc.lovelypay.service.PaymentService;
import org.simpmc.lovelypay.util.MessageUtil;

public class CancelCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("cancel")
                .executesPlayer(CancelCommand::execute);
    }

    public static void execute(Player player, CommandArguments args) {
        MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);

        if (!LPPlugin.getService(PaymentService.class).getPlayerBankingSessionPayment().containsKey(player.getUniqueId())) {
            MessageUtil.sendMessage(player, messageConfig.noExistBankingSession);
        } else {
            MessageUtil.sendMessage(player, messageConfig.cancelBanking);
            LPPlugin.getService(PaymentService.class).cancelBankPayment(player.getUniqueId());
            player.updateInventory(); // remove qr map
        }
    }
}

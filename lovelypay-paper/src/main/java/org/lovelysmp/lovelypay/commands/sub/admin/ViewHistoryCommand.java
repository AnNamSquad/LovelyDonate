package org.lovelysmp.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.types.MessageConfig;
import org.lovelysmp.lovelypay.database.entities.SPPlayer;
import org.lovelysmp.lovelypay.menu.PaymentHistoryView;
import org.lovelysmp.lovelypay.menu.ServerPaymentHistoryView;
import org.lovelysmp.lovelypay.service.DatabaseService;
import org.lovelysmp.lovelypay.util.MessageUtil;

public class ViewHistoryCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("viewhistory")
                .withPermission("lovelypay.admin.viewhistory")
                .withAliases("lichsu", "lichsunap")
                .withOptionalArguments(
                        new StringArgument("player")
                )
                .executesPlayer(ViewHistoryCommand::execute);
    }

    public static void execute(Player player, CommandArguments args) {
        String playerName = (String) args.getOptional("player").orElse(null);
        if (playerName == null) {
            // view entire server history
            LPPlugin.getInstance().getViewFrame().open(ServerPaymentHistoryView.class, player);
            return;
        }
//        if (FloodgateApi.getInstance().isFloodgateId(player.getUniqueId())) {
//            Player targetPlayer = Bukkit.getPlayer(playerName);
//            FloodgateApi.getInstance().sendForm(player.getUniqueId(), ViewHistoryForm.getHistoryForm(targetPlayer));
//            return;
//        }
        SPPlayer targetPlayer = LPPlugin.getService(DatabaseService.class).getPlayerService().findByName(playerName);
        if (targetPlayer == null) {
            MessageConfig messageConfig = LPPlugin.getInstance().getConfigManager().getConfig(MessageConfig.class);
            MessageUtil.sendMessage(player, messageConfig.playerNotFound.replace("{name}", playerName));
            return;
        }
        LPPlugin.getInstance().getViewFrame().open(PaymentHistoryView.class, player, playerName);
    }
}

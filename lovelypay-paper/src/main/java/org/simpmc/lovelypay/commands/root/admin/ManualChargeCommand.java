package org.simpmc.lovelypay.commands.root.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.database.entities.SPPlayer;
import org.simpmc.lovelypay.event.PaymentSuccessEvent;
import org.simpmc.lovelypay.model.Payment;
import org.simpmc.lovelypay.model.detail.BankingDetail;
import org.simpmc.lovelypay.service.DatabaseService;

import java.util.UUID;

public class ManualChargeCommand {
    public ManualChargeCommand() {
        new CommandAPICommand("napthucong")
                .withPermission("lovelypay.napthucong")
                .withArguments(
                        new StringArgument("player"),
                        new StringArgument("amount")
                )
                .executes((sender, args) -> {
                    String playerName = (String) args.get("player");
                    String amountStr = (String) args.get("amount");

                    SPPlayer player = LPPlugin.getService(DatabaseService.class).getPlayerService().findByName(playerName);
                    if (player == null) {
                        sender.sendMessage("Người chơi không tồn tại.");
                        return;
                    }

                    Payment payment = new Payment(
                            UUID.randomUUID(),
                            player.getUuid(),
                            new BankingDetail(
                                    Double.parseDouble(amountStr),
                                    "0",
                                    "NAPTHUCONG",
                                    "1234567890"
                            )
                    );
                    Bukkit.getPluginManager().callEvent(new PaymentSuccessEvent(payment));
                })
                .register();
    }
}

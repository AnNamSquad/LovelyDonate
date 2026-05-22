package org.lovelysmp.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.lovelysmp.lovelypay.event.PaymentSuccessEvent;
import org.lovelysmp.lovelypay.model.Payment;
import org.lovelysmp.lovelypay.model.detail.BankingDetail;

import java.util.UUID;

public class FakeBankCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("fakebank")
                .withPermission("lovelypay.admin.fakebank")
                .withArguments(
                        new LongArgument("amount")
                )
                .executesPlayer(FakeBankCommand::execute);
    }

    public static void execute(Player player, CommandArguments args) {
        long amount = (long) args.get("amount");
        Payment payment = new Payment(
                UUID.randomUUID(),
                player.getUniqueId(),
                new BankingDetail(
                        amount,
                        "1234567890123456",
                        "1234",
                        "1234567890"
                )
        );
        Bukkit.getPluginManager().callEvent(new PaymentSuccessEvent(payment));
    }
}

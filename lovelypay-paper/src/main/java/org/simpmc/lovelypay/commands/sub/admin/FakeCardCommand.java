package org.simpmc.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.data.card.CardPrice;
import org.simpmc.lovelypay.data.card.CardType;
import org.simpmc.lovelypay.event.PaymentSuccessEvent;
import org.simpmc.lovelypay.model.Payment;
import org.simpmc.lovelypay.model.detail.CardDetail;

import java.util.UUID;

public class FakeCardCommand {

    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("fakecard")
                .withPermission("lovelypay.admin.fakecard")
                .withArguments(
                        new BooleanArgument("wrongPrice")
                )
                .executesPlayer(FakeCardCommand::execute);
    }

    public static void execute(Player player, CommandArguments args) {
        LPPlugin plugin = LPPlugin.getInstance();

        Payment payment = new Payment(
                UUID.randomUUID(),
                player.getUniqueId(),
                new CardDetail(
                        "123123123",
                        "123123123",
                        CardPrice._10K,
                        CardType.VIETTEL,
                        "123123123",
                        10000
                )
        );
        boolean wrongPrice = (boolean) args.get("wrongPrice");
        Bukkit.getPluginManager().callEvent(new PaymentSuccessEvent(payment, wrongPrice));
    }
}

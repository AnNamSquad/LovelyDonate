package org.simpmc.lovelypay.commands.root;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.MessageConfig;
import org.simpmc.lovelypay.data.PaymentStatus;
import org.simpmc.lovelypay.data.card.CardPrice;
import org.simpmc.lovelypay.data.card.CardType;
import org.simpmc.lovelypay.model.Payment;
import org.simpmc.lovelypay.model.detail.CardDetail;
import org.simpmc.lovelypay.model.detail.PaymentDetail;
import org.simpmc.lovelypay.service.PaymentService;
import org.simpmc.lovelypay.util.MessageUtil;
import org.simpmc.lovelypay.util.SoundUtil;

import java.util.UUID;

// ignore unboxing create NPE
@SuppressWarnings("unboxing") // ignore unboxing create NPE
public class NaptheNhanhCommand {

    public NaptheNhanhCommand() {
        new CommandAPICommand("napthenhanh")
                .withPermission(CommandPermission.NONE)
                .withArguments(
                        new StringArgument("serial"),
                        new StringArgument("pin"),
                        new StringArgument("amount").replaceSuggestions(
                                ArgumentSuggestions.strings(
                                        CardPrice.getAllCardPrices()
                                )
                        ),
                        new StringArgument("telco").replaceSuggestions(ArgumentSuggestions.strings(
                                CardType.getAllCardTypes()
                        ))
                )
                .executesPlayer((player, args) -> {
                    // start a new napthe session
                    MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);
                    String serial = (String) args.get("serial");
                    String pin = (String) args.get("pin");

                    CardPrice amount = CardPrice.fromString((String) args.get("amount"));
                    CardType type = CardType.fromString((String) args.get("telco"));


                    UUID uuid = UUID.nameUUIDFromBytes(serial.getBytes()); // payment uuid is based on serial number of the card
                    PaymentDetail detail = CardDetail.builder()
                            .serial(serial)
                            .pin(pin)
                            .price(amount)
                            .type(type)
                            .build();
                    Payment payment = new Payment(uuid, player.getUniqueId(), detail);

                    if (LPPlugin.getService(PaymentService.class).getPayments().containsKey(payment.getPaymentID())) {
                        MessageUtil.sendMessage(player, messageConfig.pendingCard);
                        SoundUtil.sendSound(player, messageConfig.soundEffect.get(PaymentStatus.PENDING).toSound());
                        return;
                    }

                    PaymentStatus status = LPPlugin.getService(PaymentService.class).sendCard(payment);

                    if (status == PaymentStatus.FAILED) {
                        MessageUtil.sendMessage(player, messageConfig.failedCard);
                        SoundUtil.sendSound(player, messageConfig.soundEffect.get(PaymentStatus.FAILED).toSound());
                    }
                })
                .register();
    }
}

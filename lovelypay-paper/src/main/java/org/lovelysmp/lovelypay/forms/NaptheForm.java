package org.lovelysmp.lovelypay.forms;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.CardConfig;
import org.lovelysmp.lovelypay.config.types.MessageConfig;
import org.lovelysmp.lovelypay.data.PaymentStatus;
import org.lovelysmp.lovelypay.data.card.CardPrice;
import org.lovelysmp.lovelypay.data.card.CardType;
import org.lovelysmp.lovelypay.model.Payment;
import org.lovelysmp.lovelypay.model.detail.CardDetail;
import org.lovelysmp.lovelypay.model.detail.PaymentDetail;
import org.lovelysmp.lovelypay.service.PaymentService;
import org.lovelysmp.lovelypay.util.MessageUtil;
import org.lovelysmp.lovelypay.util.SoundUtil;

import java.util.List;
import java.util.UUID;

public class NaptheForm {
    public static CustomForm getNapTheForm(Player player) {
        List<String> cardTypes = ConfigManager.getInstance().getConfig(CardConfig.class).getEnabledCardTypes().stream().map(card -> card.name()).toList();
        return CustomForm.builder()
                .title("LovelyPay - Nạp thẻ")
                .dropdown("Loại Thẻ", cardTypes)
                .dropdown("Mệnh Giá", CardPrice.getAllCardPricesFormatted())
                .label(ChatColor.RED + "Lưu ý: Nhập sai mệnh giá sẽ nhận xu tương ứng giá trị thật của thẻ")
                .input("Số Serial", "Nhập số serial của thẻ")
                .input("Mã Thẻ", "Nhập mã PIN của thẻ")
                .label("Bấm Submit để nạp thẻ")
                .validResultHandler((customForm, res) -> {
                    CardType type = CardType.fromString(cardTypes.get(res.asDropdown()));
                    CardPrice amount = CardPrice.getCardPriceByIndex(res.asDropdown());
                    String serial = res.asInput();
                    String pin = res.asInput();
                    MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);

                    if (serial == null || pin == null) {
                        player.sendMessage(MessageUtil.getComponentParsed(messageConfig.invalidParam, player));
                        return;
                    }
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
                    return;

                })
                .build();
    }
}

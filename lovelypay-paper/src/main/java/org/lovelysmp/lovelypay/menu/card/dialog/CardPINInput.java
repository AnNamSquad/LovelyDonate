package org.lovelysmp.lovelypay.menu.card.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.MessageConfig;
import org.lovelysmp.lovelypay.config.types.menu.card.dialog.CardPinMenuConfig;
import org.lovelysmp.lovelypay.data.PaymentStatus;
import org.lovelysmp.lovelypay.model.Payment;
import org.lovelysmp.lovelypay.model.detail.CardDetail;
import org.lovelysmp.lovelypay.service.PaymentService;
import org.lovelysmp.lovelypay.util.MessageUtil;
import org.lovelysmp.lovelypay.util.SoundUtil;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
public class CardPINInput {
    private static final String INPUT_KEY = "pin";
    private static final Pattern CARD_INPUT_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    private final Object initialData;
    private final Player player;

    public CardPINInput(Player p, Object initialData) {
        this.initialData = initialData;
        this.player = p;
        openDialog();
    }

    public void openDialog() {
        CardPinMenuConfig menuConfig = LPPlugin.getInstance().getConfigManager().getConfig(CardPinMenuConfig.class);
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(MessageUtil.getComponentParsed(menuConfig.title, player))
                        .canCloseWithEscape(true)
                        .pause(false)
                        .afterAction(DialogBase.DialogAfterAction.CLOSE)
                        .body(List.of(DialogBody.item(menuConfig.item.getItemStack(player), null, true, true, 48, 48)))
                        .inputs(List.of(DialogInput.text(INPUT_KEY, Component.text("Mã Thẻ"))
                                .width(240)
                                .maxLength(64)
                                .build()))
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.create(Component.text("Nạp thẻ"), null, 120, DialogAction.customClick(this::handleSubmit, callbackOptions())),
                        ActionButton.create(Component.text("Hủy"), null, 80, null)
                )));
        player.showDialog(dialog);
    }

    private void handleSubmit(DialogResponseView response, Audience audience) {
        if (!(audience instanceof Player callbackPlayer) || !callbackPlayer.getUniqueId().equals(player.getUniqueId())) {
            return;
        }

        String pin = response.getText(INPUT_KEY);
        LPPlugin.getInstance().getFoliaLib().getScheduler().runAtEntity(callbackPlayer, task -> processPin(pin));
    }

    private void processPin(String pin) {
        if (pin == null || pin.isBlank() || !CARD_INPUT_PATTERN.matcher(pin).matches()) {
            MessageConfig config = LPPlugin.getInstance().getConfigManager().getConfig(MessageConfig.class);
            MessageUtil.sendMessage(player, config.invalidParam);
            openDialog();
            return;
        }

        CardDetail detail = (CardDetail) this.getInitialData();
        detail.setPin(pin.trim());
        UUID uuid = UUID.nameUUIDFromBytes(detail.serial.getBytes()); // payment uuid is based on serial number of the card
        Payment payment = new Payment(uuid, player.getUniqueId(), detail);

        MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);

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
    }

    private ClickCallback.Options callbackOptions() {
        return ClickCallback.Options.builder()
                .uses(1)
                .lifetime(Duration.ofMinutes(10))
                .build();
    }
}

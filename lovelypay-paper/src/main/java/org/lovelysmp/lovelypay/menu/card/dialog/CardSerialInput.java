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
import org.lovelysmp.lovelypay.config.types.MessageConfig;
import org.lovelysmp.lovelypay.config.types.menu.card.dialog.CardSerialMenuConfig;
import org.lovelysmp.lovelypay.model.detail.CardDetail;
import org.lovelysmp.lovelypay.util.MessageUtil;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class CardSerialInput {
    private static final String INPUT_KEY = "serial";
    private static final Pattern CARD_INPUT_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    private final Object initialData;
    private final Player player;

    public CardSerialInput(Player p, Object initialData) {
        this.initialData = initialData;
        this.player = p;
        openDialog();
    }

    public void openDialog() {
        CardSerialMenuConfig menuConfig = LPPlugin.getInstance().getConfigManager().getConfig(CardSerialMenuConfig.class);
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(MessageUtil.getComponentParsed(menuConfig.title, player))
                        .canCloseWithEscape(true)
                        .pause(false)
                        .afterAction(DialogBase.DialogAfterAction.CLOSE)
                        .body(List.of(DialogBody.item(menuConfig.item.getItemStack(player), null, true, true, 48, 48)))
                        .inputs(List.of(DialogInput.text(INPUT_KEY, Component.text("Số Serial"))
                                .width(240)
                                .maxLength(64)
                                .build()))
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.create(Component.text("Tiếp tục"), null, 120, DialogAction.customClick(this::handleSubmit, callbackOptions())),
                        ActionButton.create(Component.text("Hủy"), null, 80, null)
                )));
        player.showDialog(dialog);
    }

    private void handleSubmit(DialogResponseView response, Audience audience) {
        if (!(audience instanceof Player callbackPlayer) || !callbackPlayer.getUniqueId().equals(player.getUniqueId())) {
            return;
        }

        String serial = response.getText(INPUT_KEY);
        LPPlugin.getInstance().getFoliaLib().getScheduler().runAtEntity(callbackPlayer, task -> processSerial(serial));
    }

    private void processSerial(String serial) {
        if (serial == null || serial.isBlank() || !CARD_INPUT_PATTERN.matcher(serial).matches()) {
            MessageConfig config = LPPlugin.getInstance().getConfigManager().getConfig(MessageConfig.class);
            MessageUtil.sendMessage(player, config.invalidParam);
            openDialog();
            return;
        }

        CardDetail detail = (CardDetail) this.getInitialData();
        detail.setSerial(serial.trim());
        new CardPINInput(player, detail);
    }

    private ClickCallback.Options callbackOptions() {
        return ClickCallback.Options.builder()
                .uses(1)
                .lifetime(Duration.ofMinutes(10))
                .build();
    }
}

package org.lovelysmp.lovelypay.commands.root;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.entity.Player;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.menu.PaymentHistoryView;

public class ViewHistoryCommand {
    public ViewHistoryCommand() {
        new CommandAPICommand("lichsunapthe")
                .withPermission(CommandPermission.NONE)
                .withAliases("napthehistory", "xemgdnapthe")
                .executesPlayer((player, args) -> {
                    boolean isFloodgateUUID = player.getUniqueId().getMostSignificantBits() == 0;
                    boolean floodgateEnabled = LPPlugin.getInstance().isFloodgateEnabled();
                    if (floodgateEnabled && isFloodgateUUID) {
                        try {
                            Class<?> viewHistoryFormClass = Class.forName("org.lovelysmp.lovelypay.forms.ViewHistoryForm");
                            Object form = viewHistoryFormClass.getMethod("getHistoryForm", Player.class).invoke(null, player);

                            Class<?> floodgateUtilClass = Class.forName("org.lovelysmp.lovelypay.util.FloodgateUtil");
                            floodgateUtilClass.getMethod("sendForm", java.util.UUID.class, Object.class)
                                    .invoke(null, player.getUniqueId(), form);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    LPPlugin.getInstance().getViewFrame().open(PaymentHistoryView.class, player);
                })
                .register();
    }
}

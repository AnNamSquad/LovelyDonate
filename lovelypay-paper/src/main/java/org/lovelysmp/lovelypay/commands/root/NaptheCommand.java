package org.lovelysmp.lovelypay.commands.root;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.menu.card.CardListView;

public class NaptheCommand {

    public NaptheCommand() {
        new CommandAPICommand("napthe")
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    // start a new napthe session
                    boolean isFloodgateUUID = player.getUniqueId().getMostSignificantBits() == 0;
                    boolean floodgateEnabled = LPPlugin.getInstance().isFloodgateEnabled();
                    if (floodgateEnabled && isFloodgateUUID) {
                        try {
                            Class<?> naptheFormClass = Class.forName("org.lovelysmp.lovelypay.forms.NaptheForm");
                            Object form = naptheFormClass.getMethod("getNapTheForm", org.bukkit.entity.Player.class).invoke(null, player);

                            Class<?> floodgateUtilClass = Class.forName("org.lovelysmp.lovelypay.util.FloodgateUtil");
                            floodgateUtilClass.getMethod("sendForm", java.util.UUID.class, Object.class)
                                    .invoke(null, player.getUniqueId(), form);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    LPPlugin.getInstance().getViewFrame().open(CardListView.class, player);
                })
                .register();
    }
}

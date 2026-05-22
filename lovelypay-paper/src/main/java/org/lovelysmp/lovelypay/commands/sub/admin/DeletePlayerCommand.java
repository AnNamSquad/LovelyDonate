package org.lovelysmp.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.database.entities.SPPlayer;
import org.lovelysmp.lovelypay.service.DatabaseService;

public class DeletePlayerCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("deleteplayer")
                .withPermission("lovelypay.admin.deleteplayer")
                .withArguments(
                        new StringArgument("player")
                )
                .executesPlayer(DeletePlayerCommand::execute);
    }

    public static void execute(Player player, CommandArguments args) {
        LPPlugin plugin = LPPlugin.getInstance();

        String playerTarget = (String) args.get("player");
        plugin.getFoliaLib().getScheduler().runAsync(task -> {
            SPPlayer targetPlayer = LPPlugin.getService(DatabaseService.class).getPlayerService().findByName(playerTarget);
            LPPlugin.getService(DatabaseService.class).getPaymentLogService().resetPlayerPaymentLog(targetPlayer);
        });
    }
}

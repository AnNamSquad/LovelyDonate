package org.simpmc.simppay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.simpmc.simppay.SPPlugin;
import org.simpmc.simppay.database.entities.SPPlayer;
import org.simpmc.simppay.service.DatabaseService;

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
        SPPlugin plugin = SPPlugin.getInstance();

        String playerTarget = (String) args.get("player");
        plugin.getFoliaLib().getScheduler().runAsync(task -> {
            SPPlayer targetPlayer = SPPlugin.getService(DatabaseService.class).getPlayerService().findByName(playerTarget);
            SPPlugin.getService(DatabaseService.class).getPaymentLogService().resetPlayerPaymentLog(targetPlayer);
        });
    }
}

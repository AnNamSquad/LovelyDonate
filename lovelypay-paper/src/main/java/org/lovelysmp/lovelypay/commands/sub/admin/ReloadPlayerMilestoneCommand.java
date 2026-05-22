package org.lovelysmp.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.database.entities.SPPlayer;
import org.lovelysmp.lovelypay.service.DatabaseService;
import org.lovelysmp.lovelypay.service.MilestoneService;
import org.lovelysmp.lovelypay.util.MessageUtil;

public class ReloadPlayerMilestoneCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("reloadplayermilestone")
                .withPermission("lovelypay.admin.reloadplayermilestone")
                .withArguments(
                        new StringArgument("player")
                )
                .executes(ReloadPlayerMilestoneCommand::execute);
    }

    public static void execute(CommandSender player, CommandArguments args) {

        String playerTarget = (String) args.get("player");
        SPPlayer spPlayer = LPPlugin.getService(DatabaseService.class).getPlayerService().findByName(playerTarget);
        if (spPlayer == null) {
            MessageUtil.sendMessage(player, "Player not found");
            return;
        }
        LPPlugin.getService(MilestoneService.class).loadPlayerMilestone(spPlayer.getUuid());
    }
}

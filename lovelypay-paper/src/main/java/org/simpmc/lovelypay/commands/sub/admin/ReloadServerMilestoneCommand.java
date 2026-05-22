package org.simpmc.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.service.MilestoneService;

public class ReloadServerMilestoneCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("reloadservermilestone")
                .withPermission("lovelypay.admin.reloadservermilestone")
                .executes(ReloadServerMilestoneCommand::execute);
    }

    public static void execute(CommandSender player, CommandArguments args) {
        LPPlugin.getService(MilestoneService.class).loadServerMilestone();
    }
}

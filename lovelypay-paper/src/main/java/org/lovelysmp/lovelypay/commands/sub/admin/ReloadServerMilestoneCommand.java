package org.lovelysmp.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.service.MilestoneService;
import org.lovelysmp.lovelypay.util.MessageUtil;

public class ReloadServerMilestoneCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("reloadservermilestone")
                .withPermission("lovelypay.admin.reloadservermilestone")
                .executes(ReloadServerMilestoneCommand::execute);
    }

    public static void execute(CommandSender player, CommandArguments args) {
        ConfigManager.getInstance().reloadAll();
        LPPlugin.getService(MilestoneService.class).loadServerMilestone();
        MessageUtil.sendMessage(player, "Server milestones reloaded.");
    }
}

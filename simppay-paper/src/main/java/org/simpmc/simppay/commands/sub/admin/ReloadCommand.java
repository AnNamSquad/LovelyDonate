package org.simpmc.simppay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.simpmc.simppay.SPPlugin;
import org.simpmc.simppay.config.ConfigManager;
import org.simpmc.simppay.config.types.MessageConfig;
import org.simpmc.simppay.service.PaymentService;
import org.simpmc.simppay.util.MessageUtil;

public class ReloadCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("reload")
                .withPermission("lovelypay.admin.reload")
                .executes(ReloadCommand::execute);
    }

    public static void execute(CommandSender player, CommandArguments args) {
        SPPlugin plugin = SPPlugin.getInstance();
        plugin.getFoliaLib().getScheduler().runAsync(task -> {
            plugin.getConfigManager().reloadAll();
            MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);
            SPPlugin.getService(PaymentService.class).getHandlerRegistry().reload();
            MessageUtil.sendMessage(player, messageConfig.configReloaded);
        });
    }
}

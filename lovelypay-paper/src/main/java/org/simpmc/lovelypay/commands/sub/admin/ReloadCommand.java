package org.simpmc.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.MessageConfig;
import org.simpmc.lovelypay.service.PaymentService;
import org.simpmc.lovelypay.util.MessageUtil;

public class ReloadCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("reload")
                .withPermission("lovelypay.admin.reload")
                .executes(ReloadCommand::execute);
    }

    public static void execute(CommandSender player, CommandArguments args) {
        LPPlugin plugin = LPPlugin.getInstance();
        plugin.getFoliaLib().getScheduler().runAsync(task -> {
            plugin.getConfigManager().reloadAll();
            MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);
            LPPlugin.getService(PaymentService.class).getHandlerRegistry().reload();
            MessageUtil.sendMessage(player, messageConfig.configReloaded);
        });
    }
}

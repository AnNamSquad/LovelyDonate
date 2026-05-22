package org.lovelysmp.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.MessageConfig;
import org.lovelysmp.lovelypay.service.PaymentService;
import org.lovelysmp.lovelypay.util.MessageUtil;

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

package org.simpmc.lovelypay.commands.sub.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.simpmc.lovelypay.commands.CommandHelp;

public class AdminHelpCommand {
    public static CommandAPICommand commandCreate() {
        return new CommandAPICommand("help")
                .withPermission("lovelypay.admin")
                .executes(AdminHelpCommand::execute);
    }

    public static void execute(CommandSender sender, CommandArguments args) {
        CommandHelp.sendAdminHelp(sender);
    }
}

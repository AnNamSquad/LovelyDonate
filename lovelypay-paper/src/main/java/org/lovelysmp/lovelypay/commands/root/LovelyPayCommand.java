package org.lovelysmp.lovelypay.commands.root;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.lovelysmp.lovelypay.commands.CommandHelp;

public class LovelyPayCommand {
    public LovelyPayCommand() {
        new CommandAPICommand("lovelypay")
                .withPermission(CommandPermission.NONE)
                .withAliases("lp")
                .withSubcommand(new CommandAPICommand("help")
                        .withPermission(CommandPermission.NONE)
                        .executes(LovelyPayCommand::execute))
                .executes(LovelyPayCommand::execute)
                .register();
    }

    public static void execute(CommandSender sender, CommandArguments args) {
        CommandHelp.sendLovelyPayHelp(sender);
    }
}

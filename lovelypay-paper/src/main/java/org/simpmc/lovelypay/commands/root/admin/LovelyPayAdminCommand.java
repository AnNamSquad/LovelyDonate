package org.simpmc.lovelypay.commands.root.admin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.simpmc.lovelypay.commands.CommandHelp;
import org.simpmc.lovelypay.commands.sub.admin.*;

public class LovelyPayAdminCommand {
    public LovelyPayAdminCommand() {
        new CommandAPICommand("lovelypayadmin")
                .withPermission("lovelypay.admin")
                .withSubcommands(
                        AdminHelpCommand.commandCreate(),
                        ReloadCommand.commandCreate(),
                        ViewHistoryCommand.commandCreate(),
                        FakeBankCommand.commandCreate(),
                        FakeCardCommand.commandCreate(),
                        DeletePlayerCommand.commandCreate(),
                        ReloadServerMilestoneCommand.commandCreate(),
                        ReloadPlayerMilestoneCommand.commandCreate()
                )
                .executes(LovelyPayAdminCommand::execute)
                .register();
    }

    public static void execute(CommandSender sender, CommandArguments args) {
        CommandHelp.sendAdminHelp(sender);
    }
}

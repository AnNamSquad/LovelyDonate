package org.simpmc.simppay.commands.root.admin;

import dev.jorel.commandapi.CommandAPICommand;
import org.simpmc.simppay.commands.sub.admin.*;

public class SimpPayAdminCommand {
    public SimpPayAdminCommand() {
        new CommandAPICommand("lovelypayadmin")
                .withPermission("lovelypay.admin")
                .withSubcommands(
                        ReloadCommand.commandCreate(),
                        ViewHistoryCommand.commandCreate(),
                        FakeBankCommand.commandCreate(),
                        FakeCardCommand.commandCreate(),
                        DeletePlayerCommand.commandCreate(),
                        ReloadServerMilestoneCommand.commandCreate(),
                        ReloadPlayerMilestoneCommand.commandCreate()
                )
                .register();
    }
}

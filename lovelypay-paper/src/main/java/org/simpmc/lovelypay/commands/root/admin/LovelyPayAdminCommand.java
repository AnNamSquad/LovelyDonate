package org.simpmc.lovelypay.commands.root.admin;

import dev.jorel.commandapi.CommandAPICommand;
import org.simpmc.lovelypay.commands.sub.admin.*;

public class LovelyPayAdminCommand {
    public LovelyPayAdminCommand() {
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

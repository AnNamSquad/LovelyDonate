package org.lovelysmp.lovelypay.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.commands.root.BankingCommand;
import org.lovelysmp.lovelypay.commands.root.LovelyPayCommand;
import org.lovelysmp.lovelypay.commands.root.NaptheCommand;
import org.lovelysmp.lovelypay.commands.root.NaptheNhanhCommand;
import org.lovelysmp.lovelypay.commands.root.ViewHistoryCommand;
import org.lovelysmp.lovelypay.commands.root.admin.ManualChargeCommand;
import org.lovelysmp.lovelypay.commands.root.admin.LovelyPayAdminCommand;

public class CommandHandler {
    private final LPPlugin plugin;

    public CommandHandler(LPPlugin plugin) {
        this.plugin = plugin;
    }
    public boolean enabled;

    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIPaperConfig(plugin).silentLogs(true));
    }

    public void onEnable() {
        enabled = true;
        CommandAPI.onEnable();
        new LovelyPayCommand();
        new ManualChargeCommand();
        new LovelyPayAdminCommand();
        new BankingCommand();
        new NaptheNhanhCommand();
        new NaptheCommand();
        new ViewHistoryCommand();
    }

    public void onDisable() {
        enabled = false;
        CommandAPI.onDisable();
    }

}

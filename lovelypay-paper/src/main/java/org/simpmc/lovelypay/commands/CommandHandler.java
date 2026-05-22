package org.simpmc.lovelypay.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.commands.root.BankingCommand;
import org.simpmc.lovelypay.commands.root.NaptheCommand;
import org.simpmc.lovelypay.commands.root.NaptheNhanhCommand;
import org.simpmc.lovelypay.commands.root.ViewHistoryCommand;
import org.simpmc.lovelypay.commands.root.admin.ManualChargeCommand;
import org.simpmc.lovelypay.commands.root.admin.LovelyPayAdminCommand;

public class CommandHandler {
    private final LPPlugin plugin;

    public CommandHandler(LPPlugin plugin) {
        this.plugin = plugin;
    }
    public boolean enabled;

    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(plugin).shouldHookPaperReload(true).silentLogs(true));
    }

    public void onEnable() {
        enabled = true;
        CommandAPI.onEnable();
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

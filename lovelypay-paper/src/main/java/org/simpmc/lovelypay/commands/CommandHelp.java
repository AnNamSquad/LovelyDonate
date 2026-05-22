package org.simpmc.lovelypay.commands;

import org.bukkit.command.CommandSender;

public final class CommandHelp {
    private static final String[] PLAYER_HELP = {
            "/lovelypay help - Show this help menu.",
            "/napthe - Open the card recharge menu.",
            "/napthenhanh <serial> <pin> <amount> <telco> - Submit a card quickly.",
            "/banking <amount> - Create a banking payment.",
            "/banking cancel - Cancel your current banking payment.",
            "/lichsunapthe - View your payment history."
    };

    private static final String[] ADMIN_HELP = {
            "/lovelypayadmin help - Show this help menu.",
            "/lovelypayadmin reload - Reload plugin configs and payment handlers.",
            "/lovelypayadmin viewhistory [player] - View server or player payment history.",
            "/lovelypayadmin fakebank <amount> - Trigger a test banking payment for yourself.",
            "/lovelypayadmin fakecard <wrongPrice> - Trigger a test card payment for yourself.",
            "/lovelypayadmin deleteplayer <player> - Delete a player's payment log.",
            "/lovelypayadmin reloadservermilestone - Reload server milestones.",
            "/lovelypayadmin reloadplayermilestone <player> - Reload a player's milestones.",
            "/napthucong <player> <amount> - Manually charge a player."
    };

    private CommandHelp() {
    }

    public static void sendLovelyPayHelp(CommandSender sender) {
        sendHelp(sender, "LovelyPay commands", PLAYER_HELP);
    }

    public static void sendAdminHelp(CommandSender sender) {
        sendHelp(sender, "LovelyPay admin commands", ADMIN_HELP);
    }

    private static void sendHelp(CommandSender sender, String title, String[] lines) {
        sender.sendMessage(title + ":");
        for (String line : lines) {
            sender.sendMessage("  " + line);
        }
    }
}

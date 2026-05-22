package org.lovelysmp.lovelypay.commands.root;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.commands.sub.banking.CancelCommand;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.BankingConfig;
import org.lovelysmp.lovelypay.config.types.CoinsConfig;
import org.lovelysmp.lovelypay.config.types.MessageConfig;
import org.lovelysmp.lovelypay.data.PaymentStatus;
import org.lovelysmp.lovelypay.model.Payment;
import org.lovelysmp.lovelypay.model.detail.BankingDetail;
import org.lovelysmp.lovelypay.model.detail.PaymentDetail;
import org.lovelysmp.lovelypay.service.PaymentService;
import org.lovelysmp.lovelypay.util.MessageUtil;
import org.lovelysmp.lovelypay.util.SoundUtil;
import org.lovelysmp.lovelypay.util.qrcode.MapQR;

import java.math.BigDecimal;
import java.util.UUID;

@SuppressWarnings("unboxing") // ignore unboxing create NPE
public class BankingCommand {
    public BankingCommand() {
        new CommandAPICommand("banking")
                .withPermission(CommandPermission.NONE)
                .withAliases("bank")
                .withSubcommands(
                        CancelCommand.commandCreate()
                )
                .withOptionalArguments(
                        new LongArgument("amount")
                )
                .executesPlayer(this::execute)
                .register();
    }

    private void execute(Player player, CommandArguments args) {
        Long amount = (Long) args.getOptional("amount").orElse(null);
        if (amount == null) {
            sendUsage(player);
            return;
        }

        // start a new banking session
        MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);
        BankingConfig bankingConfig = ConfigManager.getInstance().getConfig(BankingConfig.class);
        // check min amount
        if (amount < bankingConfig.minBanking) {
            MessageUtil.sendMessage(player, messageConfig.invalidAmount.replace("{amount}", String.valueOf(bankingConfig.minBanking)));
            SoundUtil.sendSound(player, messageConfig.soundEffect.get(PaymentStatus.FAILED).toSound());
            return;
        }
        // amount must be divisible by 1000
        if (amount % 1000 != 0) {
            MessageUtil.sendMessage(player, messageConfig.mustDivisibleBy1000);
            SoundUtil.sendSound(player, messageConfig.soundEffect.get(PaymentStatus.FAILED).toSound());
            return;
        }
        if (LPPlugin.getService(PaymentService.class).getPlayerBankingSessionPayment().containsKey(player.getUniqueId())) {
            // resend qr map if player is in banking session
            MessageUtil.sendMessage(player, messageConfig.existBankingSession);
            byte[] qrMap = LPPlugin.getService(PaymentService.class).getPlayerBankQRCode().get(player.getUniqueId());
            MapQR.sendPacketQRMap(qrMap, player);
            return;
        }
        UUID uuid = UUID.randomUUID(); // payment uuid is randomized

        PaymentDetail detail = BankingDetail.builder()
                .amount(amount)
                .build();

        Payment payment = new Payment(uuid, player.getUniqueId(), detail);

        PaymentStatus status = LPPlugin.getService(PaymentService.class).sendBank(payment);
        if (status == PaymentStatus.EXIST) {
            MessageUtil.sendMessage(player, messageConfig.unknownErrror);
            SoundUtil.sendSound(player, messageConfig.soundEffect.get(PaymentStatus.PENDING).toSound());
            return;
        }
        if (status == PaymentStatus.FAILED) {
            MessageUtil.sendMessage(player, messageConfig.failedCard);
            SoundUtil.sendSound(player, messageConfig.soundEffect.get(PaymentStatus.FAILED).toSound());
            return;
        }
        LPPlugin.getService(PaymentService.class).getPlayerBankingSessionPayment().put(player.getUniqueId(), uuid);
    }

    private void sendUsage(Player player) {
        CoinsConfig coinsConfig = ConfigManager.getInstance().getConfig(CoinsConfig.class);
        double bankRate = coinsConfig.baseBankRate + coinsConfig.extraBankRate + coinsConfig.getPromoRate();
        String formattedRate = BigDecimal.valueOf(bankRate).stripTrailingZeros().toPlainString();

        MessageUtil.sendMessage(player, "<color:#E7EE88>Cú pháp lệnh: <white>/bank <số tiền><color:#E7EE88> hoặc <white>/bank cancel<color:#E7EE88> - Hủy giao dịch ngân hàng đang chờ");
        MessageUtil.sendMessage(player, "<color:#E7EE88>Thông tin: Khi nạp qua ngân hàng, cứ mỗi <white>1000VNĐ<color:#E7EE88> sẽ nhận được <white>" + formattedRate + " xu<color:#E7EE88>.");
    }
}

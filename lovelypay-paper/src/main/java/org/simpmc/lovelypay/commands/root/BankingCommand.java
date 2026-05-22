package org.simpmc.lovelypay.commands.root;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.LongArgument;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.commands.sub.banking.CancelCommand;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.BankingConfig;
import org.simpmc.lovelypay.config.types.MessageConfig;
import org.simpmc.lovelypay.data.PaymentStatus;
import org.simpmc.lovelypay.model.Payment;
import org.simpmc.lovelypay.model.detail.BankingDetail;
import org.simpmc.lovelypay.model.detail.PaymentDetail;
import org.simpmc.lovelypay.service.PaymentService;
import org.simpmc.lovelypay.util.MessageUtil;
import org.simpmc.lovelypay.util.SoundUtil;
import org.simpmc.lovelypay.util.qrcode.MapQR;

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
                .withArguments(
                        new LongArgument("amount")
                )
                .executesPlayer((player, args) -> {
                    // start a new banking session
                    MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);
                    BankingConfig bankingConfig = ConfigManager.getInstance().getConfig(BankingConfig.class);
                    // check min amount
                    if ((long) args.get("amount") < bankingConfig.minBanking) {
                        MessageUtil.sendMessage(player, messageConfig.invalidAmount.replace("{amount}", String.valueOf(bankingConfig.minBanking)));
                        SoundUtil.sendSound(player, messageConfig.soundEffect.get(PaymentStatus.FAILED).toSound());
                        return;
                    }
                    // amount must be diviable by 1000
                    if ((Long) args.get("amount") % 1000 != 0) {
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
                            .amount((Long) args.get("amount"))
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

                })
                .register();
    }
}

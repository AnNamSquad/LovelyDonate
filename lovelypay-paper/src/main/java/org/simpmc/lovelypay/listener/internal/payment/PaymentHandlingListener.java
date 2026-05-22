package org.simpmc.lovelypay.listener.internal.payment;

import io.papermc.paper.util.Tick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.BankingConfig;
import org.simpmc.lovelypay.config.types.MainConfig;
import org.simpmc.lovelypay.config.types.MessageConfig;
import org.simpmc.lovelypay.data.PaymentStatus;
import org.simpmc.lovelypay.data.PaymentType;
import org.simpmc.lovelypay.event.PaymentFailedEvent;
import org.simpmc.lovelypay.event.PaymentQueueSuccessEvent;
import org.simpmc.lovelypay.event.PaymentSuccessEvent;
import org.simpmc.lovelypay.model.PaymentResult;
import org.simpmc.lovelypay.model.detail.CardDetail;
import org.simpmc.lovelypay.service.PaymentService;
import org.simpmc.lovelypay.util.MessageUtil;
import org.simpmc.lovelypay.util.SoundUtil;

import java.time.Duration;

public class PaymentHandlingListener implements Listener {
    public PaymentHandlingListener(LPPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFailedPayment(PaymentFailedEvent event) {
        MessageConfig messageConfig = ConfigManager.getInstance().getConfig(MessageConfig.class);
        Player player = Bukkit.getPlayer(event.getPlayerUUID());
        MessageUtil.sendMessage(player, messageConfig.failedCard);
        SoundUtil.sendSound(player, messageConfig.soundEffect.get(PaymentStatus.FAILED).toSound());
    }

    @EventHandler
    public void paymentQueue(PaymentQueueSuccessEvent event) {

        if (event.getPaymentType() == PaymentType.CARD) {
            addTaskChecking(event);
        }
        if (event.getPaymentType() == PaymentType.BANKING) {
            addTaskChecking(event);
        }
    }


    private void addTaskChecking(PaymentQueueSuccessEvent event) {
        LPPlugin plugin = LPPlugin.getInstance();
        long interval = ConfigManager.getInstance().getConfig(MainConfig.class).intervalApiCall;
        int bankingTimeout = ConfigManager.getInstance().getConfig(BankingConfig.class).bankingTimeout;
        plugin.getFoliaLib().getScheduler().runTimerAsync(task -> {
            // self expires after 5 minutes
            if (System.currentTimeMillis() < event.getPayment().getCreatedAt().getTime() + Duration.ofMinutes(bankingTimeout).toMillis()) {
                // if payment is created less than 5 minutes ago, continue checking
            } else {
                // if payment is created more than 5 minutes ago, cancel the task
                MessageUtil.debug("[Payment-Poller] Payment created more than 5 minutes ago, cancelling task");
                callEventSync(new PaymentFailedEvent(event.getPayment()));
                LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                task.cancel();
                return;
            }

            // check if payment is still in pollingPayments
            if (!LPPlugin.getService(PaymentService.class).getPollingPayments().containsKey(event.getPaymentID())) {
                MessageUtil.debug("[Payment-Poller] Payment is not in pollingPayments, cancelling task");
                task.cancel();
                return;
            }
            // check card status
            PaymentStatus status = null;
            PaymentResult result = null;
            if (event.getPaymentType() == PaymentType.CARD) {
                result = LPPlugin.getService(PaymentService.class).getHandlerRegistry().getCardHandler().getTransactionResult(event.getPaymentDetail());
                status = LPPlugin.getService(PaymentService.class).getHandlerRegistry().getCardHandler().getTransactionResult(event.getPaymentDetail()).getStatus();
            }
            if (event.getPaymentType() == PaymentType.BANKING) {
                // check banking status
                result = LPPlugin.getService(PaymentService.class).getHandlerRegistry().getBankHandler().getTransactionResult(event.getPaymentDetail());
                status = LPPlugin.getService(PaymentService.class).getHandlerRegistry().getBankHandler().getTransactionResult(event.getPaymentDetail()).getStatus();
            }

            switch (status) {
                case SUCCESS -> {
                    // handle success
                    // TODO: get actual amount then set into trueamount for card, the true amount should be given in the returned json
                    callEventSync(new PaymentSuccessEvent(event.getPayment()));
                    LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                    task.cancel();
                }
                case FAILED -> {
                    // handle failed
                    callEventSync(new PaymentFailedEvent(event.getPayment()));
                    LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                    if (event.getPaymentType() == PaymentType.BANKING) {
                        LPPlugin.getService(PaymentService.class).cancelBankPayment(event.getPayment().getPlayerUUID());
                    }
                    task.cancel();
                }
                case PENDING -> {
                    // do nothing, wait for next check
                }
                case WRONG_PRICE -> {
                    // handle invalid
                    CardDetail detail = (CardDetail) event.getPaymentDetail().setAmount(result.getAmount());
                    callEventSync(new PaymentSuccessEvent(event.getPayment().setDetail(detail), true));
                    LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                    task.cancel();
                }
                case null -> {
                    callEventSync(new PaymentFailedEvent(event.getPayment()));
                    LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                    MessageUtil.debug("[Payment-Poller] Payment status is null");
                    task.cancel();
                }

                // Bank Zone
                case INVALID -> {
                    callEventSync(new PaymentFailedEvent(event.getPayment()));
                    LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                    task.cancel();
                }
                case EXIST -> {
                    callEventSync(new PaymentFailedEvent(event.getPayment()));
                    MessageUtil.debug("[Payment-Poller] Payment alrady exist on payment api");
                    LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                    task.cancel();
                }
                case EXPIRED -> {
                    callEventSync(new PaymentFailedEvent(event.getPayment()));
                    MessageUtil.debug("[Payment-Poller] Payment expired on payment api");
                    LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                    task.cancel();
                }
                case CANCELLED -> {
                    callEventSync(new PaymentFailedEvent(event.getPayment()));
                    MessageUtil.debug("[Payment-Poller] Payment cancelled on payment api");
                    LPPlugin.getService(PaymentService.class).getPollingPayments().remove(event.getPayment().getPaymentID());
                    task.cancel();
                }
            }
        }, 1L, Tick.tick().fromDuration(Duration.ofSeconds(interval)));

        LPPlugin.getService(PaymentService.class).getPollingPayments().putIfAbsent(event.getPaymentID(), event.getPayment());


    }

    private void callEventSync(Event event) {
        LPPlugin plugin = LPPlugin.getInstance();
        plugin.getFoliaLib().getScheduler().runNextTick(wrappedTask -> Bukkit.getPluginManager().callEvent(event));
    }


}

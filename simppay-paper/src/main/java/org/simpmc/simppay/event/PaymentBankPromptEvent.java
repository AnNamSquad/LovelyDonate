package org.simpmc.simppay.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.simpmc.simppay.handler.banking.data.BankingData;

import java.util.UUID;

@Getter
public class PaymentBankPromptEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private final UUID playerUUID;
    private final BankingData bankingData;

    public PaymentBankPromptEvent(BankingData payment) {
        this.playerUUID = payment.getPlayerUUID();
        this.bankingData = payment;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}

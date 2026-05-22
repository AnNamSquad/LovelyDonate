package org.simpmc.simppay.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class PlayerMilestoneEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public final UUID uuid;

    public PlayerMilestoneEvent(UUID uuid) {
        this.uuid = uuid;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}

package org.simpmc.simppay.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.simpmc.simppay.config.types.data.MilestoneConfig;
import org.simpmc.simppay.data.milestone.MilestoneType;

import java.util.UUID;

@Getter
public class MilestoneCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final UUID playerUUID; // null for server milestones
    private final MilestoneType type;
    private final MilestoneConfig milestoneConfig;
    private final long currentAmount;

    public MilestoneCompleteEvent(UUID playerUUID, MilestoneType type, MilestoneConfig milestoneConfig, long currentAmount) {
        this.playerUUID = playerUUID;
        this.type = type;
        this.milestoneConfig = milestoneConfig;
        this.currentAmount = currentAmount;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}

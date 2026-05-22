package org.simpmc.simppay.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@DatabaseTable(tableName = "players")
@NoArgsConstructor
public class SPPlayer {
    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;

    public SPPlayer(Player bukkitPlayer) {
        this.uuid = bukkitPlayer.getUniqueId();
        this.name = bukkitPlayer.getName();
    }
}

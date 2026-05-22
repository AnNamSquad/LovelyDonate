package org.simpmc.simppay.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@DatabaseTable(tableName = "player_data")
@Data
@NoArgsConstructor
public class PlayerData {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(columnName = "player_uuid", foreign = true, foreignAutoRefresh = true, canBeNull = false, unique = true)
    public SPPlayer player;
    @DatabaseField(columnName = "key", canBeNull = false)
    public String key;
    @DatabaseField(columnName = "value", canBeNull = false)
    public String value;
}

package org.simpmc.simppay.service.database;

import com.j256.ormlite.dao.Dao;
import org.simpmc.simppay.database.entities.PlayerData;
import org.simpmc.simppay.database.entities.SPPlayer;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerDataService {

    private final Dao<PlayerData, UUID> playerDataDao;

    /**
     * @param playerDataDao the ORMLite DAO for the SPPlayer entity
     */
    public PlayerDataService(Dao<PlayerData, UUID> playerDataDao) {
        this.playerDataDao = playerDataDao;
    }

    //    @DatabaseField(columnName = "player_uuid", foreign = true, foreignAutoRefresh = true, canBeNull = false, unique = true)
    //    public SPPlayer player;
    //    @DatabaseField(columnName = "key", canBeNull = false)
    //    public String key;
    //    @DatabaseField(columnName = "value", canBeNull = false)
    //    public String value;

    // create key value, pass in a SPPlayer object
    public void upsertKeyValue(SPPlayer player, String key, String value) throws SQLException {
        PlayerData playerData = new PlayerData();
        playerData.setPlayer(player);
        playerData.setKey(key);
        playerData.setValue(value);

        // Check if the entry already exists
        PlayerData existingEntry = playerDataDao.queryBuilder()
                .where()
                .eq("player_uuid", player.getUuid())
                .and()
                .eq("key", key)
                .queryForFirst();

        if (existingEntry != null) {
            // Update the existing entry
            existingEntry.setValue(value);
            playerDataDao.update(existingEntry);
        } else {
            // Insert a new entry
            playerDataDao.create(playerData);
        }
    }

    public String getValue(SPPlayer player, String key) throws SQLException {
        PlayerData playerData = playerDataDao.queryBuilder()
                .where()
                .eq("player_uuid", player.getUuid())
                .and()
                .eq("key", key)
                .queryForFirst();
        if (playerData == null) {
            return null;
        } else {
            return playerData.getValue();
        }
    }
}


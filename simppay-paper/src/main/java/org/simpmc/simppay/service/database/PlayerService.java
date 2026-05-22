package org.simpmc.simppay.service.database;

import com.j256.ormlite.dao.Dao;
import org.bukkit.entity.Player;
import org.simpmc.simppay.SPPlugin;
import org.simpmc.simppay.database.entities.SPPlayer;
import org.simpmc.simppay.service.DatabaseService;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerService {

    private final Dao<SPPlayer, UUID> playerDao;

    /**
     * @param playerDao the ORMLite DAO for the SPPlayer entity
     */
    public PlayerService(Dao<SPPlayer, UUID> playerDao) {
        this.playerDao = playerDao;
    }

    /**
     * Finds a SPPlayer entity by UUID (stored as String in the database).
     *
     * @param uuid the player's UUID in string form
     * @return the matching SPPlayer entity, or null if not found
     */
    public SPPlayer findByUuid(UUID uuid) {
        try {
            return playerDao.queryForId(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Finds a SPPlayer entity by name.
     *
     * @param name the name of the player to find
     * @return the matching SPPlayer entity, or null if not found
     */
    public SPPlayer findByName(String name) {
        try {
            return playerDao.queryBuilder().where().eq("name", name).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if a SPPlayer entity exists for the given player.
     *
     * @param player the player interface from your platform package
     * @return true if the SPPlayer entity exists, false otherwise
     */
    public boolean exists(Player player) {
        return findByUuid(player.getUniqueId()) != null;
    }

    /**
     * Creates a new SPPlayer entity in the database if it doesn't already exist.
     * If the SPPlayer already exists, it will update the stored name.
     *
     * @param player the player interface (contains UUID, name, etc.)
     */
    public void createPlayer(Player player) {
        try {
            if (!exists(player)) {
                playerDao.create(new SPPlayer(player));
            } else {
                updatePlayerName(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the stored name of an existing SPPlayer if they already exist.
     *
     * @param player the player whose name should be updated
     */
    public void updatePlayerName(Player player) {
        try {
            SPPlayer existing = findByUuid(player.getUniqueId());
            if (existing != null) {
                existing.setName(player.getName());
                playerDao.update(existing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a SPPlayer entity from the database.
     *
     * @param player the player to remove
     */
    public void deletePlayer(Player player) {
        try {
            SPPlayer existing = findByUuid(player.getUniqueId());
            if (existing != null) {
                playerDao.delete(existing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //             if (spPlayer != null && !spPlayer.isFirstCharge()) {
    //                plugin.getPlayerService().setFirstCharge(spPlayer);
    //             }
    public void setFirstCharge(SPPlayer spPlayer) {
        try {
            // check if key is true
            SPPlugin.getService(DatabaseService.class).getPlayerDataService().upsertKeyValue(spPlayer, "first_charge", "true");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasFirstCharge(SPPlayer spPlayer) {
        try {
            // check if key is true
            String value = SPPlugin.getService(DatabaseService.class).getPlayerDataService().getValue(spPlayer, "first_charge");
            if (value != null && value.equals("true")) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


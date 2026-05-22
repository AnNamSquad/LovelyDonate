package org.simpmc.simppay.service;

import lombok.Getter;
import org.simpmc.simppay.database.Database;
import org.simpmc.simppay.service.database.PaymentLogService;
import org.simpmc.simppay.service.database.PlayerDataService;
import org.simpmc.simppay.service.database.PlayerService;

@Getter
public class DatabaseService implements IService {
    private final Database database;
    private PlayerService playerService;
    private PaymentLogService paymentLogService;
    private PlayerDataService playerDataService;

    public DatabaseService(Database database) {
        this.database = database;
    }

    @Override
    public void setup() {
        playerService = new PlayerService(database.getPlayerDao());
        paymentLogService = new PaymentLogService(database);
        playerDataService = new PlayerDataService(database.getPlayerDataDao());
    }

    @Override
    public void shutdown() {

    }
}

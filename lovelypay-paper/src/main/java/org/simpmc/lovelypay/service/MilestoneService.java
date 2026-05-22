package org.simpmc.lovelypay.service;

import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.MocNapConfig;
import org.simpmc.lovelypay.config.types.MocNapServerConfig;
import org.simpmc.lovelypay.config.types.data.BossBarConfig;
import org.simpmc.lovelypay.config.types.data.MilestoneConfig;
import org.simpmc.lovelypay.data.milestone.MilestoneType;
import org.simpmc.lovelypay.database.entities.SPPlayer;
import org.simpmc.lovelypay.service.database.PaymentLogService;
import org.simpmc.lovelypay.util.MessageUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MilestoneService implements IService {
    public ConcurrentHashMap<UUID, List<ObjectObjectMutablePair<MilestoneConfig, BossBar>>> playerBossBars = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, List<MilestoneConfig>> playerCurrentMilestones = new ConcurrentHashMap<>();
    public List<MilestoneConfig> serverCurrentMilestones = new ArrayList<>();
    public List<ObjectObjectMutablePair<MilestoneConfig, BossBar>> serverBossbars = new ArrayList<>();
    private final AtomicInteger serverMilestoneLoadVersion = new AtomicInteger();


    @Override
    public void setup() {
        playerCurrentMilestones.clear();
        playerBossBars.clear();
        serverCurrentMilestones.clear();
        serverBossbars.clear();
        loadServerMilestone();
        for (Player p : Bukkit.getOnlinePlayers()) { // thread-safe for folia
            loadPlayerMilestone(p.getUniqueId());
        }
    }

    @Override
    public void shutdown() {
        serverMilestoneLoadVersion.incrementAndGet();
        playerCurrentMilestones.clear();
        playerBossBars.clear();
        serverCurrentMilestones.clear();
        serverBossbars.clear();
    }

    // all milestones should be reloaded upon a milestone complete event
    public void loadServerMilestone() {
        int loadVersion = serverMilestoneLoadVersion.incrementAndGet();
        clearServerMilestones();
        LPPlugin.getInstance().getFoliaLib().getScheduler().runAsync(task -> {
            PaymentLogService paymentLogService = LPPlugin.getService(DatabaseService.class).getPaymentLogService();
            MocNapServerConfig mocNapServerConfig = ConfigManager.getInstance().getConfig(MocNapServerConfig.class);
            List<MilestoneConfig> loadedMilestones = new ArrayList<>();
            List<ObjectObjectMutablePair<MilestoneConfig, BossBar>> loadedBossBars = new ArrayList<>();

            for (Map.Entry<MilestoneType, List<MilestoneConfig>> entry : mocNapServerConfig.mocnap.entrySet()) {
                MilestoneType type = entry.getKey();
                if (type == null) {
                    continue;
                }
                MessageUtil.debug("Loading MocNap Server " + type.name());
                double serverBal = getServerAmount(paymentLogService, type);
                List<MilestoneConfig> pendingMilestones = entry.getValue().stream()
                        .filter(config -> config != null && config.amount > serverBal)
                        .sorted(Comparator.comparingInt(config -> config.amount))
                        .toList();

                for (MilestoneConfig config : pendingMilestones) {
                    if (config.type != type) {
                        config.setType(type); // auto correct
                    }
                    MessageUtil.debug("Loading MocNap Server " + type.name() + " " + config.amount);
                    loadedMilestones.add(config);
                    MessageUtil.debug("Loaded MocNap Server Entry For Player " + type.name() + " " + config.amount);
                }

                if (!pendingMilestones.isEmpty()) {
                    MilestoneConfig config = pendingMilestones.getFirst();
                    BossBarConfig bossBarConfig = config.bossbar;
                    if (bossBarConfig != null && bossBarConfig.enabled) {
                        BossBar bossBar = BossBar.bossBar(
                                MessageUtil.getComponentParsed(bossBarConfig.getTitle(), null),
                                getBossBarProgress(serverBal, config.amount),
                                bossBarConfig.color,
                                bossBarConfig.style
                        );
                        loadedBossBars.add(new ObjectObjectMutablePair<>(config, bossBar));
                        MessageUtil.debug("Loaded MocNap Server BossBar " + type.name() + " " + config.amount);
                    }
                }
            }

            if (loadVersion != serverMilestoneLoadVersion.get()) {
                return;
            }

            serverCurrentMilestones.addAll(loadedMilestones);
            serverBossbars.addAll(loadedBossBars);
            addServerBossBarViewers(loadedBossBars.stream().map(ObjectObjectMutablePair::right).toList());
        });
    }

    public void showServerBossBars(Player player) {
        List<BossBar> bossBars = serverBossbars.stream().map(ObjectObjectMutablePair::right).toList();
        for (BossBar bar : bossBars) {
            LPPlugin.getInstance().getFoliaLib().getScheduler().runAtEntity(player, task -> bar.addViewer(player));
        }
    }

    private void clearServerMilestones() {
        List<BossBar> oldBossBars = serverBossbars.stream().map(ObjectObjectMutablePair::right).toList();
        serverCurrentMilestones.clear();
        serverBossbars.clear();
        removeServerBossBarViewers(oldBossBars);
    }

    private void addServerBossBarViewers(Collection<BossBar> bossBars) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (BossBar bar : bossBars) {
                LPPlugin.getInstance().getFoliaLib().getScheduler().runAtEntity(player, task -> bar.addViewer(player));
            }
        }
    }

    private void removeServerBossBarViewers(Collection<BossBar> bossBars) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (BossBar bar : bossBars) {
                LPPlugin.getInstance().getFoliaLib().getScheduler().runAtEntity(player, task -> bar.removeViewer(player));
            }
        }
    }

    private double getServerAmount(PaymentLogService paymentLogService, MilestoneType type) {
        return switch (type) {
            case ALL -> paymentLogService.getEntireServerAmount();
            case DAILY -> paymentLogService.getEntireServerDailyAmount();
            case WEEKLY -> paymentLogService.getEntireServerWeeklyAmount();
            case MONTHLY -> paymentLogService.getEntireServerMonthlyAmount();
            case YEARLY -> paymentLogService.getEntireServerYearlyAmount();
        };
    }

    private float getBossBarProgress(double currentAmount, int milestoneAmount) {
        if (milestoneAmount <= 0) {
            return 1.0F;
        }
        double progress = currentAmount / milestoneAmount;
        return (float) Math.max(0.0D, Math.min(1.0D, progress));
    }

    public void loadPlayerMilestone(UUID uuid) {
        playerCurrentMilestones.remove(uuid);
        playerBossBars.remove(uuid);
        LPPlugin.getInstance().getFoliaLib().getScheduler().runAsync(task -> {
            PaymentLogService paymentLogService = LPPlugin.getService(DatabaseService.class).getPaymentLogService();

            SPPlayer player = LPPlugin.getService(DatabaseService.class).getPlayerService().findByUuid(uuid);
            double playerChargedAmount = LPPlugin.getService(DatabaseService.class).getPaymentLogService().getPlayerTotalAmount(player);

            MocNapConfig mocNapConfig = ConfigManager.getInstance().getConfig(MocNapConfig.class);
            MessageUtil.debug("Loading MocNap For Player " + player.getName());
            playerBossBars.putIfAbsent(uuid, new ArrayList<>());
            playerCurrentMilestones.putIfAbsent(uuid, new ArrayList<>());
            for (Map.Entry<MilestoneType, List<MilestoneConfig>> entry : mocNapConfig.mocnap.entrySet()) {
                MilestoneType type = entry.getKey();
                if (type == null) {
                    continue;
                }
                MessageUtil.debug("Loading MocNap Entry For Player " + type.name());

                for (MilestoneConfig config : entry.getValue()) {
                    if (config.amount <= playerChargedAmount) {
                        continue;
                    }
                    if (config.type != type) {
                        config.setType(type); // auto correct
                    }
                    MessageUtil.debug("Loading MocNap Entry For Player " + type.name() + " " + config.amount);
                    BossBarConfig bossBarConfig = config.bossbar;
                    double playerBal = switch (config.getType()) {
                        case MilestoneType.ALL -> paymentLogService.getPlayerTotalAmount(player);
                        case MilestoneType.DAILY -> paymentLogService.getPlayerDailyAmount(player);
                        case MilestoneType.WEEKLY -> paymentLogService.getPlayerWeeklyAmount(player);
                        case MilestoneType.MONTHLY -> paymentLogService.getPlayerMonthlyAmount(player);
                        case MilestoneType.YEARLY -> paymentLogService.getPlayerYearlyAmount(player);
                        default -> throw new IllegalStateException("Unexpected value: " + config.getType());
                    };
                    if (config.bossbar.enabled) {
                        BossBar bossBar = BossBar.bossBar(
                                MessageUtil.getComponentParsed(bossBarConfig.getTitle(), null), // bossbar title will be loaded after
                                (float) (playerBal / config.amount),
                                config.bossbar.color,
                                config.bossbar.style
                        );
                        playerBossBars.get(uuid).add(new ObjectObjectMutablePair<>(config, bossBar));
                        MessageUtil.debug("Loaded MocNap BossBar For Player " + type.name() + " " + config.amount);
                    }
                    playerCurrentMilestones.get(uuid).add(config);
                    MessageUtil.debug("Loaded MocNap Entry For Player " + type.name() + " " + config.amount);

                }

            }
        });


    }

}

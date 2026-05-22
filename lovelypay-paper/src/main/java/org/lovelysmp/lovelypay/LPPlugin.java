package org.lovelysmp.lovelypay;

import com.github.retrooper.packetevents.PacketEvents;
import com.tcoded.folialib.FoliaLib;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.devnatan.inventoryframework.ViewFrame;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.lovelysmp.lovelypay.api.DatabaseSettings;
import org.lovelysmp.lovelypay.commands.CommandHandler;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.DatabaseConfig;
import org.lovelysmp.lovelypay.database.Database;
import org.lovelysmp.lovelypay.hook.HookManager;
import org.lovelysmp.lovelypay.listener.internal.cache.CacheUpdaterListener;
import org.lovelysmp.lovelypay.listener.internal.milestone.MilestoneListener;
import org.lovelysmp.lovelypay.listener.internal.payment.PaymentHandlingListener;
import org.lovelysmp.lovelypay.listener.internal.player.BankPromptListener;
import org.lovelysmp.lovelypay.listener.internal.player.NaplandauListener;
import org.lovelysmp.lovelypay.listener.internal.player.SuccessHandlingListener;
import org.lovelysmp.lovelypay.listener.internal.player.database.SuccessDatabaseHandlingListener;
import org.lovelysmp.lovelypay.menu.PaymentHistoryView;
import org.lovelysmp.lovelypay.menu.ServerPaymentHistoryView;
import org.lovelysmp.lovelypay.menu.card.CardListView;
import org.lovelysmp.lovelypay.menu.card.CardPriceView;
import org.lovelysmp.lovelypay.service.*;
import org.lovelysmp.lovelypay.service.cache.CacheDataService;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public final class LPPlugin extends JavaPlugin {

    @Getter
    private static LPPlugin instance;
    private final List<IService> services = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private ConfigManager configManager;
    @Getter
    private FoliaLib foliaLib;
    @Getter
    private CommandHandler commandHandler;
    @Getter
    private ViewFrame viewFrame;
    @Getter
    private boolean floodgateEnabled;
    private Metrics metrics;

    public static @NotNull <T extends IService> T getService(Class<T> clazz) {
        for (var service : instance.getServices())
            if (clazz.isAssignableFrom(service.getClass())) {
                return clazz.cast(service);
            }

        instance.getLogger().severe("Service " + clazz.getName() + " not instantiated. Did you forget to create it?");
        throw new RuntimeException("Service " + clazz.getName() + " not instantiated?");
    }


    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        commandHandler = new CommandHandler(this);
        commandHandler.onLoad();
    }

    @Override
    public void onEnable() {
        instance = this;
        services.clear();
        floodgateEnabled = false;
        foliaLib = new FoliaLib(this);

        // Reset config
        PacketEvents.getAPI().init();
        registerMetrics();
        if (getServer().getPluginManager().getPlugin("floodgate") != null) {
            floodgateEnabled = true;
            getLogger().info("Enabled floodgate support");
        }
        // Plugin startup logic
        configManager = new ConfigManager(this);

        Database database = null;
        try {
            DatabaseSettings databaseConf = ConfigManager.getInstance().getConfig(DatabaseConfig.class);
            database = new Database(databaseConf);
        } catch (RuntimeException | SQLException e) {
            getLogger().warning("LovelyPay failed to connect to database");
            this.getServer().getPluginManager().disablePlugin(this);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        services.add(new OrderIDService());
        services.add(new CacheDataService());
        services.add(new DatabaseService(database));
        services.add(new PaymentService());
        services.add(new MilestoneService());

        registerServices();

        new HookManager(this);
        registerListener();
        commandHandler.onEnable();
        registerInventoryFramework();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (viewFrame != null) {
            try {
                viewFrame.unregister();
            } catch (Exception e) {
                getLogger().warning("Failed to unregister inventory views");
                e.printStackTrace();
            }
            viewFrame = null;
        }

        HandlerList.unregisterAll(this);

        if (foliaLib != null) {
            try {
                foliaLib.getScheduler().cancelAllTasks();
            } catch (Exception e) {
                getLogger().warning("Failed to cancel scheduled tasks");
                e.printStackTrace();
            }
        }

        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }

        for (var service : new ArrayList<>(services)) {
            try {
                service.shutdown();
            } catch (Exception e) {
                getLogger().severe("Failed to shutdown service: " + service.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        services.clear();
        if (commandHandler != null && commandHandler.enabled) {
            commandHandler.onDisable();
        }
        PacketEvents.getAPI().terminate();
        configManager = null;
        foliaLib = null;
        floodgateEnabled = false;
        instance = null;
    }

    private void registerServices() {
        for (var service : services) {
            service.setup();
            getLogger().info(service.getClass().getSimpleName() + " service successfully enabled!");

            if (service instanceof Listener listener) {
                getServer().getPluginManager().registerEvents(listener, instance);
                getLogger().info(service.getClass().getSimpleName() + " is now listening to events.");
            }
        }
    }

    private void registerListener() {
        Set<Class<? extends Listener>> listeners = Set.of(
                PaymentHandlingListener.class,
                BankPromptListener.class,
                SuccessHandlingListener.class,
                SuccessDatabaseHandlingListener.class,
                CacheUpdaterListener.class,
                MilestoneListener.class,
                NaplandauListener.class
        );

        for (Class<? extends Listener> listener : listeners) {
            try {
                listener.getConstructor(LPPlugin.class).newInstance(this);
            } catch (Exception e) {
                getLogger().warning("Failed to register listener: " + listener.getSimpleName());
                e.printStackTrace();
            }
        }
    }

    public Collection<IService> getServices() {
        return services;
    }

    private void registerInventoryFramework() {
        viewFrame = ViewFrame.create(this)
                .with(
                        new CardListView(),
                        new CardPriceView(),
                        new PaymentHistoryView(),
                        new ServerPaymentHistoryView()
                )
                .disableMetrics()
                .register();
    }

    private void registerMetrics() {
        metrics = new Metrics(this, 25693);
        // check competitors stuff
        File dotManFolder = new File(getDataFolder().getParent(), "DotMan");
        File hmtopupFolder = new File(getDataFolder().getParent(), "HMTopUp");
        metrics.addCustomChart(new Metrics.SimplePie("had_dotman", () -> String.valueOf(dotManFolder.exists())));
        metrics.addCustomChart(new Metrics.SimplePie("had_hmtopup", () -> String.valueOf(hmtopupFolder.exists())));
    }

}

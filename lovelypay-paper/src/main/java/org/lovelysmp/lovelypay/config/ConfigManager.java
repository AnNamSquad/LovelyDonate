package org.lovelysmp.lovelypay.config;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.annotations.Folder;
import org.lovelysmp.lovelypay.config.serializers.KeySerializer;
import org.lovelysmp.lovelypay.config.serializers.SoundComponentSerializer;
import org.lovelysmp.lovelypay.config.types.*;
import org.lovelysmp.lovelypay.config.types.banking.PayosConfig;
import org.lovelysmp.lovelypay.config.types.banking.Web2mConfig;
import org.lovelysmp.lovelypay.config.types.card.*;
import org.lovelysmp.lovelypay.config.types.menu.PaymentHistoryMenuConfig;
import org.lovelysmp.lovelypay.config.types.menu.ServerPaymentHistoryMenuConfig;
import org.lovelysmp.lovelypay.config.types.menu.card.CardListMenuConfig;
import org.lovelysmp.lovelypay.config.types.menu.card.CardPriceMenuConfig;
import org.lovelysmp.lovelypay.config.types.menu.card.dialog.CardPinMenuConfig;
import org.lovelysmp.lovelypay.config.types.menu.card.dialog.CardSerialMenuConfig;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    // holds loaded config instances
    private static final Map<Class<?>, Object> configs = new HashMap<>();
    @Getter
    private static ConfigManager instance;
    private final LPPlugin plugin;
    private final List<Class<?>> configClasses = List.of(
            // TODO: ClassGraph auto scan ?
            PayosConfig.class,
            ThesieutocConfig.class,
            CardPinMenuConfig.class,
            CardSerialMenuConfig.class,
            CardListMenuConfig.class,
            CardPriceMenuConfig.class,
            PaymentHistoryMenuConfig.class,
            BankingConfig.class,
            CardConfig.class,
            CoinsConfig.class,
            DatabaseConfig.class,
            MainConfig.class,
            MessageConfig.class,
            ServerPaymentHistoryMenuConfig.class,
            MocNapConfig.class,
            MocNapServerConfig.class,
            NaplandauConfig.class,
            Gachthe1sConfig.class,
            Card2KConfig.class,
            Web2mConfig.class,
            ThesieureConfig.class,
            Doithe1sConfig.class
    );
    // holds file paths for each config type
    private final Map<Class<?>, Path> configPaths = new HashMap<>();
    private final YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .addSerializer(Key.class, new KeySerializer())
            .addSerializer(Sound.class, new SoundComponentSerializer())
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .build();

    public ConfigManager(LPPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        // build default YAML properties
        // prepare paths and load all
        initPaths();
        registerAll();
    }

    private void initPaths() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        for (Class<?> clazz : configClasses) {
            configPaths.put(clazz, getConfigPath(clazz));
        }
    }

    private Path getConfigPath(Class<?> clazz) {
        String fileName = getConfigFileName(clazz.getSimpleName()) + ".yml";


        if (clazz.isAnnotationPresent(Folder.class)) {
            // if the class is annotated with @Folder, create a subfolder
            String folderName = clazz.getAnnotation(Folder.class).value();
            File folder = new File(plugin.getDataFolder(), folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            return Paths.get(folder.getPath(), fileName);
        }
        return Paths.get(plugin.getDataFolder().getPath(), fileName);
    }


    @SuppressWarnings("unchecked")
    private void registerAll() {
        plugin.getLogger().info("Loading all configurations");
        for (Class<?> rawClass : configClasses) {
            // capture wildcard and process each
            registerConfig((Class<Object>) rawClass);
        }
        plugin.getLogger().info("All configurations loaded successfully");
    }

    private <T> void registerConfig(Class<T> cfgClass) {
        Path path = configPaths.get(cfgClass);

        YamlConfigurationStore<T> store = new YamlConfigurationStore<>(cfgClass, properties);

        store.update(path);

        // load (with properties on first load)
        T loaded = store.load(path);
        configs.put(cfgClass, loaded);
    }

    /**
     * Reload all configs (e.g. on /lovelypayadmin reload)
     */
    public void reloadAll() {
        configs.clear();
        plugin.getLogger().info("Reloading all configurations");
        for (Class<?> rawClass : configClasses) {
            registerConfig(rawClass);
        }
        plugin.getLogger().info("All configurations reloaded successfully");
    }

    /**
     * Retrieve a loaded configuration instance by its class
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig(Class<T> cls) {
        return (T) configs.get(cls);
    }


    private String getConfigFileName(String name) {
        var builder = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) {
                    builder.append('-');
                }
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}

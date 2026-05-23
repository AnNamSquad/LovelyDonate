package org.lovelysmp.lovelypay.service;

import org.lovelysmp.lovelypay.LPPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

public class OrderIDService implements IService {

    // The filename within the plugin data folder
    private final String FILE_NAME = "last_id.txt";
    private static final long MIN_ORDER_CODE = 1_000_000L;

    // Thread-safe counter
    private final AtomicLong counter = new AtomicLong(0);

    // File where the counter is persisted
    private File dataFile;

    /**
     * Call this once from your plugin's onEnable().
     * It will create the data folder/file if needed and load the last saved ID.
     * /**
     * Gets the next unique ID (thread-safe) and immediately persists it.
     *
     * @return the next ID
     */
    public long getNextId() {
        long next = counter.incrementAndGet();
        saveCurrent();
        return next;
    }

    @Override
    public void setup() {
        LPPlugin plugin = LPPlugin.getInstance();
        // Ensure data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        dataFile = new File(plugin.getDataFolder(), FILE_NAME);

        // If file doesn't exist, create it with a zero
        if (!dataFile.exists()) {
            try (Writer w = new OutputStreamWriter(new FileOutputStream(dataFile), StandardCharsets.UTF_8)) {
                w.write("0");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create " + FILE_NAME + ": " + e.getMessage());
            }
        }

        // Load last ID
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), StandardCharsets.UTF_8))) {
            String line = r.readLine();
            long last = line == null || line.isBlank() ? 0 : Long.parseLong(line.trim());
            long timeBasedFloor = Math.max(MIN_ORDER_CODE, System.currentTimeMillis() / 1000L);
            if (last < timeBasedFloor) {
                last = timeBasedFloor;
                LPPlugin.getInstance().getLogger().info("Raised " + FILE_NAME + " to " + last + " to avoid duplicate PayOS order codes.");
            }
            counter.set(last);
            saveCurrent();
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load last ID from " + FILE_NAME + ": " + e.getMessage());
        }
    }

    public void saveCurrent() {
        if (dataFile == null) {
            return;
        }
        try (Writer w = new OutputStreamWriter(new FileOutputStream(dataFile, false), StandardCharsets.UTF_8)) {
            w.write(Long.toString(counter.get()));
        } catch (IOException e) {
            // Best effort: log to console
            System.err.println("Failed to save ID to " + FILE_NAME + ": " + e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        saveCurrent();
    }
}

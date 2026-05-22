package org.simpmc.simppay.hook;


import org.simpmc.simppay.SPPlugin;
import org.simpmc.simppay.hook.hooks.PlaceholderAPIHook;

public class HookManager {

    private final SPPlugin plugin;

    public HookManager(SPPlugin plugin) {
        this.plugin = plugin;
        register();
    }

    private void register() {
        new PlaceholderAPIHook(plugin);
    }
}

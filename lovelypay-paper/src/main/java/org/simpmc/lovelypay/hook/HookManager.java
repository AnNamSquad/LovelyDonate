package org.simpmc.lovelypay.hook;


import org.simpmc.lovelypay.LPPlugin;
import org.simpmc.lovelypay.hook.hooks.PlaceholderAPIHook;

public class HookManager {

    private final LPPlugin plugin;

    public HookManager(LPPlugin plugin) {
        this.plugin = plugin;
        register();
    }

    private void register() {
        new PlaceholderAPIHook(plugin);
    }
}

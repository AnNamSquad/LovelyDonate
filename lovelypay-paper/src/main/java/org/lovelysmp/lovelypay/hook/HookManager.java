package org.lovelysmp.lovelypay.hook;


import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.hook.hooks.PlaceholderAPIHook;

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

package net.chunkful.inventoryrestore;

import net.chunkful.inventoryrestore.command.CommandRegistrar;
import net.chunkful.inventoryrestore.lifecycle.PluginLifecycler;
import org.bukkit.plugin.java.JavaPlugin;

public final class InventoryrestorePlugin extends JavaPlugin {

    private PluginLifecycler pluginLifecycler;

    @Override
    public void onEnable() {
        this.pluginLifecycler = new PluginLifecycler(this);

        new CommandRegistrar(this, pluginLifecycler).registerCommands();

        pluginLifecycler.load();
    }

    @Override
    public void onDisable() {
        pluginLifecycler.unload();
    }
}

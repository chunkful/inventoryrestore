package net.chunkful.inventoryrestore.viewer;

import net.chunkful.inventoryrestore.lifecycle.Lifecycled;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.viewer.view.EnderchestView;
import net.chunkful.inventoryrestore.viewer.view.InventoryView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class InventorySnapshotViewer implements SnapshotViewer, Lifecycled {

    private final Plugin owningPlugin;
    private final InventoryView inventoryView = new InventoryView(this);
    private final EnderchestView enderchestView = new EnderchestView(this);

    public InventorySnapshotViewer(Plugin owningPlugin) {
        this.owningPlugin = owningPlugin;
    }

    @Override
    public void openInventory(Player player, Snapshot snapshot) {
        inventoryView.open(player, snapshot);
    }

    @Override
    public void openEnderchest(Player player, Snapshot snapshot) {
        enderchestView.open(player, snapshot);
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(inventoryView, owningPlugin);
        Bukkit.getPluginManager().registerEvents(enderchestView, owningPlugin);
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(inventoryView);
        HandlerList.unregisterAll(enderchestView);
    }

}

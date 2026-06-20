package net.chunkful.inventoryrestore.viewer;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.entity.Player;

public interface SnapshotViewer {

    void openInventory(Player player, Snapshot snapshot);

    void openEnderchest(Player player, Snapshot snapshot);

}

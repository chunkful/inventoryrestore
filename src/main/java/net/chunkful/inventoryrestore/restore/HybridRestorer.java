package net.chunkful.inventoryrestore.restore;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HybridRestorer implements SnapshotRestorer {

    private final SnapshotRestorer onlineRestorer;
    private final SnapshotRestorer offlineRestorer;

    public HybridRestorer(SnapshotRestorer onlineRestorer, SnapshotRestorer offlineRestorer) {
        this.onlineRestorer = onlineRestorer;
        this.offlineRestorer = offlineRestorer;
    }

    @Override
    public void restoreSnapshot(UUID playerUniqueId, Snapshot snapshot, boolean teleport) {
        Player player = Bukkit.getPlayer(playerUniqueId);
        if (player == null || !player.isOnline()) {
            offlineRestorer.restoreSnapshot(playerUniqueId, snapshot, teleport);
        } else {
            onlineRestorer.restoreSnapshot(playerUniqueId, snapshot, teleport);
        }
    }
}

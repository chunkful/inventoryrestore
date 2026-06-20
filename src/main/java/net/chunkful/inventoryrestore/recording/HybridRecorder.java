package net.chunkful.inventoryrestore.recording;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HybridRecorder implements SnapshotRecorder {

    private final SnapshotRecorder onlineRecorder;
    private final SnapshotRecorder offlineRecorder;

    public HybridRecorder(SnapshotRecorder onlineRecorder, SnapshotRecorder offlineRecorder) {
        this.onlineRecorder = onlineRecorder;
        this.offlineRecorder = offlineRecorder;
    }

    @Override
    public Snapshot recordSnapshot(UUID playerUniqueId, Snapshot.Cause cause) {
        Player player = Bukkit.getPlayer(playerUniqueId);
        if(player == null || !player.isOnline()) {
            return offlineRecorder.recordSnapshot(playerUniqueId, cause);
        }   else    {
            return onlineRecorder.recordSnapshot(playerUniqueId, cause);
        }
    }
}

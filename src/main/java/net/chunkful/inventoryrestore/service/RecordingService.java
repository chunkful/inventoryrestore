package net.chunkful.inventoryrestore.service;

import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.recording.SnapshotRecorder;
import net.chunkful.inventoryrestore.storage.Storage;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RecordingService {

    private final SnapshotRecorder snapshotRecorder;
    private final Storage storage;

    @Inject
    public RecordingService(SnapshotRecorder snapshotRecorder, Storage storage) {
        this.snapshotRecorder = snapshotRecorder;
        this.storage = storage;
    }

    public CompletableFuture<Void> recordAndSave(Player player, Snapshot.Cause cause) {
        return storage.storeSnapshot(snapshotRecorder.recordSnapshot(player.getUniqueId(), cause));
    }

    public CompletableFuture<Void> recordAndSave(UUID playerUniqueId, Snapshot.Cause cause) {
        Snapshot snapshot = snapshotRecorder.recordSnapshot(playerUniqueId, cause);
        return storage.storeSnapshot(snapshot);
    }

}

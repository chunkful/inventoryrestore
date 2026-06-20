package net.chunkful.inventoryrestore.storage;

import net.chunkful.inventoryrestore.model.Snapshot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    CompletableFuture<Void> storeSnapshot(Snapshot snapshot);

    CompletableFuture<Optional<Snapshot>> getSnapshotById(UUID snapshotId);

    CompletableFuture<List<Snapshot>> getRecentSnapshotsOfPlayer(UUID playerUniqueId);

}

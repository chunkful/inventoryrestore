package net.chunkful.inventoryrestore.storage;

import net.chunkful.inventoryrestore.model.Snapshot;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class InMemoryStorage implements Storage {

    private final List<Snapshot> snapshots = new ArrayList<>();

    @Override
    public CompletableFuture<Void> storeSnapshot(Snapshot snapshot) {
        snapshots.add(snapshot);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> getSnapshotById(UUID snapshotId) {
        return CompletableFuture.completedFuture(
                snapshots.stream()
                        .filter(snapshot -> snapshot.snapshotId().equals(snapshotId))
                        .findFirst()
        );
    }

    @Override
    public CompletableFuture<List<Snapshot>> getRecentSnapshotsOfPlayer(UUID playerUniqueId) {
        return CompletableFuture.completedFuture(
                snapshots.stream()
                        .filter(snapshot -> snapshot.playerUniqueId().equals(playerUniqueId))
                        .toList()
        );
    }
}

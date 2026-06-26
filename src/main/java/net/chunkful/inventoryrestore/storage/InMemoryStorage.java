package net.chunkful.inventoryrestore.storage;

import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.SortOrder;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class InMemoryStorage implements Storage {

    private final List<Snapshot> snapshots = new ArrayList<>();

    @Override
    public void storeSnapshot(Snapshot snapshot) {
        snapshots.add(snapshot);
    }

    @Override
    public Optional<Snapshot> getSnapshotById(UUID snapshotId) {
        return snapshots.stream()
                .filter(snapshot -> snapshot.snapshotId().equals(snapshotId))
                .findFirst();
    }

    @Override
    public List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder) {
        return snapshots.stream()
                .filter(snapshot -> snapshot.playerUniqueId().equals(playerUniqueId))
                .skip(offset)
                .limit(limit)
                .sorted(SortOrder.snapshotComparator(sortOrder))
                .toList();
    }

    @Override
    public List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder, Snapshot.Cause cause) {
        return snapshots.stream()
                .filter(snapshot -> snapshot.playerUniqueId().equals(playerUniqueId) && snapshot.cause().equals(cause))
                .skip(offset)
                .limit(limit)
                .sorted(SortOrder.snapshotComparator(sortOrder))
                .toList();
    }
}

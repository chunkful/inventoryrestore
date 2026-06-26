package net.chunkful.inventoryrestore.storage;

import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.SortOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    void storeSnapshot(Snapshot snapshot);

    Optional<Snapshot> getSnapshotById(UUID snapshotId);

    List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder);

    List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder, Snapshot.Cause cause);

}

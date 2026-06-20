package net.chunkful.inventoryrestore.restore;

import net.chunkful.inventoryrestore.model.Snapshot;

import java.util.UUID;

public interface SnapshotRestorer {

    void restoreSnapshot(UUID playerUniqueId, Snapshot snapshot, boolean teleport);

}

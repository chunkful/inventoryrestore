package net.chunkful.inventoryrestore.recording;

import net.chunkful.inventoryrestore.model.Snapshot;

import java.util.UUID;

public interface SnapshotRecorder {

    Snapshot recordSnapshot(UUID playerUniqueId, Snapshot.Cause cause);

}

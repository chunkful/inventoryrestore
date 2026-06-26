package net.chunkful.inventoryrestore.lifecycle;

import net.chunkful.inventoryrestore.export.SnapshotExporter;
import net.chunkful.inventoryrestore.format.Format;
import net.chunkful.inventoryrestore.format.MessageLoader;
import net.chunkful.inventoryrestore.recording.SnapshotRecorder;
import net.chunkful.inventoryrestore.restore.SnapshotRestorer;
import net.chunkful.inventoryrestore.service.Service;
import net.chunkful.inventoryrestore.storage.Storage;
import net.chunkful.inventoryrestore.viewer.SnapshotViewer;

public record PluginBindings(
        Storage storage,
        SnapshotRecorder snapshotRecorder,
        SnapshotRestorer snapshotRestorer,
        SnapshotViewer snapshotViewer,
        SnapshotExporter snapshotExporter,
        MessageLoader messageLoader,
        Format format,
        Service service
) {

}

package net.chunkful.inventoryrestore.lifecycle;

import net.chunkful.inventoryrestore.config.PluginConfiguration;
import net.chunkful.inventoryrestore.export.SnapshotExporter;
import net.chunkful.inventoryrestore.export.StandardContainerSnapshotExporter;
import net.chunkful.inventoryrestore.format.Formatter;
import net.chunkful.inventoryrestore.format.MessageLoader;
import net.chunkful.inventoryrestore.recording.HybridRecorder;
import net.chunkful.inventoryrestore.recording.NBTSnapshotRecorder;
import net.chunkful.inventoryrestore.recording.OnlineSnapshotRecorder;
import net.chunkful.inventoryrestore.recording.SnapshotRecorder;
import net.chunkful.inventoryrestore.restore.HybridRestorer;
import net.chunkful.inventoryrestore.restore.NBTSnapshotRestorer;
import net.chunkful.inventoryrestore.restore.OnlineSnapshotRestorer;
import net.chunkful.inventoryrestore.restore.SnapshotRestorer;
import net.chunkful.inventoryrestore.service.RecordingService;
import net.chunkful.inventoryrestore.storage.AsyncStorage;
import net.chunkful.inventoryrestore.viewer.InventorySnapshotViewer;
import net.chunkful.inventoryrestore.viewer.SnapshotViewer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class PluginBinder {

    public PluginBindings bind(Plugin owningPlugin, PluginConfiguration pluginConfiguration) {
        AsyncStorage storage = new AsyncStorage(owningPlugin, pluginConfiguration.storageType().create(pluginConfiguration, owningPlugin));
        SnapshotRecorder snapshotRecorder = createSnapshotRecorder();
        SnapshotRestorer snapshotRestorer = createSnapshotRestorer();
        SnapshotViewer snapshotViewer = new InventorySnapshotViewer(owningPlugin);
        SnapshotExporter snapshotExporter = new StandardContainerSnapshotExporter(Material.CHEST, 27);
        RecordingService recordingService = new RecordingService(snapshotRecorder, storage);
        MessageLoader messageLoader = new MessageLoader(owningPlugin, pluginConfiguration.locales());
        Formatter formatter = new Formatter();

        return new PluginBindings(
                storage,
                snapshotRecorder,
                snapshotRestorer,
                snapshotViewer,
                snapshotExporter,
                recordingService,
                messageLoader,
                formatter
        );
    }

    private SnapshotRecorder createSnapshotRecorder() {
        if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            return new HybridRecorder(new OnlineSnapshotRecorder(), new NBTSnapshotRecorder());
        } else {
            return new OnlineSnapshotRecorder();
        }
    }

    private SnapshotRestorer createSnapshotRestorer() {
        if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            return new HybridRestorer(new OnlineSnapshotRestorer(), new NBTSnapshotRestorer());
        } else {
            return new OnlineSnapshotRestorer();
        }
    }

}

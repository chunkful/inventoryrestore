package net.chunkful.inventoryrestore.lifecycle;

import io.papermc.paper.ServerBuildInfo;
import net.chunkful.inventoryrestore.config.PluginConfiguration;
import net.chunkful.inventoryrestore.export.SnapshotExporter;
import net.chunkful.inventoryrestore.export.StandardContainerSnapshotExporter;
import net.chunkful.inventoryrestore.format.Format;
import net.chunkful.inventoryrestore.format.MessageLoader;
import net.chunkful.inventoryrestore.recording.HybridRecorder;
import net.chunkful.inventoryrestore.recording.NBTSnapshotRecorder;
import net.chunkful.inventoryrestore.recording.OnlineSnapshotRecorder;
import net.chunkful.inventoryrestore.recording.SnapshotRecorder;
import net.chunkful.inventoryrestore.restore.HybridRestorer;
import net.chunkful.inventoryrestore.restore.NBTSnapshotRestorer;
import net.chunkful.inventoryrestore.restore.OnlineSnapshotRestorer;
import net.chunkful.inventoryrestore.restore.SnapshotRestorer;
import net.chunkful.inventoryrestore.service.FoliaService;
import net.chunkful.inventoryrestore.service.PaperService;
import net.chunkful.inventoryrestore.service.Service;
import net.chunkful.inventoryrestore.storage.Storage;
import net.chunkful.inventoryrestore.viewer.InventorySnapshotViewer;
import net.chunkful.inventoryrestore.viewer.SnapshotViewer;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class PluginBinder {

    private static final boolean FOLIA = ServerBuildInfo.buildInfo().isBrandCompatible(Key.key("papermc:folia"));

    public PluginBindings bind(Plugin owningPlugin, PluginConfiguration pluginConfiguration) {
        Storage storage = pluginConfiguration.storageType().create(pluginConfiguration, owningPlugin);
        SnapshotRecorder snapshotRecorder = createSnapshotRecorder();
        SnapshotRestorer snapshotRestorer = createSnapshotRestorer();
        SnapshotViewer snapshotViewer = new InventorySnapshotViewer(owningPlugin);
        SnapshotExporter snapshotExporter = new StandardContainerSnapshotExporter(Material.CHEST, 27);
        MessageLoader messageLoader = new MessageLoader(owningPlugin, pluginConfiguration.locales());
        Format format = new Format(pluginConfiguration);
        Service service = createService(owningPlugin, format, snapshotRecorder, snapshotRestorer, snapshotExporter, snapshotViewer, storage);

        return new PluginBindings(
                storage,
                snapshotRecorder,
                snapshotRestorer,
                snapshotViewer,
                snapshotExporter,
                messageLoader,
                format,
                service
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

    private Service createService(Plugin owningPlugin, Format format, SnapshotRecorder snapshotRecorder, SnapshotRestorer snapshotRestorer, SnapshotExporter snapshotExporter, SnapshotViewer snapshotViewer, Storage storage) {
        if(FOLIA)
            return new FoliaService(owningPlugin, format, snapshotRecorder, snapshotRestorer, snapshotExporter, snapshotViewer, storage);
        else
            return new PaperService(owningPlugin, format, snapshotRecorder, snapshotRestorer, snapshotExporter, snapshotViewer, storage);
    }

}

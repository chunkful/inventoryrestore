package net.chunkful.inventoryrestore.storage.nitrite;

import net.chunkful.inventoryrestore.lifecycle.Lifecycled;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.storage.Storage;
import org.bukkit.plugin.Plugin;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.mvstore.MVStoreModule;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NitriteStorage implements Storage, Lifecycled {

    private final Plugin owningPlugin;
    private final SnapshotDecorator snapshotDecorator = new SnapshotDecorator();

    private Nitrite nitrite;

    public NitriteStorage(Plugin owningPlugin) {
        this.owningPlugin = owningPlugin;
    }

    @Override
    public CompletableFuture<Void> storeSnapshot(Snapshot snapshot) {
        nitrite.getRepository(snapshotDecorator).insert(snapshot);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> getSnapshotById(UUID snapshotId) {
        Optional<Snapshot> optionalSnapshot = Optional.ofNullable(nitrite.getRepository(snapshotDecorator).getById(snapshotId));
        return CompletableFuture.completedFuture(optionalSnapshot);
    }

    @Override
    public CompletableFuture<List<Snapshot>> getRecentSnapshotsOfPlayer(UUID playerUniqueId) {
        List<Snapshot> snapshots = nitrite.getRepository(snapshotDecorator)
                .find(FluentFilter.where("playerUniqueId").eq(playerUniqueId))
                .toList();
        return CompletableFuture.completedFuture(snapshots);
    }

    @Override
    public void load() {
        nitrite = Nitrite.builder()
                .loadModule(
                        MVStoreModule.withConfig()
                                .filePath(new File(owningPlugin.getDataFolder(), "snapshots.db"))
                                .autoCommit(true)
                                .build()
                )
                .registerEntityConverter(new SnapshotConverter())
                .disableRepositoryTypeValidation()
                .openOrCreate();
    }

    @Override
    public void unload() {
        nitrite.close();
    }
}

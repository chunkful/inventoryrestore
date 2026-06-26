package net.chunkful.inventoryrestore.storage.nitrite;

import net.chunkful.inventoryrestore.lifecycle.Lifecycled;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.SortOrder;
import net.chunkful.inventoryrestore.storage.Storage;
import org.bukkit.plugin.Plugin;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.mvstore.MVStoreModule;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NitriteStorage implements Storage, Lifecycled {

    private final Plugin owningPlugin;
    private final SnapshotDecorator snapshotDecorator = new SnapshotDecorator();

    private Nitrite nitrite;

    public NitriteStorage(Plugin owningPlugin) {
        this.owningPlugin = owningPlugin;
    }

    @Override
    public void storeSnapshot(Snapshot snapshot) {
        nitrite.getRepository(snapshotDecorator).insert(snapshot);
    }

    @Override
    public Optional<Snapshot> getSnapshotById(UUID snapshotId) {
        return Optional.ofNullable(nitrite.getRepository(snapshotDecorator).getById(snapshotId));
    }

    @Override
    public List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder) {
        return nitrite.getRepository(snapshotDecorator)
                .find(FluentFilter.where("playerUniqueId").eq(playerUniqueId))
                .toList()
                .stream()
                .skip(offset)
                .limit(limit)
                .sorted(SortOrder.snapshotComparator(sortOrder))
                .toList();
    }

    @Override
    public List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder, Snapshot.Cause cause) {
        return nitrite.getRepository(snapshotDecorator)
                .find(FluentFilter.where("playerUniqueId").eq(playerUniqueId).and(FluentFilter.where("cause").eq(cause)))
                .toList()
                .stream()
                .skip(offset)
                .limit(limit)
                .sorted(SortOrder.snapshotComparator(sortOrder))
                .toList();
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

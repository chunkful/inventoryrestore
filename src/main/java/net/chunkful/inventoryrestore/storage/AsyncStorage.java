package net.chunkful.inventoryrestore.storage;

import net.chunkful.inventoryrestore.lifecycle.Lifecycled;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class AsyncStorage implements Storage, Lifecycled {

    private final Plugin owningPlugin;
    private final Storage delegate;

    public AsyncStorage(Plugin owningPlugin, Storage delegate) {
        this.owningPlugin = owningPlugin;
        this.delegate = delegate;
    }

    @Override
    public CompletableFuture<Void> storeSnapshot(Snapshot snapshot) {
        return CompletableFuture.runAsync(() -> delegate.storeSnapshot(snapshot), async());
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> getSnapshotById(UUID snapshotId) {
        return CompletableFuture.supplyAsync(() -> delegate.getSnapshotById(snapshotId), async())
                .thenCompose(Function.identity());
    }

    @Override
    public CompletableFuture<List<Snapshot>> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit) {
        return CompletableFuture.supplyAsync(() -> delegate.getRecentSnapshotsOfPlayer(playerUniqueId, offset, limit), async())
                .thenCompose(Function.identity());
    }

    private Executor async() {
        return command -> Bukkit.getScheduler().runTaskAsynchronously(owningPlugin, command);
    }

    @Override
    public void load() {
        if(delegate instanceof Lifecycled lifecycled) {
            lifecycled.load();
        }
    }

    @Override
    public void unload() {
        if(delegate instanceof Lifecycled lifecycled) {
            lifecycled.unload();
        }
    }
}

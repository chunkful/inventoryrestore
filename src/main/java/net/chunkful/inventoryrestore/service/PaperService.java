package net.chunkful.inventoryrestore.service;

import net.chunkful.inventoryrestore.export.SnapshotExporter;
import net.chunkful.inventoryrestore.format.Format;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.recording.SnapshotRecorder;
import net.chunkful.inventoryrestore.restore.SnapshotRestorer;
import net.chunkful.inventoryrestore.storage.Storage;
import net.chunkful.inventoryrestore.viewer.SnapshotViewer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class PaperService implements Service {

    private final Plugin owningPlugin;
    private final Format format;
    private final SnapshotRecorder snapshotRecorder;
    private final SnapshotRestorer snapshotRestorer;
    private final SnapshotExporter snapshotExporter;
    private final SnapshotViewer snapshotViewer;
    private final Storage storage;

    public PaperService(Plugin owningPlugin, Format format, SnapshotRecorder snapshotRecorder, SnapshotRestorer snapshotRestorer, SnapshotExporter snapshotExporter, SnapshotViewer snapshotViewer, Storage storage) {
        this.owningPlugin = owningPlugin;
        this.format = format;
        this.snapshotRecorder = snapshotRecorder;
        this.snapshotRestorer = snapshotRestorer;
        this.snapshotExporter = snapshotExporter;
        this.snapshotViewer = snapshotViewer;
        this.storage = storage;
    }

    @Override
    public void recordAndSave(UUID playerUniqueId, Snapshot.Cause cause) {
        CompletableFuture.supplyAsync(() -> snapshotRecorder.recordSnapshot(playerUniqueId, cause), sync()).thenAcceptAsync(storage::storeSnapshot, async());
    }

    @Override
    public void showSnapshotList(CommandSender sender, UUID playerUniqueId, Snapshot.@Nullable Cause causeFilter, SortOrder sortOrder, int page) {
        if (!sender.hasPermission("inventoryrestore.list")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        int offset = (page - 1) * 10;
        int limit = 10;
        CompletableFuture.supplyAsync(() -> {
            if (causeFilter != null) {
                return storage.getRecentSnapshotsOfPlayer(playerUniqueId, offset, limit, sortOrder, causeFilter);
            } else {
                return storage.getRecentSnapshotsOfPlayer(playerUniqueId, offset, limit, sortOrder);
            }
        }, async()).thenAcceptAsync(snapshots -> {
            sender.sendMessage(format.formatList(snapshots, page, playerUniqueId, causeFilter, sortOrder));
        }, sync());
    }

    @Override
    public void showSnapshotDetails(CommandSender sender, Snapshot snapshot) {
        if (!sender.hasPermission("inventoryrestore.details")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        sync().execute(() -> {
            sender.sendMessage(format.formatDetails(snapshot));
        });
    }

    @Override
    public void showSnapshotDetails(CommandSender sender, UUID snapshotId) {
        if (!sender.hasPermission("inventoryrestore.details")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        withSnapshot(sender, snapshotId, snapshot -> showSnapshotDetails(sender, snapshot));
    }

    @Override
    public void restoreSnapshot(CommandSender sender, Snapshot snapshot, UUID targetUniqueId, boolean teleport) {
        if (!sender.hasPermission("inventoryrestore.restore")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        sync().execute(() -> {
            snapshotRestorer.restoreSnapshot(targetUniqueId, snapshot, teleport);
        });
    }

    @Override
    public void restoreSnapshot(CommandSender sender, UUID snapshotId, UUID targetUniqueId, boolean teleport) {
        if (!sender.hasPermission("inventoryrestore.restore")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        withSnapshot(sender, snapshotId, snapshot -> restoreSnapshot(sender, snapshot, targetUniqueId, teleport));
    }

    @Override
    public void restoreSnapshot(CommandSender sender, Snapshot snapshot, boolean teleport) {
        restoreSnapshot(sender, snapshot, snapshot.playerUniqueId(), teleport);
    }

    @Override
    public void restoreSnapshot(CommandSender sender, UUID snapshotId, boolean teleport) {
        withSnapshot(sender, snapshotId, snapshot -> restoreSnapshot(sender, snapshot, teleport));
    }

    @Override
    public void openSnapshot(Player sender, Snapshot snapshot) {
        if (!sender.hasPermission("inventoryrestore.open")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        sync().execute(() -> {
            snapshotViewer.openInventory(sender, snapshot);
        });
    }

    @Override
    public void openSnapshot(Player sender, UUID snapshotId) {
        if (!sender.hasPermission("inventoryrestore.open")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        withSnapshot(sender, snapshotId, snapshot -> openSnapshot(sender, snapshot));
    }

    @Override
    public void exportSnapshot(Player sender, Snapshot snapshot, boolean skipInventory, boolean skipEnderchest) {
        if (!sender.hasPermission("inventoryrestore.export")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        sync().execute(() -> {
            sender.give(snapshotExporter.exportSnapshot(snapshot, skipInventory, skipEnderchest), false);
        });
    }

    @Override
    public void exportSnapshot(Player sender, UUID snapshotId, boolean skipInventory, boolean skipEnderchest) {
        if (!sender.hasPermission("inventoryrestore.export")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        withSnapshot(sender, snapshotId, snapshot -> exportSnapshot(sender, snapshot, skipInventory, skipEnderchest));
    }

    private void withSnapshot(CommandSender sender, UUID snapshotId, Consumer<Snapshot> snapshotConsumer) {
        CompletableFuture.supplyAsync(() -> storage.getSnapshotById(snapshotId), async()).thenAcceptAsync(optionalSnapshot -> optionalSnapshot.ifPresentOrElse(snapshotConsumer, () -> sender.sendMessage(Component.translatable("ir.snapshot_not_found"))));
    }

    private Executor sync() {
        return command -> {
            if(Bukkit.isPrimaryThread()) {
                command.run();
            }   else    {
                Bukkit.getScheduler().runTask(owningPlugin, command);
            }
        };
    }

    private Executor async() {
        return command -> Bukkit.getScheduler().runTaskAsynchronously(owningPlugin, command);
    }
}

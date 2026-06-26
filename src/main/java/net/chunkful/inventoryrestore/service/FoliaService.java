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
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class FoliaService implements Service {

    private final Plugin owningPlugin;
    private final Format format;
    private final SnapshotRecorder snapshotRecorder;
    private final SnapshotRestorer snapshotRestorer;
    private final SnapshotExporter snapshotExporter;
    private final SnapshotViewer snapshotViewer;
    private final Storage storage;

    public FoliaService(Plugin owningPlugin, Format format, SnapshotRecorder snapshotRecorder, SnapshotRestorer snapshotRestorer, SnapshotExporter snapshotExporter, SnapshotViewer snapshotViewer, Storage storage) {
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
        CompletableFuture.supplyAsync(() -> snapshotRecorder.recordSnapshot(playerUniqueId, cause), playerUniqueId(playerUniqueId)).thenAcceptAsync(storage::storeSnapshot, async());
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
        }, sender(sender));
    }

    @Override
    public void showSnapshotDetails(CommandSender sender, Snapshot snapshot) {
        if (!sender.hasPermission("inventoryrestore.details")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        sender(sender).execute(() -> {
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

        playerUniqueId(targetUniqueId).execute(() -> snapshotRestorer.restoreSnapshot(targetUniqueId, snapshot, teleport));
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
    public void openSnapshot(Player sender, Snapshot snapshot) {
        if (!sender.hasPermission("inventoryrestore.open")) {
            sender.sendMessage(Component.translatable("ir.no_permission"));
            return;
        }

        entity(sender).execute(() -> {
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

        entity(sender).execute(() -> {
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

    private Executor entity(Entity entity) {
        return command -> {
            if (Bukkit.isOwnedByCurrentRegion(entity)) {
                command.run();
            } else {
                entity.getScheduler().run(owningPlugin, _ -> command.run(), null);
            }
        };
    }

    private Executor location(Location location) {
        return command -> {
            if (Bukkit.isOwnedByCurrentRegion(location)) {
                command.run();
            } else {
                Bukkit.getRegionScheduler().run(owningPlugin, location, _ -> command.run());
            }
        };
    }

    private Executor global() {
        return command -> {
            if (Bukkit.isGlobalTickThread()) {
                command.run();
            } else {
                Bukkit.getGlobalRegionScheduler().run(owningPlugin, _ -> command.run());
            }
        };
    }

    private Executor async() {
        return command -> Bukkit.getAsyncScheduler().runNow(owningPlugin, _ -> command.run());
    }

    private Executor sender(CommandSender commandSender) {
        return switch (commandSender) {
            case Entity entity -> entity(entity);
            case BlockCommandSender blockCommandSender -> location(blockCommandSender.getBlock().getLocation());
            case ConsoleCommandSender _ -> global();
            default ->
                    throw new IllegalArgumentException("Unsupported command sender type: " + commandSender.getClass().getName());
        };
    }

    private Executor playerUniqueId(UUID playerUniqueId) {
        Player targetPlayer = Bukkit.getPlayer(playerUniqueId);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            return async();
        } else {
            return entity(targetPlayer);
        }
    }
}

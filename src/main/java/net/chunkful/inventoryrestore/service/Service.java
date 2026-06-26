package net.chunkful.inventoryrestore.service;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface Service {

    void recordAndSave(UUID playerUniqueId, Snapshot.Cause cause);

    void showSnapshotList(
            CommandSender sender,
            UUID playerUniqueId,
            Snapshot.@Nullable Cause causeFilter,
            SortOrder sortOrder,
            int page
    );

    void showSnapshotDetails(
            CommandSender sender,
            Snapshot snapshot
    );

    void showSnapshotDetails(
            CommandSender sender,
            UUID snapshotId
    );

    void restoreSnapshot(CommandSender sender, Snapshot snapshot, UUID targetUniqueId, boolean teleport);

    void restoreSnapshot(CommandSender sender, UUID snapshotId, UUID targetUniqueId, boolean teleport);

    void restoreSnapshot(CommandSender sender, Snapshot snapshot, boolean teleport);

    void restoreSnapshot(CommandSender sender, UUID snapshotId, boolean teleport);

    void openSnapshot(Player sender, Snapshot snapshot);

    void openSnapshot(Player sender, UUID snapshotId);

    void exportSnapshot(Player sender, Snapshot snapshot, boolean skipInventory, boolean skipEnderchest);

    void exportSnapshot(Player sender, UUID snapshotId, boolean skipInventory, boolean skipEnderchest);

}

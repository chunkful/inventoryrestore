package net.chunkful.inventoryrestore.restore;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OnlineSnapshotRestorer implements SnapshotRestorer {

    @Override
    public void restoreSnapshot(UUID playerUniqueId, Snapshot snapshot, boolean teleport) {
        Player player = Bukkit.getPlayer(playerUniqueId);
        if(player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Player is not online");
        }

        player.getInventory().setContents(snapshot.items());
        player.getEnderChest().setContents(snapshot.enderChest());
        // player.setHealth(snapshot.health()); Pretty dumb
        player.setFoodLevel(snapshot.foodLevel());
        player.setExperienceLevelAndProgress(snapshot.experience());
        snapshot.activePotionEffects().forEach(player::addPotionEffect);
        player.updateInventory();

        if(teleport) {
            player.teleport(snapshot.location());
        }
    }

}

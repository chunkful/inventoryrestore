package net.chunkful.inventoryrestore.recording;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnlineSnapshotRecorder implements SnapshotRecorder {

    @Override
    public Snapshot recordSnapshot(UUID playerUniqueId, Snapshot.Cause cause) {
        Player player = Bukkit.getPlayer(playerUniqueId);
        if(player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Player is not online");
        }

        ItemStack[] items = player.getInventory().getContents();
        ItemStack[] enderChest = player.getEnderChest().getContents();
        int health = (int) player.getHealth();
        int level = player.getLevel();
        int foodLevel = player.getFoodLevel();
        int experience = player.calculateTotalExperiencePoints();
        List<PotionEffect> activePotionEffects = new ArrayList<>(player.getActivePotionEffects());
        Location location = player.getLocation();

        return Snapshot.create(playerUniqueId, cause, items, enderChest, health, level, foodLevel, experience, activePotionEffects, location);
    }
}

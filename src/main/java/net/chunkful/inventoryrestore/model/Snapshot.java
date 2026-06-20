package net.chunkful.inventoryrestore.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Snapshot(
        @BsonId
        UUID snapshotId,
        Instant createdAt,
        UUID playerUniqueId,
        Cause cause,
        ItemStack[] items,
        ItemStack[] enderChest,
        int health,
        int level,
        int foodLevel,
        int experience,
        List<PotionEffect> activePotionEffects,
        Location location
) {

    public static Snapshot create(
            UUID playerUniqueId,
            Cause cause,
            ItemStack[] items,
            ItemStack[] enderChest,
            int health,
            int level,
            int foodLevel,
            int experience,
            List<PotionEffect> activePotionEffects,
            Location location
    ) {
        return new Snapshot(
                UUID.randomUUID(),
                Instant.now(),
                playerUniqueId,
                cause,
                items,
                enderChest,
                health,
                level,
                foodLevel,
                experience,
                activePotionEffects,
                location
        );
    }

    public enum Cause {
        MANUAL,
        SCHEDULE,
        JOIN,
        QUIT,
        DEATH,
    }

}

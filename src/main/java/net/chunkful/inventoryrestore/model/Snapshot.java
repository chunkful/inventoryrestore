package net.chunkful.inventoryrestore.model;

import net.chunkful.inventoryrestore.storage.sql.ItemStackArrayDeserializer;
import net.chunkful.inventoryrestore.storage.sql.ItemStackArraySerializer;
import net.chunkful.inventoryrestore.storage.sql.LocationDeserializingConverter;
import net.chunkful.inventoryrestore.storage.sql.LocationSerializingConverter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Snapshot(
        @BsonId
        UUID snapshotId,
        Instant createdAt,
        UUID playerUniqueId,
        Cause cause,
        @JsonSerialize(using = ItemStackArraySerializer.class)
        @JsonDeserialize(using = ItemStackArrayDeserializer.class)
        ItemStack[] items,
        @JsonSerialize(using = ItemStackArraySerializer.class)
        @JsonDeserialize(using = ItemStackArrayDeserializer.class)
        ItemStack[] enderChest,
        int health,
        int level,
        int foodLevel,
        int experience,
        List<PotionEffect> activePotionEffects,
        @JsonSerialize(converter = LocationSerializingConverter.class)
        @JsonDeserialize(converter = LocationDeserializingConverter.class)
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

package net.chunkful.inventoryrestore.storage.nitrite;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

class SnapshotConverter implements EntityConverter<Snapshot> {

    @Override
    public Class<Snapshot> getEntityType() {
        return Snapshot.class;
    }

    @Override
    public Document toDocument(Snapshot entity, NitriteMapper nitriteMapper) {
        byte[] itemsAsBytes = ItemStack.serializeItemsAsBytes(entity.items());
        byte[] enderChestAsBytes = ItemStack.serializeItemsAsBytes(entity.enderChest());

        return Document.createDocument()
                .put("snapshotId", entity.snapshotId())
                .put("createdAt", entity.createdAt().getEpochSecond())
                .put("playerUniqueId", entity.playerUniqueId())
                .put("cause", entity.cause().name())
                .put("items", itemsAsBytes)
                .put("enderChest", enderChestAsBytes)
                .put("health", entity.health())
                .put("foodLevel", entity.foodLevel())
                .put("level", entity.level())
                .put("experience", entity.experience())
                .put("location.world", entity.location().getWorld().getName())
                .put("location.x", entity.location().getX())
                .put("location.y", entity.location().getY())
                .put("location.z", entity.location().getZ())
                .put("location.yaw", entity.location().getYaw())
                .put("location.pitch", entity.location().getPitch());
    }

    @Override
    public Snapshot fromDocument(Document document, NitriteMapper nitriteMapper) {
        UUID snapshotId = document.get("snapshotId", UUID.class);
        UUID playerUniqueId = document.get("playerUniqueId", UUID.class);
        Instant createdAt = Instant.ofEpochSecond(document.get("createdAt", Long.class));
        Snapshot.Cause cause = Snapshot.Cause.valueOf(document.get("cause", String.class));

        byte[] itemsAsBytes = document.get("items", byte[].class);
        byte[] enderChestAsBytes = document.get("enderChest", byte[].class);
        ItemStack[] items = ItemStack.deserializeItemsFromBytes(itemsAsBytes);
        ItemStack[] enderChest = ItemStack.deserializeItemsFromBytes(enderChestAsBytes);

        int health = document.get("health", Integer.class);
        int foodLevel = document.get("foodLevel", Integer.class);
        int level = document.get("level", Integer.class);
        int experience = document.get("experience", Integer.class);

        String worldName = document.get("location.world", String.class);
        World world = Bukkit.getWorld(worldName);
        double x = document.get("location.x", Double.class);
        double y = document.get("location.y", Double.class);
        double z = document.get("location.z", Double.class);
        float yaw = document.get("location.yaw", Float.class);
        float pitch = document.get("location.pitch", Float.class);
        Location location = new Location(world, x, y, z, yaw, pitch);

        return new Snapshot(snapshotId, createdAt, playerUniqueId, cause, items, enderChest,
                health, level, foodLevel, experience, List.of(), location);
    }
}

package net.chunkful.inventoryrestore.recording;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTType;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class NBTSnapshotRecorder implements SnapshotRecorder {

    @Override
    public Snapshot recordSnapshot(UUID playerUniqueId, Snapshot.Cause cause) {
        File playerdataFile = getPlayerdataFile(playerUniqueId);
        if (!playerdataFile.exists()) {
            throw new IllegalArgumentException("Player data file does not exist");
        }

        try {
            ReadableNBT readableNBT = NBT.readFile(playerdataFile);
            ItemStack[] items = readInventory(readableNBT);
            ItemStack[] enderChest = readEnderchest(readableNBT);

            int health = readHealth(readableNBT);
            int foodLevel = readFoodLevel(readableNBT);
            int level = readLevel(readableNBT);
            int experience = readExperience(readableNBT);

            return Snapshot.create(playerUniqueId, cause, items, enderChest, health, level, foodLevel, experience, List.of(), null);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read player data file", e);
        }
    }

    private File getPlayerdataFile(UUID uuid) {
        World firstWorld = Bukkit.getWorlds().getFirst();
        File playerdataDirectory = new File(firstWorld.getWorldFolder(), "playerdata");
        return new File(playerdataDirectory, uuid + ".dat");
    }

    private ItemStack[] readInventory(ReadableNBT readableNBT) {
        ItemStack[] items = readItems(readableNBT, "Inventory", 45);
        readEquipment(readableNBT, items);
        return items;
    }

    private ItemStack[] readEnderchest(ReadableNBT readableNBT) {
        return readItems(readableNBT, "EnderItems", 27);
    }

    private int readHealth(ReadableNBT readableNBT) {
        return readableNBT.getFloat("Health").intValue();
    }

    private int readFoodLevel(ReadableNBT readableNBT) {
        return readableNBT.getFloat("foodLevel").intValue();
    }

    private int readLevel(ReadableNBT readableNBT) {
        return readableNBT.getFloat("XpLevel").intValue();
    }

    private int readExperience(ReadableNBT readableNBT) {
        return readableNBT.getFloat("XpTotal").intValue();
    }

    private ItemStack[] readItems(ReadableNBT readableNBT, String path, int size) {
        ItemStack[] items = new ItemStack[size];
        if (!readableNBT.hasTag(path, NBTType.NBTTagList)) {
            return items;
        }

        ReadableNBTList<ReadWriteNBT> compoundList = readableNBT.getCompoundList(path);
        for (ReadableNBT compound : compoundList) {
            int slot = compound.getByte("Slot").intValue();
            items[slot] = NBT.itemStackFromNBT(compound);
        }
        return items;
    }

    private void readEquipment(ReadableNBT readableNBT, ItemStack[] itemStacks) {
        readEquipmentSlot(readableNBT, "head", itemStacks, 39);
        readEquipmentSlot(readableNBT, "chest", itemStacks, 38);
        readEquipmentSlot(readableNBT, "legs", itemStacks, 37);
        readEquipmentSlot(readableNBT, "feet", itemStacks, 36);
        readEquipmentSlot(readableNBT, "offhand", itemStacks, 40);
    }

    private void readEquipmentSlot(ReadableNBT readableNBT, String slotName, ItemStack[] itemStacks, int slotIndex) {
        ReadableNBT equipment = readableNBT.getCompound("equipment");
        if(equipment == null) return;
        ReadableNBT item = equipment.getCompound(slotName);
        if(item == null) return;
        itemStacks[slotIndex] = NBT.itemStackFromNBT(item);
    }
}

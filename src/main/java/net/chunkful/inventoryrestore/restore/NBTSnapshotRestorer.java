package net.chunkful.inventoryrestore.restore;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.UUID;

public class NBTSnapshotRestorer implements SnapshotRestorer {

    @Override
    public void restoreSnapshot(UUID playerUniqueId, Snapshot snapshot, boolean teleport) {
        File playerdataFile = getPlayerdataFile(playerUniqueId);
        if (!playerdataFile.exists()) {
            throw new IllegalArgumentException("Player data file does not exist");
        }

        try {
            ReadWriteNBT nbt = NBT.readFile(playerdataFile);
            writeInventory(nbt, snapshot.items());
            writeEnderchest(nbt, snapshot.enderChest());

            nbt.setFloat("Health", (float) snapshot.health());
            nbt.setFloat("foodLevel", (float) snapshot.foodLevel());
            nbt.setFloat("XpLevel", (float) snapshot.level());
            nbt.setFloat("XpTotal", (float) snapshot.experience());

            writeEquipment(nbt, snapshot.items());

            if (teleport && snapshot.location() != null) {
                nbt.getDoubleList("Pos").clear();
                nbt.getDoubleList("Pos").add(snapshot.location().getX());
                nbt.getDoubleList("Pos").add(snapshot.location().getY());
                nbt.getDoubleList("Pos").add(snapshot.location().getZ());

                nbt.getFloatList("Rotation").clear();
                nbt.getFloatList("Rotation").add(snapshot.location().getYaw());
                nbt.getFloatList("Rotation").add(snapshot.location().getPitch());

                nbt.setString("Dimension", snapshot.location().getWorld().getKey().toString());
            }

            NBT.writeFile(playerdataFile, nbt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore snapshot to NBT", e);
        }
    }

    private File getPlayerdataFile(UUID uuid) {
        World firstWorld = Bukkit.getWorlds().getFirst();
        File playerdataDirectory = new File(firstWorld.getWorldFolder(), "playerdata");
        return new File(playerdataDirectory, uuid + ".dat");
    }

    private void writeInventory(ReadWriteNBT nbt, ItemStack[] items) {
        writeItems(nbt, "Inventory", items);
    }

    private void writeEnderchest(ReadWriteNBT nbt, ItemStack[] items) {
        writeItems(nbt, "EnderItems", items);
    }

    private void writeItems(ReadWriteNBT nbt, String path, ItemStack[] items) {
        ReadWriteNBTCompoundList compoundList = nbt.getCompoundList(path);
        compoundList.clear();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null || item.getType().isAir()) continue;

            ReadWriteNBT compound = compoundList.addCompound();
            compound.setByte("Slot", (byte) i);
            compound.mergeCompound(NBT.itemStackToNBT(item));
        }
    }

    private void writeEquipment(ReadWriteNBT nbt, ItemStack[] items) {
        writeEquipmentSlot(nbt, "head", items, 39);
        writeEquipmentSlot(nbt, "chest", items, 38);
        writeEquipmentSlot(nbt, "legs", items, 37);
        writeEquipmentSlot(nbt, "feet", items, 36);
        writeEquipmentSlot(nbt, "offhand", items, 40);
    }

    private void writeEquipmentSlot(ReadWriteNBT nbt, String slotName, ItemStack[] items, int slotIndex) {
        if (items.length <= slotIndex) return;
        ItemStack item = items[slotIndex];
        if (item == null || item.getType().isAir()) {
            if (nbt.hasTag("equipment")) {
                nbt.getCompound("equipment").removeKey(slotName);
            }
            return;
        }

        ReadWriteNBT equipment = nbt.getOrCreateCompound("equipment");
        ReadWriteNBT itemCompound = equipment.getOrCreateCompound(slotName);
        itemCompound.mergeCompound(NBT.itemStackToNBT(item));
    }
}

package net.chunkful.inventoryrestore.viewer.holder;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.Component;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class SnapshotViewHolder implements InventoryHolder {

    private final Inventory inventory;
    private final Snapshot snapshot;
    private final IntList lockedSlots;

    public SnapshotViewHolder(Snapshot snapshot, int size, Component title) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.snapshot = snapshot;
        this.lockedSlots = new IntArrayList();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void lockSlot(int slot) {
        lockedSlots.add(slot);
    }

    public boolean isLocked(int slot) {
        return lockedSlots.contains(slot);
    }
}

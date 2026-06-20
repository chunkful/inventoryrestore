package net.chunkful.inventoryrestore.viewer.holder;

import net.kyori.adventure.text.Component;
import net.chunkful.inventoryrestore.model.Snapshot;

public class InventoryViewHolder extends SnapshotViewHolder {

    public InventoryViewHolder(Snapshot snapshot) {
        super(snapshot, 9 * 5, Component.text("Inventory Snapshot"));
    }
}

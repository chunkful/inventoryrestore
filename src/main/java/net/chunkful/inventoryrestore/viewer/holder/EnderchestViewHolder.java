package net.chunkful.inventoryrestore.viewer.holder;

import net.kyori.adventure.text.Component;
import net.chunkful.inventoryrestore.model.Snapshot;

public class EnderchestViewHolder extends SnapshotViewHolder {

    public EnderchestViewHolder(Snapshot snapshot) {
        super(snapshot, 9 * 4, Component.text("Enderchest Snapshot"));
    }
}

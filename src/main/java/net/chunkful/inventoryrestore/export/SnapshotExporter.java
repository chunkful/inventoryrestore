package net.chunkful.inventoryrestore.export;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface SnapshotExporter {

    List<ItemStack> export(Snapshot snapshot, boolean skipInventory, boolean skipEnderchest);

}

package net.chunkful.inventoryrestore.export;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Gatherers;

abstract class AbstractContainerSnapshotExporter implements SnapshotExporter {

    private final int containerSize;

    AbstractContainerSnapshotExporter(int containerSize) {
        this.containerSize = containerSize;
    }

    @Override
    public List<ItemStack> export(Snapshot snapshot, boolean skipInventory, boolean skipEnderchest) {
        if(skipInventory && skipEnderchest) {
            return Collections.emptyList();
        }

        if(skipInventory) {
            return export(snapshot, Snapshot::enderChest);
        }

        if(skipEnderchest) {
            return export(snapshot, Snapshot::items);
        }

        return export(snapshot, this::combinedGatherer);
    }

    private List<ItemStack> export(Snapshot snapshot, Function<Snapshot, ItemStack[]> itemStackGatherer) {
        return Arrays.stream(itemStackGatherer.apply(snapshot))
                .filter(Objects::nonNull)
                .filter(itemStack -> !itemStack.isEmpty())
                .gather(Gatherers.windowFixed(containerSize))
                .map(this::createContainer)
                .toList();
    }

    private ItemStack[] combinedGatherer(Snapshot snapshot) {
        return ArrayUtils.addAll(snapshot.items(), snapshot.enderChest());
    }

    abstract ItemStack createContainer(List<ItemStack> itemStacks);

}

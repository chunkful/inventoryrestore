package net.chunkful.inventoryrestore.export;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class StandardContainerSnapshotExporter extends AbstractContainerSnapshotExporter {

    private final Material material;

    public StandardContainerSnapshotExporter(Material material, int containerSize) {
        super(containerSize);
        this.material = material;
    }

    @Override
    ItemStack createContainer(List<ItemStack> itemStacks) {
        ItemStack itemStack = new ItemStack(material);
        itemStack.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(itemStacks));
        return itemStack;
    }
}

package net.chunkful.inventoryrestore.viewer.view;

import net.kyori.adventure.text.Component;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.viewer.InventorySnapshotViewer;
import net.chunkful.inventoryrestore.viewer.holder.EnderchestViewHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderchestView extends SnapshotView<EnderchestViewHolder> implements Listener {

    private static final int[] PLACEHOLDER_SLOTS = {27, 28, 29, 30, 31, 32, 33, 34};
    private static final int SWITCH_BUTTON_SLOT = 35;

    public EnderchestView(InventorySnapshotViewer viewer) {
        super(viewer, PLACEHOLDER_SLOTS, SWITCH_BUTTON_SLOT);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        handleInventoryClick(event);
    }

    @Override
    public EnderchestViewHolder createHolder(Snapshot snapshot) {
        return new EnderchestViewHolder(snapshot);
    }

    @Override
    EnderchestViewHolder getHolder(Inventory inventory) {
        if(inventory.getHolder() instanceof EnderchestViewHolder enderchestViewHolder) {
            return enderchestViewHolder;
        }
        return null;
    }

    @Override
    void fill(EnderchestViewHolder enderchestViewHolder) {
        Snapshot snapshot = enderchestViewHolder.getSnapshot();
        for (int i = 0; i < 27; i++) {
            ItemStack item = snapshot.enderChest()[i];
            if(item != null && !item.isEmpty()) {
                enderchestViewHolder.getInventory().setItem(i, item);
            }
        }
    }

    @Override
    ItemStack createSwitchViewButton() {
        ItemStack itemStack = new ItemStack(Material.CHEST);
        itemStack.editMeta(itemMeta -> itemMeta.itemName(Component.text("Switch to Inventory")));
        return itemStack;
    }

    @Override
    void openNextView(InventorySnapshotViewer viewer, Player player, Snapshot snapshot) {
        viewer.openInventory(player, snapshot);
    }

}

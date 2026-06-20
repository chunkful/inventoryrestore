package net.chunkful.inventoryrestore.viewer.view;

import net.kyori.adventure.text.Component;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.viewer.InventorySnapshotViewer;
import net.chunkful.inventoryrestore.viewer.holder.InventoryViewHolder;
import net.chunkful.inventoryrestore.viewer.holder.SnapshotViewHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryView extends SnapshotView<InventoryViewHolder> implements Listener {

    private static final int[] PLACEHOLDER_SLOTS = {36, 41, 43};
    private static final int SWITCH_BUTTON_SLOT = 44;

    public InventoryView(InventorySnapshotViewer viewer) {
        super(viewer, PLACEHOLDER_SLOTS, SWITCH_BUTTON_SLOT);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        handleInventoryClick(event);
    }

    @Override
    public InventoryViewHolder createHolder(Snapshot snapshot) {
        return new InventoryViewHolder(snapshot);
    }

    @Override
    InventoryViewHolder getHolder(Inventory inventory) {
        if(inventory.getHolder() instanceof InventoryViewHolder inventoryViewHolder) {
            return inventoryViewHolder;
        }
        return null;
    }

    @Override
    void fill(InventoryViewHolder inventoryViewHolder) {
        Snapshot snapshot = inventoryViewHolder.getSnapshot();
        for (int i = 0; i < 36; i++) {
            ItemStack item = snapshot.items()[i];
            if(item != null && !item.isEmpty()) {
                inventoryViewHolder.getInventory().setItem(i, item);
            }
        }

        for (ExtraSlot extraSlot : ExtraSlot.values()) {
            placeExtraSlot(inventoryViewHolder, extraSlot);
        }
    }

    @Override
    ItemStack createSwitchViewButton() {
        ItemStack itemStack = new ItemStack(Material.ENDER_CHEST);
        itemStack.editMeta(itemMeta -> itemMeta.itemName(Component.text("Switch to Enderchest")));
        return itemStack;
    }

    @Override
    void openNextView(InventorySnapshotViewer viewer, Player player, Snapshot snapshot) {
        viewer.openEnderchest(player, snapshot);
    }

    private void placeExtraSlot(SnapshotViewHolder snapshotViewHolder, ExtraSlot extraSlot) {
        ItemStack slotItem = snapshotViewHolder.getSnapshot().items()[extraSlot.sourceSlot];
        if(slotItem != null && !slotItem.isEmpty()) {
            snapshotViewHolder.getInventory().setItem(extraSlot.targetSlot, slotItem);
        }   else    {
            snapshotViewHolder.getInventory().setItem(extraSlot.targetSlot, createExtraSlotPlaceholder(extraSlot.slotName));
            snapshotViewHolder.lockSlot(extraSlot.targetSlot);
        }
    }

    private enum ExtraSlot {
        HELMET(39, 37, "Helmet"),
        CHESTPLATE(38, 38, "Chestplate"),
        LEGGINGS(37, 39, "Leggings"),
        BOOTS(36, 40, "Boots"),
        OFFHAND(40, 42, "Offhand");

        private final int sourceSlot;
        private final int targetSlot;
        private final String slotName;

        ExtraSlot(int sourceSlot, int targetSlot, String slotName) {
            this.sourceSlot = sourceSlot;
            this.targetSlot = targetSlot;
            this.slotName = slotName;
        }
    }

}

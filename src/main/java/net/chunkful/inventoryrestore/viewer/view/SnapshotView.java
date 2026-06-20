package net.chunkful.inventoryrestore.viewer.view;

import net.kyori.adventure.text.Component;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.viewer.InventorySnapshotViewer;
import net.chunkful.inventoryrestore.viewer.holder.SnapshotViewHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class SnapshotView<H extends SnapshotViewHolder> {

    private final InventorySnapshotViewer viewer;
    private final int[] placeholderSlots;
    private final int switchViewSlot;

    protected SnapshotView(InventorySnapshotViewer viewer, int[] placeholderSlots, int switchViewSlot) {
        this.viewer = viewer;
        this.placeholderSlots = placeholderSlots;
        this.switchViewSlot = switchViewSlot;
    }

    public void open(Player player, Snapshot snapshot) {
        H holder = createHolder(snapshot);
        fill(holder);
        for (int placeholderSlot : placeholderSlots) {
            holder.getInventory().setItem(placeholderSlot, createPlaceholder());
            holder.lockSlot(placeholderSlot);
        }
        holder.getInventory().setItem(switchViewSlot, createSwitchViewButton());
        holder.lockSlot(switchViewSlot);
        player.openInventory(holder.getInventory());
    }

    final void handleInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        H holder;
        if ((holder = getHolder(event.getClickedInventory())) == null) {
            return;
        }

        if (holder.isLocked(event.getSlot())) {
            event.setCancelled(true);
        }

        if(event.getSlot() == switchViewSlot && event.getWhoClicked() instanceof Player player) {
            openNextView(viewer, player, holder.getSnapshot());
        }
    }

    ItemStack createPlaceholder() {
        ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        itemStack.editMeta(itemMeta -> itemMeta.setHideTooltip(true));
        return itemStack;
    }

    ItemStack createExtraSlotPlaceholder(String slotName) {
        ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        itemStack.editMeta(itemMeta -> itemMeta.itemName(Component.text(slotName)));
        return itemStack;
    }

    abstract H createHolder(Snapshot snapshot);

    abstract H getHolder(Inventory inventory);

    abstract void fill(H holder);

    abstract ItemStack createSwitchViewButton();

    abstract void openNextView(InventorySnapshotViewer viewer, Player player, Snapshot snapshot);

}

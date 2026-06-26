package net.chunkful.inventoryrestore.format.button;

import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.Service;
import net.chunkful.inventoryrestore.service.SortOrder;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public final class ListButton implements Button<ListButton> {

    private static final String SUBCOMMAND_NAME = "list";
    static final ButtonEncoder<ListButton, String> COMMAND_ENCODER = new CommandEncoder();
    static final ButtonEncoder<ListButton, BinaryTag> PAYLOAD_ENCODER = new PayloadEncoder();
    static final ButtonDecoder<ListButton, BinaryTag> PAYLOAD_DECODER = new PayloadDecoder();

    private final UUID playerUniqueId;
    private final Snapshot.@Nullable Cause causeFilter;
    private final int page;
    private final SortOrder sortOrder;

    public ListButton(UUID playerUniqueId, Snapshot.@Nullable Cause causeFilter, int page, SortOrder sortOrder) {
        this.playerUniqueId = playerUniqueId;
        this.causeFilter = causeFilter;
        this.page = page;
        this.sortOrder = sortOrder;
    }

    @Override
    public ButtonType<ListButton> type() {
        return ButtonType.LIST;
    }

    @Override
    public void execute(Player player, Service service) {
        service.showSnapshotList(player, playerUniqueId, causeFilter, sortOrder, page);
    }

    private static final class CommandEncoder implements ButtonEncoder<ListButton, String> {

        @Override
        public String encode(ListButton button) {
            StringBuilder commandBuilder = new StringBuilder();
            commandBuilder.append(SUBCOMMAND_NAME)
                    .append(" ")
                    .append(button.playerUniqueId)
                    .append(" ")
                    .append(button.page)
                    .append(" ")
                    .append("--sort")
                    .append(" ")
                    .append(button.sortOrder.name());
            if (button.causeFilter != null)
                commandBuilder.append(" ").append("--cause").append(" ").append(button.causeFilter.name().toLowerCase());
            return commandBuilder.toString();
        }
    }

    private static final class PayloadEncoder implements ButtonEncoder<ListButton, BinaryTag> {

        @Override
        public BinaryTag encode(ListButton button) {
            CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder()
                    .putString("playerUniqueId", button.playerUniqueId.toString())
                    .putInt("page", button.page)
                    .putString("sortOrder", button.sortOrder.name());
            if(button.causeFilter != null) {
                builder.putString("causeFilter", button.causeFilter.name().toLowerCase());
            }
            return builder.build();
        }
    }

    private static final class PayloadDecoder implements ButtonDecoder<ListButton, BinaryTag> {

        @Override
        public ListButton decode(BinaryTag input) {
            CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) input;
            UUID playerUniqueId = UUID.fromString(compoundBinaryTag.getString("playerUniqueId"));
            int page = compoundBinaryTag.getInt("page");
            SortOrder sortOrder = SortOrder.valueOf(compoundBinaryTag.getString("sortOrder"));
            Snapshot.Cause causeFilter = null;
            if(compoundBinaryTag.contains("causeFilter")) {
                causeFilter = Snapshot.Cause.valueOf(compoundBinaryTag.getString("causeFilter").toUpperCase());
            }

            return new ListButton(playerUniqueId, causeFilter, page, sortOrder);
        }
    }



}

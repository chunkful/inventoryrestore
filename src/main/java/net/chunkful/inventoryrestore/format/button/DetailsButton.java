package net.chunkful.inventoryrestore.format.button;

import net.chunkful.inventoryrestore.service.Service;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class DetailsButton implements Button<DetailsButton> {

    private static final String SUBCOMMAND_NAME = "details";
    static final ButtonEncoder<DetailsButton, String> COMMAND_ENCODER = button -> SUBCOMMAND_NAME + " " + button.snapshotId.toString();
    static final ButtonEncoder<DetailsButton, BinaryTag> PAYLOAD_ENCODER = button -> StringBinaryTag.stringBinaryTag(button.snapshotId.toString());
    static final ButtonDecoder<DetailsButton, BinaryTag> PAYLOAD_DECODER = input -> new DetailsButton(UUID.fromString(((StringBinaryTag) input).value()));

    private final UUID snapshotId;

    public DetailsButton(UUID snapshotId) {
        this.snapshotId = snapshotId;
    }

    @Override
    public ButtonType<DetailsButton> type() {
        return ButtonType.DETAILS;
    }

    @Override
    public void execute(Player player, Service service) {
        service.showSnapshotDetails(player, snapshotId);
    }
}

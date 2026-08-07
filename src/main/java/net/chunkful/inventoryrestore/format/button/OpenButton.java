package net.chunkful.inventoryrestore.format.button;

import net.chunkful.inventoryrestore.service.Service;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class OpenButton implements Button<OpenButton> {

    private static final String SUBCOMMAND_NAME = "open";
    static final ButtonEncoder<OpenButton, String> COMMAND_ENCODER = button -> SUBCOMMAND_NAME + " " + button.snapshotId.toString();
    static final ButtonEncoder<OpenButton, BinaryTag> PAYLOAD_ENCODER = button -> StringBinaryTag.stringBinaryTag(button.snapshotId.toString());
    static final ButtonDecoder<OpenButton, BinaryTag> PAYLOAD_DECODER = input -> new OpenButton(UUID.fromString(((StringBinaryTag) input).value()));

    private final UUID snapshotId;

    public OpenButton(UUID snapshotId) {
        this.snapshotId = snapshotId;
    }

    @Override
    public ButtonType<OpenButton> type() {
        return ButtonType.OPEN;
    }

    @Override
    public void execute(Player player, Service service) {
        if(!player.hasPermission("inventoryrestore.open")) return;
        service.openSnapshot(player, snapshotId);
    }
}

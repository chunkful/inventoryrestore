package net.chunkful.inventoryrestore.format.button;

import net.chunkful.inventoryrestore.service.Service;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public final class RestoreButton implements Button<RestoreButton> {

    private static final String SUBCOMMAND_NAME = "restore";
    static final ButtonEncoder<RestoreButton, String> COMMAND_ENCODER = new CommandEncoder();
    static final ButtonEncoder<RestoreButton, BinaryTag> PAYLOAD_ENCODER = new PayloadEncoder();
    static final ButtonDecoder<RestoreButton, BinaryTag> PAYLOAD_DECODER = new PayloadDecoder();

    private final UUID snapshotId;
    @Nullable private final UUID targetPlayerUniqueId;
    private final boolean skipTeleport;

    public RestoreButton(UUID snapshotId, @Nullable UUID targetPlayerUniqueId, boolean skipTeleport) {
        this.snapshotId = snapshotId;
        this.targetPlayerUniqueId = targetPlayerUniqueId;
        this.skipTeleport = skipTeleport;
    }

    @Override
    public ButtonType<RestoreButton> type() {
        return ButtonType.RESTORE;
    }

    @Override
    public void execute(Player player, Service service) {
        if(!player.hasPermission("inventoryrestore.restore")) return;
        service.restoreSnapshot(player, snapshotId, !skipTeleport);
    }

    private static final class CommandEncoder implements ButtonEncoder<RestoreButton, String> {

        @Override
        public String encode(RestoreButton button) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(SUBCOMMAND_NAME);
            stringBuilder.append(" ");
            stringBuilder.append(button.snapshotId);
            if(button.targetPlayerUniqueId != null) {
                stringBuilder.append(" ");
                stringBuilder.append("--target");
                stringBuilder.append(" ");
                stringBuilder.append(button.targetPlayerUniqueId);
            }
            if(button.skipTeleport) {
                stringBuilder.append(" ");
                stringBuilder.append("--no-teleport");
            }
            return stringBuilder.toString();
        }
    }

    private static final class PayloadEncoder implements ButtonEncoder<RestoreButton, BinaryTag> {

        @Override
        public BinaryTag encode(RestoreButton button) {
            CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder()
                    .putString("snapshotId", button.snapshotId.toString())
                    .putBoolean("skipTeleport", button.skipTeleport);
            if(button.targetPlayerUniqueId != null) {
                builder.putString("targetPlayerUniqueId", button.targetPlayerUniqueId.toString());
            }
            return builder.build();
        }
    }

    private static final class PayloadDecoder implements ButtonDecoder<RestoreButton, BinaryTag> {

        @Override
        public RestoreButton decode(BinaryTag input) {
            CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) input;
            UUID snapshotId = UUID.fromString(compoundBinaryTag.getString("snapshotId"));
            UUID targetPlayerUniqueId = null;
            if(compoundBinaryTag.contains("targetPlayerUniqueId")) {
                targetPlayerUniqueId = UUID.fromString(compoundBinaryTag.getString("targetPlayerUniqueId"));
            }
            boolean skipTeleport = compoundBinaryTag.getBoolean("skipTeleport");
            return new RestoreButton(snapshotId, targetPlayerUniqueId, skipTeleport);
        }
    }

}

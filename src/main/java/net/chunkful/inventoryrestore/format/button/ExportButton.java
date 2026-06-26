package net.chunkful.inventoryrestore.format.button;

import net.chunkful.inventoryrestore.service.Service;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ExportButton implements Button<ExportButton> {

    private static final String SUBCOMMAND_NAME = "export";
    static final ButtonEncoder<ExportButton, String> COMMAND_ENCODER = new CommandEncoder();
    static final ButtonEncoder<ExportButton, BinaryTag> PAYLOAD_ENCODER = new PayloadEncoder();
    static final ButtonDecoder<ExportButton, BinaryTag> PAYLOAD_DECODER = new PayloadDecoder();

    private final UUID snapshotId;
    private final boolean skipInventory;
    private final boolean skipEnderchest;

    public ExportButton(UUID snapshotId, boolean skipInventory, boolean skipEnderchest) {
        this.snapshotId = snapshotId;
        this.skipInventory = skipInventory;
        this.skipEnderchest = skipEnderchest;
    }

    @Override
    public ButtonType<ExportButton> type() {
        return ButtonType.EXPORT;
    }

    @Override
    public void execute(Player player, Service service) {
        service.exportSnapshot(player, snapshotId, skipInventory, skipEnderchest);
    }

    private static final class CommandEncoder implements ButtonEncoder<ExportButton, String> {

        @Override
        public String encode(ExportButton button) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(SUBCOMMAND_NAME);
            stringBuilder.append(" ");
            stringBuilder.append(button.snapshotId.toString());
            if(button.skipInventory) {
                stringBuilder.append(" ");
                stringBuilder.append("--skip-inventory");
            }
            if(button.skipEnderchest) {
                stringBuilder.append(" ");
                stringBuilder.append("--skip-enderchest");
            }
            return stringBuilder.toString();
        }
    }

    private static final class PayloadEncoder implements ButtonEncoder<ExportButton, BinaryTag> {

        @Override
        public BinaryTag encode(ExportButton button) {
            return CompoundBinaryTag.builder()
                    .putString("snapshotId", button.snapshotId.toString())
                    .putBoolean("skipInventory", button.skipInventory)
                    .putBoolean("skipEnderchest", button.skipEnderchest)
                    .build();
        }
    }

    private static final class PayloadDecoder implements ButtonDecoder<ExportButton, BinaryTag> {

        @Override
        public ExportButton decode(BinaryTag input) throws Exception {
            CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) input;
            UUID snapshotId = UUID.fromString(compoundBinaryTag.getString("snapshotId"));
            boolean skipInventory = compoundBinaryTag.getBoolean("skipInventory");
            boolean skipEnderchest = compoundBinaryTag.getBoolean("skipEnderchest");
            return new ExportButton(snapshotId, skipInventory, skipEnderchest);
        }
    }

}

package net.chunkful.inventoryrestore.format.button;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.BinaryTag;

import java.util.Set;

public final class ButtonType<B extends Button<B>> {

    public static final ButtonType<ListButton> LIST = new ButtonType<>(Key.key("inventoryrestore", "list"), ListButton.COMMAND_ENCODER, ListButton.PAYLOAD_ENCODER, ListButton.PAYLOAD_DECODER);
    public static final ButtonType<DetailsButton> DETAILS = new ButtonType<>(Key.key("inventoryrestore", "details"), DetailsButton.COMMAND_ENCODER, DetailsButton.PAYLOAD_ENCODER, DetailsButton.PAYLOAD_DECODER);
    public static final ButtonType<OpenButton> OPEN = new ButtonType<>(Key.key("inventoryrestore", "open"), OpenButton.COMMAND_ENCODER, OpenButton.PAYLOAD_ENCODER, OpenButton.PAYLOAD_DECODER);
    public static final ButtonType<ExportButton> EXPORT = new ButtonType<>(Key.key("inventoryrestore", "export"), ExportButton.COMMAND_ENCODER, ExportButton.PAYLOAD_ENCODER, ExportButton.PAYLOAD_DECODER);
    public static final ButtonType<RestoreButton> RESTORE = new ButtonType<>(Key.key("inventoryrestore", "restore"), RestoreButton.COMMAND_ENCODER, RestoreButton.PAYLOAD_ENCODER, RestoreButton.PAYLOAD_DECODER);

    public static final Set<ButtonType<?>> VALUES = Set.of(LIST, DETAILS, OPEN, EXPORT, RESTORE);

    private final Key identifier;
    private final ButtonEncoder<B, String> commandEncoder;
    private final ButtonEncoder<B, BinaryTag> payloadEncoder;
    private final ButtonDecoder<B, BinaryTag> payloadDecoder;

    ButtonType(
            Key identifier, ButtonEncoder<B, String> commandEncoder,
            ButtonEncoder<B, BinaryTag> payloadEncoder,
            ButtonDecoder<B, BinaryTag> payloadDecoder
    ) {
        this.identifier = identifier;
        this.commandEncoder = commandEncoder;
        this.payloadEncoder = payloadEncoder;
        this.payloadDecoder = payloadDecoder;
    }

    public Key identifier() {
        return identifier;
    }

    public Button<?> decodePayload(BinaryTag input) throws Exception {
        return this.payloadDecoder.decode(input);
    }

    public BinaryTag encodePayload(Button<?> button) {
        return this.payloadEncoder.encode((B) button);
    }

    public String encodeCommand(Button<?> button) {
        return this.commandEncoder.encode((B) button);
    }

    public static ButtonType<?> byIdentifier(Key identifier) {
        for (ButtonType<?> buttonType : VALUES) {
            if (buttonType.identifier.equals(identifier)) {
                return buttonType;
            }
        }
        return null;
    }
}

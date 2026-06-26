package net.chunkful.inventoryrestore.config;

import net.chunkful.inventoryrestore.format.button.Button;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.ClickEvent;

public enum ButtonMode {
    RUN_COMMAND((button, pluginConfiguration) -> ClickEvent.runCommand(pluginConfiguration.buttonCommandRoot() + " " +button.type().encodeCommand(button))),
    SUGGEST_COMMAND((button, pluginConfiguration) -> ClickEvent.suggestCommand("/" + pluginConfiguration.buttonCommandRoot() + " " + button.type().encodeCommand(button))),
    PAYLOAD((button, _) -> ClickEvent.custom(button.type().identifier(), BinaryTagHolder.binaryTagHolder(TagStringIO.tagStringIO().asString(button.type().encodePayload(button)))));

    private final ClickEventFactory clickEventFactory;

    ButtonMode(ClickEventFactory clickEventFactory) {
        this.clickEventFactory = clickEventFactory;
    }

    public ClickEvent create(Button<?> button, PluginConfiguration pluginConfiguration) throws Exception {
        return clickEventFactory.create(button, pluginConfiguration);
    }

    @FunctionalInterface
    interface ClickEventFactory {
        ClickEvent create(Button<?> button, PluginConfiguration pluginConfiguration) throws Exception;
    }
}

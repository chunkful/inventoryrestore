package net.chunkful.inventoryrestore.storage.sql;

import org.bukkit.inventory.ItemStack;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class ItemStackArrayDeserializer extends StdDeserializer<ItemStack[]> {

    public ItemStackArrayDeserializer() {
        super(ItemStack[].class);
    }

    @Override
    public ItemStack[] deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        return ItemStack.deserializeItemsFromBytes(p.getBinaryValue());
    }
}

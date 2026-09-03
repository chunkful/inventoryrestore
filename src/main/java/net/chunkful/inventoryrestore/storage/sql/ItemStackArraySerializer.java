package net.chunkful.inventoryrestore.storage.sql;

import org.bukkit.inventory.ItemStack;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public final class ItemStackArraySerializer extends StdSerializer<ItemStack[]> {

    public ItemStackArraySerializer() {
        super(ItemStack[].class);
    }

    @Override
    public void serialize(ItemStack[] value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeBinary(ItemStack.serializeItemsAsBytes(value));
    }
}

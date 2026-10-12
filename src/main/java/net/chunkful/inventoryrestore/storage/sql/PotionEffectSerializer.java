package net.chunkful.inventoryrestore.storage.sql;

import org.bukkit.potion.PotionEffect;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public final class PotionEffectSerializer extends StdSerializer<PotionEffect> {

    public PotionEffectSerializer() {
        super(PotionEffect.class);
    }

    @Override
    public void serialize(PotionEffect value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        ctxt.writeValue(gen, value.serialize());
    }
}

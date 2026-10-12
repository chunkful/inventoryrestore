package net.chunkful.inventoryrestore.storage.sql;

import org.bukkit.potion.PotionEffect;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Map;

public final class PotionEffectDeserializer extends StdDeserializer<PotionEffect> {

    public PotionEffectDeserializer() {
        super(PotionEffect.class);
    }

    @Override
    public PotionEffect deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        Map<String, Object> map = ctxt.readValue(p, Map.class);
        return new PotionEffect(map);
    }
}

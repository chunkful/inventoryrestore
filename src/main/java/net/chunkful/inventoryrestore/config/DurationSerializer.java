package net.chunkful.inventoryrestore.config;

import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationSerializer implements TypeSerializer<Duration> {

    @Override
    public Duration deserialize(Type type, ConfigurationNode node) {
        String string = node.getString();
        if(string == null) return null;
        return Duration.parse(string);
    }

    @Override
    public void serialize(Type type, @Nullable Duration obj, ConfigurationNode node) throws SerializationException {
        if(obj == null) {
            node.set(null);
        }   else    {
            node.set(obj.toString());
        }
    }
}

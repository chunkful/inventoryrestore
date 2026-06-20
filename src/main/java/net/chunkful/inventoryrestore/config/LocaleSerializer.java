package net.chunkful.inventoryrestore.config;

import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Locale;

public class LocaleSerializer implements TypeSerializer<Locale> {
    @Override
    public Locale deserialize(Type type, ConfigurationNode node) {
        String string = node.getString();
        if (string == null) {
            return Locale.getDefault();
        }

        return Locale.forLanguageTag(string);
    }

    @Override
    public void serialize(Type type, @Nullable Locale obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
        } else {
            node.set(obj.toLanguageTag());
        }
    }
}

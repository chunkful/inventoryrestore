package net.chunkful.inventoryrestore.storage.sql;

import org.bukkit.Location;
import tools.jackson.databind.util.StdConverter;

import java.util.Map;

public final class LocationDeserializingConverter extends StdConverter<Map<String, Object>, Location> {

    @Override
    public Location convert(Map<String, Object> value) {
        return Location.deserialize(value);
    }
}

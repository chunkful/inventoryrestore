package net.chunkful.inventoryrestore.storage.sql;

import org.bukkit.Location;
import tools.jackson.databind.util.StdConverter;

import java.util.Map;

public final class LocationSerializingConverter extends StdConverter<Location, Map<String, Object>> {

    @Override
    public Map<String, Object> convert(Location value) {
        return value.serialize();
    }
}

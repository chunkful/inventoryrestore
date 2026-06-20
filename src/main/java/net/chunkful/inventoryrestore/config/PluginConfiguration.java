package net.chunkful.inventoryrestore.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Locale;
import java.util.Set;

@ConfigSerializable
public class PluginConfiguration {

    private StorageType storageType = StorageType.IN_MEMORY;
    private String connectionString = "mongodb://localhost:27017/";
    private String databaseName = "inventoryrestore";
    private Set<Locale> locales = Set.of(Locale.US);

    public PluginConfiguration() {}

    public StorageType storageType() {
        return storageType;
    }

    public String connectionString() {
        return connectionString;
    }

    public String databaseName() {
        return databaseName;
    }

    public Set<Locale> locales() {
        return locales;
    }
}

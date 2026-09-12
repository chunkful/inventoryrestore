package net.chunkful.inventoryrestore.config;

import net.chunkful.inventoryrestore.storage.InMemoryStorage;
import net.chunkful.inventoryrestore.storage.mongodb.MongoDbStorage;
import net.chunkful.inventoryrestore.storage.nitrite.NitriteStorage;
import net.chunkful.inventoryrestore.storage.Storage;
import net.chunkful.inventoryrestore.storage.sql.SQLStorage;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;

public enum StorageType {
    IN_MEMORY((_, _) -> new InMemoryStorage()),
    NITRITE((_, plugin) -> new NitriteStorage(plugin)),
    MONGODB((pluginConfiguration, _) -> new MongoDbStorage(pluginConfiguration.connectionString(), pluginConfiguration.databaseName())),
    SQL((pluginConfiguration, _) -> new SQLStorage(pluginConfiguration.connectionString()));

    private final BiFunction<PluginConfiguration, Plugin, Storage> storageFactory;

    StorageType(BiFunction<PluginConfiguration, Plugin, Storage> storageFactory) {
        this.storageFactory = storageFactory;
    }

    public Storage create(PluginConfiguration pluginConfiguration, Plugin plugin) {
        return storageFactory.apply(pluginConfiguration, plugin);
    }
}

package net.chunkful.inventoryrestore.storage.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import net.chunkful.inventoryrestore.lifecycle.Lifecycled;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.SortOrder;
import net.chunkful.inventoryrestore.storage.Storage;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDbStorage implements Storage, Lifecycled {

    private final String connectionString;
    private final String databaseName;

    private MongoClient mongoClient;
    private MongoCollection<Snapshot> collection;

    public MongoDbStorage(String connectionString, String databaseName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
    }

    @Override
    public void storeSnapshot(Snapshot snapshot) {
        collection.insertOne(snapshot);
    }

    @Override
    public Optional<Snapshot> getSnapshotById(UUID snapshotId) {
        return Optional.ofNullable(collection.find(Filters.eq("_id", snapshotId), Snapshot.class).first());
    }

    @Override
    public List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder) {
        return collection.find(Filters.eq("playerUniqueId", playerUniqueId))
                .skip(offset)
                .limit(limit)
                .sort(sortOrder == SortOrder.ASCENDING ? Sorts.ascending("createdAt") : Sorts.descending("createdAt"))
                .into(new ArrayList<>());
    }

    @Override
    public List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder, Snapshot.Cause cause) {
        return collection.find(
                    Filters.and(
                            Filters.eq("playerUniqueId", playerUniqueId),
                            Filters.eq("cause", cause)
                    )
                )
                .skip(offset)
                .limit(limit)
                .sort(sortOrder == SortOrder.ASCENDING ? Sorts.ascending("createdAt") : Sorts.descending("createdAt"))
                .into(new ArrayList<>());
    }

    @Override
    public void load() {
        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(
                        new MapConverterCodec<>(PotionEffect.class, PotionEffect::serialize, PotionEffect::new),
                        new MapConverterCodec<>(Location.class, Location::serialize, Location::deserialize),
                        new ItemStackCodec(),
                        new ItemStackArrayCodec())
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(registry)
                .build();
        mongoClient = MongoClients.create(settings);
        collection = mongoClient.getDatabase(databaseName).getCollection("snapshots", Snapshot.class);
    }

    @Override
    public void unload() {
        mongoClient.close();
    }
}

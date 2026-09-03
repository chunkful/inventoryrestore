package net.chunkful.inventoryrestore.storage.sql;

import net.chunkful.inventoryrestore.lifecycle.Lifecycled;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.SortOrder;
import net.chunkful.inventoryrestore.storage.Storage;
import net.chunkful.inventoryrestore.storage.sql.jooq.Tables;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SQLStorage implements Storage, Lifecycled {

    private static final Logger log = LoggerFactory.getLogger(SQLStorage.class);

    private final String connectionString;

    private SQLDialect dialect;
    private DataSource dataSource;
    private DSLContext dslContext;

    public SQLStorage(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public void load() {
        SQLConfigurer sqlConfigurer = new SQLConfigurer();
        this.dialect = sqlConfigurer.dialect(connectionString);
        this.dataSource = sqlConfigurer.dataSource(connectionString, dialect);
        this.dslContext = sqlConfigurer.dslContext(dataSource, dialect);

        migrate();
    }

    @Override
    public void unload() {
        if(dataSource instanceof Closeable closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("Failed to close SQL data source", e);
            }
        }
    }

    private void migrate() {
        Flyway.configure(getClass().getClassLoader())
                .dataSource(dataSource)
                .locations("classpath:sql-migrations/common")
                .locations("classpath:sql-migrations/" + dialect.getNameLC())
                .load()
                .migrate();
    }

    @Override
    public void storeSnapshot(Snapshot snapshot) {
        try {
            dslContext.insertInto(Tables.SNAPSHOTS)
                    .set(Tables.SNAPSHOTS.ID, snapshot.snapshotId())
                    .set(Tables.SNAPSHOTS.CREATED_AT, snapshot.createdAt())
                    .set(Tables.SNAPSHOTS.PLAYER, snapshot.playerUniqueId())
                    .set(Tables.SNAPSHOTS.CAUSE, snapshot.cause())
                    .set(Tables.SNAPSHOTS.DATA, snapshot)
                    .execute();
        }   catch (Exception e) {
            log.error("Failed to store snapshot", e);
        }
    }

    @Override
    public Optional<Snapshot> getSnapshotById(UUID snapshotId) {
        try {
            return dslContext.select(Tables.SNAPSHOTS.DATA)
                    .from(Tables.SNAPSHOTS)
                    .where(Tables.SNAPSHOTS.ID.eq(snapshotId))
                    .fetchOptional(Tables.SNAPSHOTS.DATA);
        } catch (Exception e) {
            log.error("Failed to get snapshot by id", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder) {
        try {
            return dslContext.select(Tables.SNAPSHOTS.DATA)
                    .from(Tables.SNAPSHOTS)
                    .where(Tables.SNAPSHOTS.PLAYER.eq(playerUniqueId))
                    .orderBy(Tables.SNAPSHOTS.CREATED_AT.desc())
                    .limit(limit)
                    .offset(offset)
                    .fetch(Tables.SNAPSHOTS.DATA);
        }   catch (Exception e) {
            log.error("Failed to get snapshots by player", e);
            return List.of();
        }
    }

    @Override
    public List<Snapshot> getRecentSnapshotsOfPlayer(UUID playerUniqueId, int offset, int limit, SortOrder sortOrder, Snapshot.Cause cause) {
        try {
            return dslContext.select(Tables.SNAPSHOTS.DATA)
                    .from(Tables.SNAPSHOTS)
                    .where(Tables.SNAPSHOTS.PLAYER.eq(playerUniqueId))
                    .and(Tables.SNAPSHOTS.CAUSE.eq(cause))
                    .orderBy(Tables.SNAPSHOTS.CREATED_AT.desc())
                    .limit(limit)
                    .offset(offset)
                    .fetch(Tables.SNAPSHOTS.DATA);
        }   catch (Exception e) {
            log.error("Failed to get snapshots by player", e);
            return List.of();
        }
    }
}

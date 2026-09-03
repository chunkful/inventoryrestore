package net.chunkful.inventoryrestore.storage.sql;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;

import java.time.Instant;

public final class InstantConverter implements Converter<Long, Instant> {

    @Override
    public Instant from(Long databaseObject) {
        return (databaseObject == null) ? null : Instant.ofEpochMilli(databaseObject);
    }

    @Override
    public Long to(Instant userObject) {
        return (userObject == null) ? null : userObject.toEpochMilli();
    }

    @Override
    public @NotNull Class<Long> fromType() {
        return Long.class;
    }

    @Override
    public @NotNull Class<Instant> toType() {
        return Instant.class;
    }
}

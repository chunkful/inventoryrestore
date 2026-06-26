package net.chunkful.inventoryrestore.service;

import net.chunkful.inventoryrestore.model.Snapshot;

import java.util.Comparator;

public enum SortOrder {
    ASCENDING,
    DESCENDING;

    public static Comparator<Snapshot> snapshotComparator(SortOrder sortOrder) {
        return switch (sortOrder) {
            case ASCENDING -> Comparator.comparing(Snapshot::createdAt);
            case DESCENDING -> Comparator.comparing(Snapshot::createdAt).reversed();
        };
    }

}

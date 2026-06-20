package net.chunkful.inventoryrestore.storage.nitrite;

import net.chunkful.inventoryrestore.model.Snapshot;
import org.dizitart.no2.repository.EntityDecorator;
import org.dizitart.no2.repository.EntityId;
import org.dizitart.no2.repository.EntityIndex;

import java.util.List;

public class SnapshotDecorator implements EntityDecorator<Snapshot> {

    @Override
    public Class<Snapshot> getEntityType() {
        return Snapshot.class;
    }

    @Override
    public EntityId getIdField() {
        return new EntityId("snapshotId");
    }

    @Override
    public List<EntityIndex> getIndexFields() {
        return List.of();
    }
}

package net.chunkful.inventoryrestore.storage.mongodb;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.inventory.ItemStack;

public class ItemStackCodec implements Codec<ItemStack> {

    @Override
    public ItemStack decode(BsonReader reader, DecoderContext decoderContext) {
        return ItemStack.deserializeBytes(reader.readBinaryData().getData());
    }

    @Override
    public void encode(BsonWriter writer, ItemStack value, EncoderContext encoderContext) {
        writer.writeBinaryData(new BsonBinary(value.serializeAsBytes()));
    }

    @Override
    public Class<ItemStack> getEncoderClass() {
        return ItemStack.class;
    }
}

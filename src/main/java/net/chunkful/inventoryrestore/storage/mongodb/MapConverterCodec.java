package net.chunkful.inventoryrestore.storage.mongodb;

import com.mongodb.Function;
import com.mongodb.MongoClientSettings;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.Map;

public class MapConverterCodec<T> implements Codec<T> {

    private final Class<T> clazz;
    private final Function<T, Map<String, Object>> toMapFunction;
    private final Function<Map<String, Object>, T> fromMapFunction;

    private final Codec<Document> documentCodec;

    public MapConverterCodec(Class<T> clazz, Function<T, Map<String, Object>> toMapFunction, Function<Map<String, Object>, T> fromMapFunction) {
        this.clazz = clazz;
        this.toMapFunction = toMapFunction;
        this.fromMapFunction = fromMapFunction;
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry()
                .get(Document.class);
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        return fromMapFunction.apply(documentCodec.decode(reader, decoderContext));
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        documentCodec.encode(writer, new Document(toMapFunction.apply(value)), encoderContext);
    }

    @Override
    public Class<T> getEncoderClass() {
        return clazz;
    }
}

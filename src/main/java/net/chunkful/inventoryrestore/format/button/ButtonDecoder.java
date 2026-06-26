package net.chunkful.inventoryrestore.format.button;

public interface ButtonDecoder<B extends Button<B>, I> {

    B decode(I input) throws Exception;

}

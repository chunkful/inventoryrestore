package net.chunkful.inventoryrestore.format.button;

public interface ButtonEncoder<B extends Button, O> {

    O encode(B button);

}

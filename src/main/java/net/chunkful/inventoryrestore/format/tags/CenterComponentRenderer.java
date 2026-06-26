package net.chunkful.inventoryrestore.format.tags;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.VirtualComponentRenderer;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

public class CenterComponentRenderer implements VirtualComponentRenderer<Void> {

    private final ComponentLike inner;
    private final Component frame;

    public CenterComponentRenderer(ComponentLike inner, Component frame) {
        this.inner = inner;
        this.frame = frame;
    }

    @Override
    public @UnknownNullability ComponentLike apply(@NonNull Void context) {
        return ComponentCentering.center(inner.asComponent(), frame);
    }
}

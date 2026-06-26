package net.chunkful.inventoryrestore.format.tag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public class CenterTag implements Modifying {

    public static TagResolver RESOLVER = TagResolver.resolver("center", (aq, ctx) ->
            new CenterTag(ctx.deserialize(aq.popOr("Center tag requires frame component").value())));

    private final Component frame;

    private CenterTag(Component frame) {
        this.frame = frame;
    }

    @Override
    public Component apply(@NotNull Component current, int depth) {
        if (depth > 0) return Component.empty();
        return ComponentCentering.center(current, frame);
    }

}

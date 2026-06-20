package net.chunkful.inventoryrestore.command;

import net.chunkful.inventoryrestore.lifecycle.PluginLifecycler;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;

import java.util.Optional;
import java.util.UUID;

public class SnapshotParser implements ArgumentParser<Source, Snapshot> {

    private final PluginLifecycler pluginLifecycler;

    public SnapshotParser(PluginLifecycler pluginLifecycler) {
        this.pluginLifecycler = pluginLifecycler;
    }

    @Override
    public @NonNull ArgumentParseResult<Snapshot> parse(@NonNull CommandContext<Source> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.readString();

        UUID uuid;
        try {
            uuid = UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            return ArgumentParseResult.failure(e);
        }

        Optional<Snapshot> optionalSnapshot = pluginLifecycler.bindings().storage().getSnapshotById(uuid).join();

        return optionalSnapshot.map(ArgumentParseResult::success).orElseGet(() -> ArgumentParseResult.failure(new IllegalArgumentException("Snapshot not found")));

    }
}

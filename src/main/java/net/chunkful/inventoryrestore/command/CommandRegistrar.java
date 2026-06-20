package net.chunkful.inventoryrestore.command;

import net.chunkful.inventoryrestore.lifecycle.PluginLifecycler;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.ParserDescriptor;

public class CommandRegistrar {

    private final Plugin owningPlugin;
    private final PaperCommandManager<Source> commandManager;
    private final PluginLifecycler pluginLifecycler;

    public CommandRegistrar(Plugin owningPlugin, PluginLifecycler pluginLifecycler) {
        this.owningPlugin = owningPlugin;
        this.commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
                .buildOnEnable(owningPlugin);
        this.pluginLifecycler = pluginLifecycler;
    }

    public void registerCommands() {
        MinecraftExceptionHandler.create(Source::source).defaultHandlers().registerTo(commandManager);

        ParserDescriptor<Source, Snapshot> snapshotParserDescriptor = ParserDescriptor.parserDescriptor(new SnapshotParser(pluginLifecycler), Snapshot.class);
        commandManager.parserRegistry().registerParser(snapshotParserDescriptor);
        commandManager.command(new Commands(owningPlugin, pluginLifecycler, snapshotParserDescriptor));
    }

}

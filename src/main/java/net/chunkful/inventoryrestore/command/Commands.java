package net.chunkful.inventoryrestore.command;

import net.kyori.adventure.text.Component;
import net.chunkful.inventoryrestore.lifecycle.PluginLifecycler;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandFactory;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.flag.CommandFlag;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Commands implements CommandFactory<Source> {

    private final Plugin owningPlugin;
    private final PluginLifecycler pluginLifecycler;
    private final ParserDescriptor<Source, Snapshot> snapshotParser;

    public Commands(Plugin owningPlugin, PluginLifecycler pluginLifecycler, ParserDescriptor<Source, Snapshot> parserDescriptor) {
        this.owningPlugin = owningPlugin;
        this.pluginLifecycler = pluginLifecycler;
        this.snapshotParser = parserDescriptor;
    }

    @Override
    public @NonNull List<@NonNull Command<? extends Source>> createCommands(@NonNull CommandManager<Source> commandManager) {
        Command.Builder<Source> rootCommand = commandManager.commandBuilder("inventoryrestore", "invrestore", "ir");

        return List.of(
            reload(rootCommand), list(rootCommand), save(rootCommand), details(rootCommand), restore(rootCommand), export(rootCommand), open(rootCommand)
        );
    }

    private Command<Source> reload(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("reload")
                .permission("inventoryrestore.reload")
                .handler((ctx) -> {
                    pluginLifecycler.unload();
                    pluginLifecycler.load();
                    ctx.sender().source().sendMessage(Component.translatable("ir.reloaded"));
                })
                .build();
    }

    private Command<Source> list(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("list")
                .permission("inventoryrestore.list")
                .required("target", OfflinePlayerParser.offlinePlayerParser())
                .handler((ctx) -> {
                    OfflinePlayer target = ctx.get("target");
                    List<Snapshot> snapshots = pluginLifecycler.bindings().storage().getRecentSnapshotsOfPlayer(target.getUniqueId()).join();

                    snapshots.forEach(snapshot -> {
                        ctx.sender().source().sendMessage(pluginLifecycler.bindings().formatter().formatList(snapshot));
                    });
                })
                .build();
    }

    private Command<Source> save(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("save")
                .permission("inventoryrestore.save")
                .required("target", OfflinePlayerParser.offlinePlayerParser())
                .handler(sync((ctx) -> {
                    OfflinePlayer target = ctx.get("target");
                    pluginLifecycler.bindings().recordingService().recordAndSave(target.getUniqueId(), Snapshot.Cause.MANUAL);
                    ctx.sender().source().sendMessage(Component.translatable("ir.saved"));
                }))
                .build();
    }

    private Command<Source> details(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("details")
                .permission("inventoryrestore.details")
                .required("snapshot", snapshotParser)
                .handler((ctx) -> {
                    Snapshot snapshot = ctx.get("snapshot");
                    ctx.sender().source().sendMessage(pluginLifecycler.bindings().formatter().formatDetails(snapshot));
                })
                .build();
    }

    private Command<Source> restore(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("restore")
                .permission("inventoryrestore.restore")
                .required("snapshot", snapshotParser)
                .flag(CommandFlag.<Source>builder("target").withComponent(OfflinePlayerParser.offlinePlayerParser()).build())
                .flag(CommandFlag.builder("no-teleport").build())
                .handler(sync((ctx) -> {
                    Snapshot snapshot = ctx.get("snapshot");
                    UUID target = ctx.flags().<OfflinePlayer>getValue("target").map(OfflinePlayer::getUniqueId).orElse(snapshot.playerUniqueId());
                    boolean noTeleport = ctx.flags().hasFlag("no-teleport");
                    pluginLifecycler.bindings().snapshotRestorer().restoreSnapshot(target, snapshot, !noTeleport);
                    ctx.sender().source().sendMessage(Component.translatable("ir.restored"));
                }))
                .build();
    }

    private Command<PlayerSource> export(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("export")
                .senderType(PlayerSource.class)
                .permission("inventoryrestore.export")
                .required("snapshot", snapshotParser)
                .flag(CommandFlag.builder("skip-inventory").build())
                .flag(CommandFlag.builder("skip-enderchest").build())
                .handler(sync(ctx -> {
                    Snapshot snapshot = ctx.get("snapshot");
                    boolean skipInventory = ctx.flags().hasFlag("skip-inventory");
                    boolean skipEnderchest = ctx.flags().hasFlag("skip-enderchest");
                    pluginLifecycler.bindings().snapshotExporter().export(snapshot, skipInventory, skipEnderchest);
                    ctx.sender().source().sendMessage(Component.translatable("ir.exported"));
                }))
                .build();
    }

    private Command<PlayerSource> open(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("open")
                .senderType(PlayerSource.class)
                .permission("inventoryrestore.open")
                .required("snapshot", snapshotParser)
                .handler(sync((ctx) -> {
                    Snapshot snapshot = ctx.get("snapshot");
                    pluginLifecycler.bindings().snapshotViewer().openInventory(ctx.sender().source(), snapshot);
                }))
                .build();
    }

    private <C extends Source>CommandExecutionHandler<C> sync(Consumer<CommandContext<C>> context) {
        return commandContext -> Bukkit.getScheduler().runTask(owningPlugin, () -> context.accept(commandContext));
    }
}

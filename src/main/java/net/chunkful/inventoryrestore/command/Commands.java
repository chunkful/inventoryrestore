package net.chunkful.inventoryrestore.command;

import net.chunkful.inventoryrestore.service.SortOrder;
import net.kyori.adventure.text.Component;
import net.chunkful.inventoryrestore.lifecycle.PluginLifecycler;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandFactory;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.List;
import java.util.UUID;

public class Commands implements CommandFactory<Source> {

    private final PluginLifecycler pluginLifecycler;
    private final ParserDescriptor<Source, Snapshot> snapshotParser;

    public Commands(PluginLifecycler pluginLifecycler, ParserDescriptor<Source, Snapshot> parserDescriptor) {
        this.pluginLifecycler = pluginLifecycler;
        this.snapshotParser = parserDescriptor;
    }

    @Override
    public @NonNull List<@NonNull Command<? extends Source>> createCommands(@NonNull CommandManager<Source> commandManager) {
        Command.Builder<Source> rootCommand = commandManager.commandBuilder("inventoryrestore", "invrestore", "ir");

        return List.of(
            reload(rootCommand), list(rootCommand), save(rootCommand), details(rootCommand), restore(rootCommand), export(rootCommand), open(rootCommand), debug(rootCommand)
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
                .required("target", BetterOfflinePlayerParser.offlinePlayerParser())
                .required("page", IntegerParser.integerParser(1))
                .flag(CommandFlag.<Source>builder("cause").withComponent(EnumParser.enumParser(Snapshot.Cause.class)))
                .flag(CommandFlag.<Source>builder("sort").withComponent(EnumParser.enumParser(SortOrder.class)))
                .handler((ctx) -> {
                    OfflinePlayer target = ctx.get("target");
                    int page = ctx.get("page");
                    Snapshot.Cause cause = ctx.flags().get("cause");
                    SortOrder sortOrder = ctx.flags().getValue("sort", SortOrder.DESCENDING);
                    pluginLifecycler.bindings().service().showSnapshotList(ctx.sender().source(), target.getUniqueId(), cause, sortOrder, page);
                })
                .build();
    }

    private Command<Source> save(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("save")
                .permission("inventoryrestore.save")
                .required("target", BetterOfflinePlayerParser.offlinePlayerParser())
                .handler((ctx) -> {
                    OfflinePlayer target = ctx.get("target");
                    pluginLifecycler.bindings().service().recordAndSave(target.getUniqueId(), Snapshot.Cause.MANUAL);
                    ctx.sender().source().sendMessage(Component.translatable("ir.saved"));
                })
                .build();
    }

    private Command<Source> details(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("details")
                .permission("inventoryrestore.details")
                .required("snapshot", snapshotParser)
                .handler((ctx) -> {
                    Snapshot snapshot = ctx.get("snapshot");
                    pluginLifecycler.bindings().service().showSnapshotDetails(ctx.sender().source(), snapshot);
                })
                .build();
    }

    private Command<Source> restore(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("restore")
                .permission("inventoryrestore.restore")
                .required("snapshot", snapshotParser)
                .flag(CommandFlag.<Source>builder("target").withComponent(BetterOfflinePlayerParser.offlinePlayerParser()).build())
                .flag(CommandFlag.builder("no-teleport").build())
                .handler((ctx) -> {
                    Snapshot snapshot = ctx.get("snapshot");
                    UUID target = ctx.flags().<OfflinePlayer>getValue("target").map(OfflinePlayer::getUniqueId).orElse(snapshot.playerUniqueId());
                    boolean noTeleport = ctx.flags().hasFlag("no-teleport");
                    pluginLifecycler.bindings().service().restoreSnapshot(ctx.sender().source(), snapshot, target, !noTeleport);
                    ctx.sender().source().sendMessage(Component.translatable("ir.restored"));
                })
                .build();
    }

    private Command<PlayerSource> export(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("export")
                .senderType(PlayerSource.class)
                .permission("inventoryrestore.export")
                .required("snapshot", snapshotParser)
                .flag(CommandFlag.builder("skip-inventory").build())
                .flag(CommandFlag.builder("skip-enderchest").build())
                .handler(ctx -> {
                    Snapshot snapshot = ctx.get("snapshot");
                    boolean skipInventory = ctx.flags().hasFlag("skip-inventory");
                    boolean skipEnderchest = ctx.flags().hasFlag("skip-enderchest");
                    pluginLifecycler.bindings().service().exportSnapshot(ctx.sender().source(), snapshot, skipInventory, skipEnderchest);
                    ctx.sender().source().sendMessage(Component.translatable("ir.exported"));
                })
                .build();
    }

    private Command<PlayerSource> open(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("open")
                .senderType(PlayerSource.class)
                .permission("inventoryrestore.open")
                .required("snapshot", snapshotParser)
                .handler(ctx -> {
                    Snapshot snapshot = ctx.get("snapshot");
                    pluginLifecycler.bindings().service().openSnapshot(ctx.sender().source(), snapshot);
                })
                .build();
    }

    private Command<Source> debug(Command.Builder<Source> rootCommand) {
        return rootCommand.literal("debug")
                .permission("inventoryrestore.debug")
                .handler((ctx) -> {
                    ctx.sender().source().sendMessage("*chirp* *chirp*");
                })
                .build();
    }
}

package net.chunkful.inventoryrestore.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.object.ObjectContents;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;

public class Formatter {

    public Component formatList(Snapshot snapshot) {
        return Component.translatable("ir.list.entry")
                .arguments(
                        Argument.component("cause", formatCause(snapshot.cause())),
                        Argument.component("timestamp", formatTimestamp(snapshot.createdAt())),
                        Argument.component("button_details", formatDetailsButton(snapshot.snapshotId()))
                );
    }

    public Component formatDetails(Snapshot snapshot) {
        return Component.translatable("ir.details")
                .arguments(
                        Argument.component("id", formatId(snapshot)),
                        Argument.component("player", formatPlayer(snapshot.playerUniqueId())),
                        Argument.component("cause", formatCause(snapshot.cause())),
                        Argument.component("timestamp", formatTimestamp(snapshot.createdAt())),
                        Argument.component("button_restore", formatRestoreButton(snapshot.snapshotId())),
                        Argument.component("button_open", formatOpenButton(snapshot.snapshotId())),
                        Argument.component("button_export", formatExportButton(snapshot.snapshotId()))
                );
    }

    private Component formatId(Snapshot snapshot) {
        return Component.text(snapshot.snapshotId().toString())
                .clickEvent(ClickEvent.copyToClipboard(snapshot.snapshotId().toString()));
    }

    private Component formatTimestamp(Instant createdAt) {
        ZonedDateTime zonedDateTime = createdAt.atZone(ZoneId.systemDefault());
        String format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(zonedDateTime);

        Duration duration = Duration.between(createdAt, Instant.now());
        String formattedDuration = DurationFormatUtils.formatDuration(duration.toMillis(), "dd:HH:mm:ss", true);

        return Component.text(format)
                .hoverEvent(Component.text()
                        .append(Component.text(formattedDuration))
                        .append(Component.text(" ago"))
                        .build()
                )
                .clickEvent(ClickEvent.copyToClipboard(format));
    }

    private Component formatCause(Snapshot.Cause cause) {
        return switch (cause) {
            case JOIN -> Component.translatable("ir.cause.join");
            case QUIT -> Component.translatable("ir.cause.quit");
            case DEATH -> Component.translatable("ir.cause.death");
            case SCHEDULE -> Component.translatable("ir.cause.schedule");
            case MANUAL -> Component.translatable("ir.cause.manual");
        };
    }

    private Component formatPlayer(UUID playerUniqueId) {
        String name = Bukkit.getOfflinePlayer(playerUniqueId).getName();

        return Component.text()
                .append(Component.object(ObjectContents.playerHead(name)))
                .appendSpace()
                .append(Component.text(name, NamedTextColor.WHITE))
                .clickEvent(ClickEvent.copyToClipboard(playerUniqueId.toString()))
                .build();
    }

    private Component formatDetailsButton(UUID snapshotId) {
        return Component.text("Details", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(Component.text("Click to view details")))
                .clickEvent(ClickEvent.runCommand("inventoryrestore details " + snapshotId.toString()));
    }

    private Component formatRestoreButton(UUID snapshotId) {
        return Component.text("Restore", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(Component.text("Click to restore")))
                .clickEvent(ClickEvent.runCommand("inventoryrestore restore " + snapshotId.toString()));
    }

    private Component formatExportButton(UUID snapshotId) {
        return Component.text("Export", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(Component.text("Click to export")))
                .clickEvent(ClickEvent.runCommand("inventoryrestore export " + snapshotId.toString()));
    }

    private Component formatOpenButton(UUID snapshotId) {
        return Component.text("Open", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(Component.text("Click to open")))
                .clickEvent(ClickEvent.runCommand("inventoryrestore open " + snapshotId.toString()));
    }

}

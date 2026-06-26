package net.chunkful.inventoryrestore.format;

import com.google.common.collect.Lists;
import net.chunkful.inventoryrestore.config.PluginConfiguration;
import net.chunkful.inventoryrestore.format.button.*;
import net.chunkful.inventoryrestore.service.SortOrder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.object.ObjectContents;
import net.chunkful.inventoryrestore.model.Snapshot;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class Format {

    private static final Logger log = LoggerFactory.getLogger(Format.class);
    private final PluginConfiguration pluginConfiguration;

    public Format(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    public Component formatList(
            List<Snapshot> snapshotPage,
            int pageNumber,
            UUID playerUniqueId,
            Snapshot.Cause cause,
            SortOrder sortOrder
    ) {
        return Component.empty()
                .append(
                        Component.translatable(
                                "ir.list.header",
                                Argument.component("player", formatPlayer(playerUniqueId)),
                                Argument.numeric("page", pageNumber)
                        )
                ).appendNewline()
                .append(
                        snapshotPage.isEmpty() ?
                                Component.translatable("ir.list.empty") :
                                Component.join(JoinConfiguration.newlines(), Lists.transform(snapshotPage, this::formatEntry))
                ).appendNewline()
                .append(
                        Component.translatable(
                                "ir.list.footer",
                                Argument.component("button_previous", formatPreviousPageButton(playerUniqueId, cause, sortOrder, pageNumber - 1)),
                                Argument.component("button_next", formatNextPageButton(playerUniqueId, cause, sortOrder, pageNumber + 1))
                        )
                );
    }

    private Component formatEntry(Snapshot snapshot) {
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

        Duration duration = Duration.between(createdAt, Instant.now());
        String formattedDuration = DurationFormatUtils.formatDuration(duration.toMillis(), "dd:HH:mm:ss", true);

        return Component.translatable(
                "ir.timestamp",
                        Argument.tagResolver(Formatter.date("date", zonedDateTime)),
                        Argument.string("duration", formattedDuration)
                );
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
        try {
            return Component.text("Details", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to view details")))
                    .clickEvent(pluginConfiguration.buttonMode().create(new DetailsButton(snapshotId), pluginConfiguration));
        } catch (Exception e) {
            log.error("Failed to create details button for snapshot {}", snapshotId, e);
            return Component.text("Error", NamedTextColor.RED);
        }
    }

    private Component formatRestoreButton(UUID snapshotId) {
        try {
            return Component.text("Restore", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to restore")))
                    .clickEvent(pluginConfiguration.buttonMode().create(new RestoreButton(snapshotId, null, false), pluginConfiguration));
        } catch (Exception e) {
            log.error("Failed to create restore button for snapshot {}", snapshotId, e);
            return Component.text("Error", NamedTextColor.RED);
        }
    }

    private Component formatExportButton(UUID snapshotId) {
        try {
            return Component.text("Export", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to export")))
                    .clickEvent(pluginConfiguration.buttonMode().create(new ExportButton(snapshotId, false, false), pluginConfiguration));
        } catch (Exception e) {
            log.error("Failed to create export button for snapshot {}", snapshotId, e);
            return Component.text("Error", NamedTextColor.RED);
        }
    }

    private Component formatOpenButton(UUID snapshotId) {
        try {
            return Component.text("Open", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to open")))
                    .clickEvent(pluginConfiguration.buttonMode().create(new OpenButton(snapshotId), pluginConfiguration));
        } catch (Exception e) {
            log.error("Failed to create open button for snapshot {}", snapshotId, e);
            return Component.text("Error", NamedTextColor.RED);
        }
    }

    private Component formatPreviousPageButton(
            UUID playerUniqueId,
            Snapshot.Cause cause,
            SortOrder sortOrder,
            int pageNumber
    ) {
        return formatPageButton(Component.translatable("ir.previous_page"), playerUniqueId, cause, sortOrder, pageNumber);
    }

    private Component formatNextPageButton(
            UUID playerUniqueId,
            Snapshot.Cause cause,
            SortOrder sortOrder,
            int pageNumber
    ) {
        return formatPageButton(Component.translatable("ir.next_page"), playerUniqueId, cause, sortOrder, pageNumber);
    }

    private Component formatPageButton(
            TranslatableComponent component,
            UUID playerUniqueId,
            Snapshot.Cause cause,
            SortOrder sortOrder,
            int pageNumber
    ) {
        try {
            return component.clickEvent(pluginConfiguration.buttonMode().create(new ListButton(playerUniqueId, cause, pageNumber, sortOrder), pluginConfiguration));
        } catch (Exception e) {
            log.error("Failed to create page button for player {} with cause {} and page number {}", playerUniqueId, cause, pageNumber, e);
            return Component.text("Error", NamedTextColor.RED);
        }
    }

}

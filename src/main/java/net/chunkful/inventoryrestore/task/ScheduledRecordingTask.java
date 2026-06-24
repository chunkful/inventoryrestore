package net.chunkful.inventoryrestore.task;

import net.chunkful.inventoryrestore.config.PluginConfiguration;
import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.Service;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.WeakHashMap;

public class ScheduledRecordingTask implements Runnable {

    private final PluginConfiguration pluginConfiguration;
    private final Service service;
    private final Map<Player, Instant> lastRecorded = new WeakHashMap<>();

    public ScheduledRecordingTask(PluginConfiguration pluginConfiguration, Service service) {
        this.pluginConfiguration = pluginConfiguration;
        this.service = service;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(shouldRecord(player)) {
                service.recordAndSave(player.getUniqueId(), Snapshot.Cause.SCHEDULE);
                this.lastRecorded.put(player, Instant.now());
            }
        });
    }

    public boolean shouldRecord(Player player) {
        Instant lastRecorded = this.lastRecorded.computeIfAbsent(player, _ -> Instant.now());
        return Duration.between(lastRecorded, Instant.now()).compareTo(pluginConfiguration.scheduledRecordingInterval()) > 0;
    }
}

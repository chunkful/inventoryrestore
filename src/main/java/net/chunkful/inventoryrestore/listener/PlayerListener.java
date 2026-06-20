package net.chunkful.inventoryrestore.listener;

import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.RecordingService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

public class PlayerListener implements Listener {

    private final RecordingService recordingService;

    @Inject
    public PlayerListener(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        recordingService.recordAndSave(event.getPlayer(), Snapshot.Cause.JOIN);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        recordingService.recordAndSave(event.getPlayer(), Snapshot.Cause.QUIT);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        recordingService.recordAndSave(event.getPlayer(), Snapshot.Cause.DEATH);
    }

}

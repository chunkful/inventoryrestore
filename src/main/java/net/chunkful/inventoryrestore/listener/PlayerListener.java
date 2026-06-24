package net.chunkful.inventoryrestore.listener;

import net.chunkful.inventoryrestore.model.Snapshot;
import net.chunkful.inventoryrestore.service.Service;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

public class PlayerListener implements Listener {

    private final Service service;

    @Inject
    public PlayerListener(Service service) {
        this.service = service;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        service.recordAndSave(event.getPlayer().getUniqueId(), Snapshot.Cause.JOIN);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        service.recordAndSave(event.getPlayer().getUniqueId(), Snapshot.Cause.QUIT);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        service.recordAndSave(event.getPlayer().getUniqueId(), Snapshot.Cause.DEATH);
    }

}

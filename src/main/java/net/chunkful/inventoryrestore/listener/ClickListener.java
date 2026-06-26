package net.chunkful.inventoryrestore.listener;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.chunkful.inventoryrestore.format.button.Button;
import net.chunkful.inventoryrestore.format.button.ButtonType;
import net.chunkful.inventoryrestore.service.Service;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClickListener implements Listener {

    private final Service service;

    public ClickListener(Service service) {
        this.service = service;
    }

    @EventHandler
    private void onPlayerClick(PlayerCustomClickEvent event) {
        ButtonType buttonType = ButtonType.byIdentifier(event.getIdentifier());
        if(buttonType == null) return;

        if(!(event.getCommonConnection() instanceof PlayerGameConnection playerGameConnection)) {
            return;
        }
        Player player = playerGameConnection.getPlayer();

        try {
            BinaryTag binaryTag = TagStringIO.tagStringIO().asTag(event.getTag().string());
            Button<?> button = buttonType.decodePayload(binaryTag);
            button.execute(player, service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

package net.chunkful.inventoryrestore.format.button;

import net.chunkful.inventoryrestore.service.Service;
import org.bukkit.entity.Player;

public interface Button<B extends Button<B>> {

    ButtonType<B> type();

    void execute(Player player, Service service);

}

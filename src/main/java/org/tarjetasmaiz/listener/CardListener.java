package org.tarjetasmaiz.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.tarjetasmaiz.TarjetasMaiz;

public class CardListener implements Listener {

    private final TarjetasMaiz plugin;

    public CardListener(TarjetasMaiz plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().name().contains("BANNER")) {
            String itemName = event.getItemInHand().getItemMeta().getDisplayName();
            if (itemName.contains("TARJETA AMARILLA") || itemName.contains("TARJETA ROJA")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getLanguageManager().getMessage("cannot_place_card"));
            }
        }
    }
}

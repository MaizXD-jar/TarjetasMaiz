package org.tarjetasmaiz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TarjetasMaiz extends JavaPlugin implements CommandExecutor, Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Integer> tarjetaCount = new HashMap<>();

    @Override
    public void onEnable() {
        getCommand("tarjeta").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tarjetasmaiz.Give")) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("give")) {
            player.sendMessage(ChatColor.RED + "Uso correcto: /tarjeta give <amarilla|roja>");
            return true;
        }

        String color = args[1].toLowerCase();
        ItemStack item = new ItemStack(Material.YELLOW_BANNER);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            player.sendMessage(ChatColor.RED + "Error al crear la tarjeta.");
            return true;
        }

        if (color.equals("amarilla")) {
            meta.setDisplayName(ChatColor.YELLOW + "Tarjeta Amarilla");
        } else if (color.equals("roja")) {
            meta.setDisplayName(ChatColor.RED + "Tarjeta Roja");
        } else {
            player.sendMessage(ChatColor.RED + "Color inválido. Usa 'amarilla' o 'roja'.");
            return true;
        }

        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GREEN + "Has recibido una " + color + " tarjeta.");
        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("tarjetasmaiz.Execute")) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.YELLOW_BANNER) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        String color = ChatColor.stripColor(meta.getDisplayName()).toLowerCase();
        UUID playerUUID = player.getUniqueId();

        if (color.equals("tarjeta amarilla")) {
            handleYellowCard(player, playerUUID);
        } else if (color.equals("tarjeta roja")) {
            handleRedCard(player, playerUUID);
        }
    }

    private void handleYellowCard(Player player, UUID playerUUID) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(playerUUID, 0L);

        if (currentTime - lastUsed < 3000) {
            player.sendMessage(ChatColor.YELLOW + "Debes esperar " + ChatColor.RED + ((3000 - (currentTime - lastUsed)) / 1000.0) + " segundos" + ChatColor.YELLOW + " antes de usar la tarjeta amarilla de nuevo.");
            return;
        }

        cooldowns.put(playerUUID, currentTime);

        int count = tarjetaCount.getOrDefault(playerUUID, 0);
        if (count == 0) {
            player.sendTitle(ChatColor.YELLOW + "1/2", ChatColor.YELLOW + "TARJETA AMARILLA", 10, 70, 20);
            Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " ha recibido una tarjeta amarilla, 1/2");
        } else {
            player.sendTitle(ChatColor.YELLOW + "2/2", ChatColor.YELLOW + "TARJETA AMARILLA", 10, 70, 20);
            Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " ha recibido una tarjeta amarilla, 2/2");
            player.kickPlayer(ChatColor.RED + "Has sido expulsado por 10 minutos por acumulación de tarjetas amarillas.");
            tarjetaCount.put(playerUUID, 0);
        }

        tarjetaCount.put(playerUUID, count + 1);
    }

    private void handleRedCard(Player player, UUID playerUUID) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(playerUUID, 0L);

        if (currentTime - lastUsed < 3000) {
            player.sendMessage(ChatColor.YELLOW + "Debes esperar " + ChatColor.RED + ((3000 - (currentTime - lastUsed)) / 1000.0) + " segundos" + ChatColor.YELLOW + " antes de usar la tarjeta roja de nuevo.");
            return;
        }

        cooldowns.put(playerUUID, currentTime);
        player.sendTitle(ChatColor.YELLOW + "2/2", ChatColor.RED + "TARJETA ROJA", 10, 70, 20);
        Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " ha recibido una tarjeta roja.");
        player.kickPlayer(ChatColor.RED + "Has sido expulsado por 10 minutos por recibir una tarjeta roja.");
    }
}

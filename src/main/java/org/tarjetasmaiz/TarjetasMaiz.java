package org.tarjetasmaiz;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TarjetasMaiz extends JavaPlugin implements CommandExecutor, Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Integer> tarjetaCount = new HashMap<>();
    private int maxTarjetasAmarillas;
    private long cooldownTarjetaAmarilla;
    private long cooldownTarjetaRoja;
    private long duracionExpulsion;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getCommand("tarjeta").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadConfig() {
        maxTarjetasAmarillas = getConfig().getInt("maxTarjetasAmarillas", 2);
        cooldownTarjetaAmarilla = getConfig().getLong("cooldownTarjetaAmarilla", 3000);
        cooldownTarjetaRoja = getConfig().getLong("cooldownTarjetaRoja", 3000);
        duracionExpulsion = getConfig().getLong("duracionExpulsion", 600000);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("tarjetasmaiz.give")) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("give")) {
            player.sendMessage(ChatColor.RED + "Uso correcto: /tarjeta give <amarilla|roja>");
            return true;
        }

        String color = args[1].toLowerCase();
        ItemStack item;
        ItemMeta meta;
        if (color.equals("amarilla") || color.equals("yellow")) {
            item = new ItemStack(Material.YELLOW_BANNER);
            meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage(ChatColor.RED + "Error al crear la tarjeta.");
                return true;
            }
            meta.setDisplayName(ChatColor.YELLOW + "TARJETA AMARILLA");
        } else if (color.equals("roja") || color.equals("red")) {
            item = new ItemStack(Material.RED_BANNER);
            meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage(ChatColor.RED + "Error al crear la tarjeta.");
                return true;
            }
            meta.setDisplayName(ChatColor.RED + "TARJETA ROJA");
        } else {
            player.sendMessage(ChatColor.RED + "Color inválido. Usa 'amarilla' o 'roja'.");
            return true;
        }
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GREEN + "Has recibido una tarjeta " + color + ".");
        return true;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        Player target = (Player) event.getRightClicked();
        if (player.equals(target)) {
            return;
        }

        if (!player.hasPermission("tarjetasmaiz.execute")) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.YELLOW_BANNER && item.getType() != Material.RED_BANNER) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        String color = ChatColor.stripColor(meta.getDisplayName()).toLowerCase();
        UUID targetUUID = target.getUniqueId();
        if (color.contains("amarilla") || color.contains("yellow")) {
            handleYellowCard(target, targetUUID, player);
        } else if (color.contains("roja") || color.contains("red")) {
            handleRedCard(target, targetUUID, player);
        }
    }

    private void handleYellowCard(Player target, UUID targetUUID, Player issuer) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(targetUUID, 0L);
        if (currentTime - lastUsed < cooldownTarjetaAmarilla) {
            issuer.sendMessage(ChatColor.YELLOW + "Debes esperar " + cooldownTarjetaAmarilla / 1000.0 + " segundos antes de usar la tarjeta amarilla de nuevo.");
            return;
        }

        cooldowns.put(targetUUID, currentTime);
        int count = tarjetaCount.getOrDefault(targetUUID, 0);
        if (count == 0) {
            target.sendTitle(ChatColor.YELLOW + "1/" + maxTarjetasAmarillas, ChatColor.YELLOW + "TARJETA AMARILLA", 10, 70, 20);
            Bukkit.broadcastMessage(ChatColor.YELLOW + target.getName() + " ha recibido una tarjeta amarilla, 1/" + maxTarjetasAmarillas);
        } else {
            target.sendTitle(ChatColor.YELLOW + "2/" + maxTarjetasAmarillas, ChatColor.YELLOW + "TARJETA AMARILLA", 10, 70, 20);
            Bukkit.broadcastMessage(ChatColor.YELLOW + target.getName() + " ha recibido una tarjeta amarilla, 2/" + maxTarjetasAmarillas);
            banPlayer(target, ChatColor.RED + "Has sido expulsado por 10 minutos por acumulación de tarjetas amarillas.");
            tarjetaCount.put(targetUUID, 0);
        }
        tarjetaCount.put(targetUUID, count + 1);
    }

    private void handleRedCard(Player target, UUID targetUUID, Player issuer) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(targetUUID, 0L);
        if (currentTime - lastUsed < cooldownTarjetaRoja) {
            issuer.sendMessage(ChatColor.YELLOW + "Debes esperar " + cooldownTarjetaRoja / 1000.0 + " segundos antes de usar la tarjeta roja de nuevo.");
            return;
        }

        cooldowns.put(targetUUID, currentTime);
        target.sendTitle(ChatColor.RED + "TARJETA ROJA", ChatColor.RED + "TARJETA ROJA", 10, 70, 20);
        Bukkit.broadcastMessage(ChatColor.RED + target.getName() + " ha recibido una tarjeta roja.");
        banPlayer(target, ChatColor.RED + "Has sido expulsado por 10 minutos por recibir una tarjeta roja.");
    }

    private void banPlayer(Player player, String reason) {
        Date unbanDate = new Date(System.currentTimeMillis() + duracionExpulsion);
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason, unbanDate, null);
        player.kickPlayer(reason);
    }
}

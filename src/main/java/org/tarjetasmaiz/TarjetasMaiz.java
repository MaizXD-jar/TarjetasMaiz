package org.tarjetasmaiz;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

public final class TarjetasMaiz extends JavaPlugin implements CommandExecutor, Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Integer> tarjetaCount = new HashMap<>();
    private Properties messages;
    private int maxYellowCards;
    private long yellowCardCooldown;
    private long redCardCooldown;
    private long banDuration;
    private Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        saveDefaultConfig();
        loadConfig();
        loadMessages();

        getCommand("tarjeta").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        maxYellowCards = config.getInt("maxYellowCards", 2);
        yellowCardCooldown = config.getLong("yellowCardCooldown", 3000);
        redCardCooldown = config.getLong("redCardCooldown", 3000);
        banDuration = config.getLong("banDuration", 600000);
    }

    private void loadMessages() {
        String lang = getConfig().getString("language", "en_us");
        messages = new Properties();
        try (InputStream input = getResource("lang_" + lang + ".properties")) {
            if (input == null) {
                logger.severe("Language file not found: lang_" + lang + ".properties");
                return;
            }
            messages.load(input);
        } catch (IOException ex) {
            logger.severe("Error loading language file: " + ex.getMessage());
            ex.printStackTrace();  // Optionally log the stack trace if needed
        }
    }

    private String getMessage(String key, Object... args) {
        String message = messages.getProperty(key, key);
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("command.onlyPlayers"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tarjetasmaiz.Give")) {
            player.sendMessage(getMessage("command.noPermission"));
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("give")) {
            player.sendMessage(getMessage("command.usage"));
            return true;
        }

        String color = args[1].toLowerCase();
        ItemStack item;
        ItemMeta meta;

        if (color.equals("amarilla") || color.equals("yellow")) {
            item = new ItemStack(Material.YELLOW_BANNER);
            meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage(getMessage("command.errorCreatingCard"));
                return true;
            }
            meta.setDisplayName(ChatColor.YELLOW + getMessage("command.cardReceived", "yellow"));
        } else if (color.equals("roja") || color.equals("red")) {
            item = new ItemStack(Material.RED_BANNER);
            meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage(getMessage("command.errorCreatingCard"));
                return true;
            }
            meta.setDisplayName(ChatColor.RED + getMessage("command.cardReceived", "red"));
        } else {
            player.sendMessage(getMessage("command.invalidColor"));
            return true;
        }

        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        player.sendMessage(getMessage("command.cardReceived", color));
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

        if (!player.hasPermission("tarjetasmaiz.Execute")) {
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

        String color = ChatColor.stripColor(meta.getDisplayName()).toLowerCase(Locale.ROOT);
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

        if (currentTime - lastUsed < yellowCardCooldown) {
            issuer.sendMessage(getMessage("interact.wait", yellowCardCooldown / 1000.0, getMessage("command.cardReceived", "yellow")));
            return;
        }

        cooldowns.put(targetUUID, currentTime);

        int count = tarjetaCount.getOrDefault(targetUUID, 0);
        if (count == 0) {
            target.sendTitle(getMessage("interact.yellowCardTitle1", maxYellowCards), getMessage("interact.yellowCardSubtitle"), 10, 70, 20);
            Bukkit.broadcastMessage(getMessage("interact.yellowCardBroadcast1", target.getName(), maxYellowCards));
        } else {
            target.sendTitle(getMessage("interact.yellowCardTitle2", maxYellowCards), getMessage("interact.yellowCardSubtitle"), 10, 70, 20);
            Bukkit.broadcastMessage(getMessage("interact.yellowCardBroadcast2", target.getName(), maxYellowCards));
            banPlayer(target, getMessage("ban.yellowReason"));
            tarjetaCount.put(targetUUID, 0);
        }

        tarjetaCount.put(targetUUID, count + 1);
    }

    private void handleRedCard(Player target, UUID targetUUID, Player issuer) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(targetUUID, 0L);

        if (currentTime - lastUsed < redCardCooldown) {
            issuer.sendMessage(getMessage("interact.wait", redCardCooldown / 1000.0, getMessage("command.cardReceived", "red")));
            return;
        }

        cooldowns.put(targetUUID, currentTime);
        target.sendTitle(getMessage("interact.redCardTitle"), getMessage("interact.redCardTitle"), 10, 70, 20);
        Bukkit.broadcastMessage(getMessage("interact.redCardBroadcast", target.getName()));
        banPlayer(target, getMessage("ban.redReason"));
    }

    private void banPlayer(Player player, String reason) {
        Date unbanDate = new Date(System.currentTimeMillis() + banDuration);
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason, unbanDate, null);
        player.kickPlayer(reason);
    }
}

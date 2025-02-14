package org.tarjetasmaiz.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tarjetasmaiz.TarjetasMaiz;


public class CardCommand implements CommandExecutor {

    private final TarjetasMaiz plugin;

    public CardCommand(TarjetasMaiz plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("only_players"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("tarjetasmaiz.give")) {
            player.sendMessage(plugin.getLanguageManager().getMessage("no_permission"));
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("give")) {
            player.sendMessage(plugin.getLanguageManager().getMessage("usage"));
            return true;
        }

        String color = args[1].toLowerCase();
        ItemStack item;
        ItemMeta meta;
        if (color.equals("amarilla") || color.equals("yellow")) {
            item = new ItemStack(Material.valueOf(plugin.getConfigManager().getYellowCardModel()));
            meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage(ChatColor.RED + "Error al crear la tarjeta.");
                return true;
            }
            meta.setDisplayName(ChatColor.YELLOW + plugin.getLanguageManager().getMessage("yellow_card_title"));
        } else if (color.equals("roja") || color.equals("red")) {
            item = new ItemStack(Material.valueOf(plugin.getConfigManager().getRedCardModel()));
            meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage(ChatColor.RED + "Error al crear la tarjeta.");
                return true;
            }
            meta.setDisplayName(ChatColor.RED + plugin.getLanguageManager().getMessage("red_card_title"));
        } else {
            player.sendMessage(plugin.getLanguageManager().getMessage("invalid_color"));
            return true;
        }
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        player.sendMessage(plugin.getLanguageManager().getMessage("card_received").replace("{color}", color));
        return true;
    }
}
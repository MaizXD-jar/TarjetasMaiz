package org.tarjetasmaiz.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.tarjetasmaiz.TarjetasMaiz;

import java.io.File;

public class LanguageManager {

    private final TarjetasMaiz plugin;
    private YamlConfiguration languageConfig;

    public LanguageManager(TarjetasMaiz plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        String language = plugin.getConfigManager().getLanguage();
        File languageFile = new File(plugin.getDataFolder(), "messages_" + language + ".yml");

        if (!languageFile.exists()) {
            plugin.saveResource("messages_" + language + ".yml", false);
        }

        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }

    public String getMessage(String key) {
        String message = languageConfig.getString(key);
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : "Missing message: " + key;
    }
}

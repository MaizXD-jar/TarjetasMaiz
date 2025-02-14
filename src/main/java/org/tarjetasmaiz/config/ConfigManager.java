package org.tarjetasmaiz.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.tarjetasmaiz.TarjetasMaiz;

public class ConfigManager {

    private final TarjetasMaiz plugin;
    private final FileConfiguration config;

    public ConfigManager(TarjetasMaiz plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadConfig();
    }

    public void loadConfig() {
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public String getLanguage() {
        return config.getString("language", "es");
    }

    public String getYellowCardModel() {
        return config.getString("yellowCardModel", "minecraft:yellow_banner");
    }

    public String getRedCardModel() {
        return config.getString("redCardModel", "minecraft:red_banner");
    }
}

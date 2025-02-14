package org.tarjetasmaiz;

import org.bukkit.plugin.java.JavaPlugin;
import org.tarjetasmaiz.config.ConfigManager;
import org.tarjetasmaiz.config.LanguageManager;
import org.tarjetasmaiz.listener.CardListener;
import org.tarjetasmaiz.util.JsonManager;
import org.tarjetasmaiz.command.CardCommand;

public final class TarjetasMaiz extends JavaPlugin {

    private ConfigManager configManager;
    private LanguageManager languageManager;
    private JsonManager jsonManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.languageManager = new LanguageManager(this);
        this.jsonManager = new JsonManager(this);

        getCommand("tarjeta").setExecutor(new CardCommand(this));
        getServer().getPluginManager().registerEvents(new CardListener(this), this);

        // Reiniciar información del JSON al iniciar el plugin
        jsonManager.resetYellowCards();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public JsonManager getJsonManager() {
        return jsonManager;
    }
}

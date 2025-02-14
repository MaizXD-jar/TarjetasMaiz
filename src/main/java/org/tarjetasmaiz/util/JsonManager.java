package org.tarjetasmaiz.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tarjetasmaiz.TarjetasMaiz;
import org.tarjetasmaiz.model.YellowCardHolder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonManager {

    private final TarjetasMaiz plugin;
    private final File jsonFile;
    private final Gson gson;

    public JsonManager(TarjetasMaiz plugin) {
        this.plugin = plugin;
        this.jsonFile = new File(plugin.getDataFolder(), "yellow_cards.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveYellowCards(List<YellowCardHolder> holders) {
        try (Writer writer = new FileWriter(jsonFile)) {
            gson.toJson(holders, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<YellowCardHolder> loadYellowCards() {
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(jsonFile)) {
            return gson.fromJson(reader, new com.google.gson.reflect.TypeToken<List<YellowCardHolder>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void resetYellowCards() {
        saveYellowCards(new ArrayList<>());
    }
}

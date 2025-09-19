package net.tutla.spoofer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpooferConfig {
    private static final String FILE_NAME = "spoof.json";
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getGameDir().resolve("assets").resolve(FILE_NAME);
    private static final String LOCAL_CONF_PATH = "/assets/spoof.json";

    private static List<String> hiddenMods = new ArrayList<>();
    private static HashMap<String, HashMap<String, Object>> spoof = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static int toggleKey = 71;

    public static void load() {
        try (Reader reader = Files.newBufferedReader(CONFIG_FILE)) {
            SpooferAssets data = GSON.fromJson(reader, SpooferAssets.class);

            hiddenMods = data.hiddenMods != null ? new ArrayList<>(data.hiddenMods) : new ArrayList<>();
            toggleKey = data.toggleKey != null ? data.toggleKey : 71;
            spoof = data.spoof != null ? data.spoof : defaultConfig();
        } catch (IOException e) {
            hiddenMods = new ArrayList<>();
            spoof = defaultConfig();
            toggleKey = 71;
        }
    }

    public static void save(SpooferAssets conf) {

        SpooferAssets data = conf;

        System.out.println(CONFIG_FILE);
        System.out.println(GSON.toJson(data).toString());
        try {
            // Make sure the parent directories exist
            Files.createDirectories(CONFIG_FILE.getParent());

            // Now write the file
            try (Writer writer = Files.newBufferedWriter(CONFIG_FILE)) {
                GSON.toJson(conf, writer);
            }

            System.out.println("Saved config to " + CONFIG_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        load();
    }

    public static HashMap<String, HashMap<String, Object>> defaultConfig(){
        try (InputStream stream = SpooferConfig.class.getResourceAsStream(LOCAL_CONF_PATH)) {
            if (stream != null) {
                Gson gson = new Gson();
                InputStreamReader reader = new InputStreamReader(stream);
                SpooferAssets data = gson.fromJson(reader, SpooferAssets.class);
                spoof = data.spoof != null ? data.spoof : new HashMap<>();
            } else {
                hiddenMods = new ArrayList<>();
            }
            return spoof;
        } catch (Exception e) { return new HashMap<>(); }
    }

    public static void addHiddenMod(String mod) {
        if (!hiddenMods.contains(mod)) hiddenMods.add(mod);
        //save();
    }

    public static void removeHiddenMod(String mod) {
        hiddenMods.remove(mod);
        //save();
    }

    public static boolean isHidden(String modId) {
        return hiddenMods != null && hiddenMods.contains(modId);
    }

    public static List<String> getHiddenMods() {
        return hiddenMods;
    }

    public static int getToggleKey() {
        return toggleKey;
    }

    public static void setToggleKey(int key) {
        toggleKey = key;
    }
    public static HashMap<String, HashMap<String, Object>> getSpoof(){
        return spoof;
    }

    public static void setSpoof(HashMap<String, HashMap<String, Object>> data){
        spoof = data;
    }


}

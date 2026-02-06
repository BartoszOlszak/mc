package me.bartus47.multik;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerManager {

    private final Multik plugin;
    private final Map<UUID, Integer> kills = new HashMap<>();
    private File file;
    private FileConfiguration config;

    public PlayerManager(Multik plugin) {
        this.plugin = plugin;
        loadFile();
    }

    public void addKill(UUID playerUUID) {
        kills.put(playerUUID, getKills(playerUUID) + 1);
        saveFile();
    }

    public int getKills(UUID playerUUID) {
        return kills.getOrDefault(playerUUID, 0);
    }

    // Zwraca posortowaną listę top zabójców (Mapa: NazwaGracza -> IlośćZabójstw)
    public LinkedHashMap<String, Integer> getTopKillers(int limit) {
        return kills.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        e -> {
                            String name = Bukkit.getOfflinePlayer(e.getKey()).getName();
                            return (name != null) ? name : "Unknown";
                        },
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private void loadFile() {
        file = new File(plugin.getDataFolder(), "players.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("kills")) {
            for (String key : config.getConfigurationSection("kills").getKeys(false)) {
                kills.put(UUID.fromString(key), config.getInt("kills." + key));
            }
        }
    }

    private void saveFile() {
        if (config == null) return;
        for (Map.Entry<UUID, Integer> entry : kills.entrySet()) {
            config.set("kills." + entry.getKey().toString(), entry.getValue());
        }
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
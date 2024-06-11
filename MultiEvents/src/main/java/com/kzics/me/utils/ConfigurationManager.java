package com.kzics.me.utils;

import com.kzics.me.MultiEvents;
import com.kzics.me.rules.GameEvent;
import com.kzics.me.rules.impl.FarmerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationManager {

    private final MultiEvents plugin;
    private final File eventsFolder;

    public ConfigurationManager(MultiEvents plugin) {
        this.plugin = plugin;
        this.eventsFolder = new File(plugin.getDataFolder(), "events");
        if (!eventsFolder.exists()) {
            eventsFolder.mkdirs();
        }
    }

    public void saveEventConfig(GameEvent<?> event) {
        File eventFile = new File(eventsFolder, event.getName() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(eventFile);

        config.set("name", event.getName());
        config.set("spawnLocations", serializeLocations(event.getSpawnLocations()));

        try {
            config.save(eventFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends GameEvent<?>> T loadEventConfig(Class<T> eventClass, String eventName) {
        File eventFile = new File(eventsFolder, eventName + ".yml");
        if (!eventFile.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(eventFile);
        String name = eventName;
        List<Location> spawnLocations = deserializeLocations(config.getStringList("spawnLocations"));
        List<String> cropsPoints = config.getStringList("cropPoints");
        int time = config.getInt("event-time");
        HashMap<Material, Integer> cropPoints = new HashMap<>();
        for (String s : cropsPoints) {
            String[] parts = s.split(",");
            cropPoints.put(Material.valueOf(parts[0]), Integer.parseInt(parts[1]));
        }
        try {
            return eventClass.getConstructor(String.class, List.class, HashMap.class,int.class)
                    .newInstance(name, spawnLocations, cropPoints, time);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> serializeLocations(List<Location> locations) {
        List<String> serialized = new ArrayList<>();
        for (Location location : locations) {
            serialized.add(location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ());
        }
        return serialized;
    }

    private List<Location> deserializeLocations(List<String> serialized) {
        List<Location> locations = new ArrayList<>();
        for (String s : serialized) {
            String[] parts = s.split(",");
            Location location = new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            locations.add(location);
        }
        return locations;
    }
}



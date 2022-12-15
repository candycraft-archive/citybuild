/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 */
package net.twerion.twicesystem.skypvp.skid.builder;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationBuilder {
    private static final File FILE = new File(TwiceSystem.getInstance().getDataFolder(), "location.yml");
    private static final YamlConfiguration YAML_CONFIGURATION = YamlConfiguration.loadConfiguration((File) FILE);
    private static final Map<String, Location> LOCATION_CACHE = new HashMap<String, Location>();

    public static void saveLocation(Player player, String locationName) {
        player.sendMessage("\u00a7aDiese Location wurde zwischen gespeichert!");
        player.sendMessage("\u00a7aName der Location: \u00a7e" + locationName);
        LOCATION_CACHE.put(locationName, player.getLocation());
    }

    public static void onEnablePlugin() {
        for (String locationName : YAML_CONFIGURATION.getStringList("Locations")) {
            double locationX = YAML_CONFIGURATION.getDouble("Location." + locationName + ".X");
            double locationY = YAML_CONFIGURATION.getDouble("Location." + locationName + ".Y");
            double locationZ = YAML_CONFIGURATION.getDouble("Location." + locationName + ".Z");
            float locationYaw = (float) YAML_CONFIGURATION.getDouble("Location." + locationName + ".Yaw");
            float locationPitch = (float) YAML_CONFIGURATION.getDouble("Location." + locationName + ".Pitch");
            Location location = new Location(Bukkit.getWorld((String) YAML_CONFIGURATION.getString("Location." + locationName + ".World")), locationX, locationY, locationZ);
            location.setYaw(locationYaw);
            location.setPitch(locationPitch);
            LOCATION_CACHE.put(locationName, location);
        }
    }

    public static void onDisablePlugin() {
        ArrayList<String> locations = new ArrayList();
        LOCATION_CACHE.keySet().forEach(location -> locations.add(location));
        YAML_CONFIGURATION.set("Locations", locations);
        for (String locationName : locations) {
            try {
                Location location2 = LOCATION_CACHE.get(locationName);
                YAML_CONFIGURATION.set("Location." + locationName + ".X", (Object) location2.getX());
                YAML_CONFIGURATION.set("Location." + locationName + ".Y", (Object) location2.getY());
                YAML_CONFIGURATION.set("Location." + locationName + ".Z", (Object) location2.getZ());
                YAML_CONFIGURATION.set("Location." + locationName + ".Yaw", (Object) Float.valueOf(location2.getYaw()));
                YAML_CONFIGURATION.set("Location." + locationName + ".Pitch", (Object) Float.valueOf(location2.getPitch()));
                YAML_CONFIGURATION.set("Location." + locationName + ".World", (Object) location2.getWorld().getName());
                LOCATION_CACHE.remove(locationName);
            } catch (Exception location2) {
            }
        }
        try {
            YAML_CONFIGURATION.save(FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOCATION_CACHE.clear();
    }

    public static File getFILE() {
        return FILE;
    }

    public static YamlConfiguration getYAML_CONFIGURATION() {
        return YAML_CONFIGURATION;
    }

    public static Map<String, Location> getLOCATION_CACHE() {
        return LOCATION_CACHE;
    }
}


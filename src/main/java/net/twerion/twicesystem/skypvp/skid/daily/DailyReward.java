/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.configuration.file.YamlConfigurationOptions
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package net.twerion.twicesystem.skypvp.skid.daily;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyReward {
    private final File file = new File(TwiceSystem.getInstance().getDataFolder(), "skid.yml");
    private final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File) this.file);
    private final Map<String, SpecialPlayer> specialPlayerMap = new HashMap<String, SpecialPlayer>();
    private int trippleXpMinutes;
    private int signClicks;
    private boolean event;

    public DailyReward() {
        this.initConfig();
        new BukkitRunnable() {

            public void run() {
                DailyReward.this.saveSignClicks();
                if (DailyReward.this.trippleXpMinutes != 0) {
                    DailyReward.this.trippleXpMinutes--;
                    DailyReward.this.event = true;
                }
                if (DailyReward.this.trippleXpMinutes == 0 && DailyReward.this.event) {
                    DailyReward.this.event = false;
                    Bukkit.broadcastMessage((String) "\u00a78\u27a4 \u00a7cDas \u00a7eTrippelXP Event \u00a7cwurde deaktiviert. :<");
                }
            }
        }.runTaskTimer((Plugin) TwiceSystem.getInstance(), 0L, 1200L);
    }

    public final void addSignClicks() {
        ++this.signClicks;
    }

    public final void saveSignClicks() {
        this.yamlConfiguration.set("Adjuster.SignClicks", (Object) this.signClicks);
        try {
            this.yamlConfiguration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void initConfig() {
        this.yamlConfiguration.addDefault("Players.Names", Arrays.asList("leStylex"));
        try {
            this.yamlConfiguration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> playerNameList = this.yamlConfiguration.getStringList("Players.Names");
        for (String playerName : playerNameList) {
            File file = new File(TwiceSystem.getInstance().getDataFolder() + "//PlayerData", playerName + ".yml");
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File) file);
            if (!file.exists()) {
                yamlConfiguration.addDefault("Player", (Object) playerName);
                yamlConfiguration.addDefault("XPMinutes", (Object) 30);
                yamlConfiguration.addDefault("Cooldown", (Object) 0);
                yamlConfiguration.options().copyDefaults(true);
                try {
                    yamlConfiguration.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int xpMinutes = yamlConfiguration.getInt("XPMinutes");
            long cooldown = yamlConfiguration.getLong("Cooldown");
            SpecialPlayer specialPlayer = new SpecialPlayer(playerName, xpMinutes);
            specialPlayer.setCooldown(cooldown);
            this.specialPlayerMap.put(playerName, specialPlayer);
        }
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getYamlConfiguration() {
        return this.yamlConfiguration;
    }

    public Map<String, SpecialPlayer> getSpecialPlayerMap() {
        return this.specialPlayerMap;
    }

    public int getTrippleXpMinutes() {
        return this.trippleXpMinutes;
    }

    public void setTrippleXpMinutes(int trippleXpMinutes) {
        this.trippleXpMinutes = trippleXpMinutes;
    }

    public int getSignClicks() {
        return this.signClicks;
    }

    public void setSignClicks(int signClicks) {
        this.signClicks = signClicks;
    }

    public boolean isEvent() {
        return this.event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

}


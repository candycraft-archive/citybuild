/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package net.twerion.twicesystem.skypvp.skid.daily;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SpecialPlayer {
    private final String playerName;
    private final int trippleXpMinutes;
    private long cooldown;

    public SpecialPlayer(String playerName, int trippleXpMinutes) {
        this.playerName = playerName;
        this.trippleXpMinutes = trippleXpMinutes;
    }

    public final void boomXp() {
        if (System.currentTimeMillis() > this.cooldown) {
            Bukkit.broadcastMessage((String) ("\u00a78\u27a4 \u00a74" + this.playerName + " \u00a7ahat den Server betreten. Das \u00a7eTrippleXP Event \u00a7ah\u00e4lt nun \u00a76" + (TwiceSystem.getInstance().getDailyReward().getTrippleXpMinutes() + this.trippleXpMinutes) + " Minuten \u00a7alange an."));
            this.cooldown = TimeUnit.DAYS.toMillis(1L) + System.currentTimeMillis();
            this.saveThisEntry();
            TwiceSystem.getInstance().getDailyReward().setTrippleXpMinutes(TwiceSystem.getInstance().getDailyReward().getTrippleXpMinutes() + this.trippleXpMinutes);
        }
    }

    public final void saveThisEntry() {
        File file = new File(TwiceSystem.getInstance().getDataFolder() + "//PlayerData", this.playerName + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File) file);
        yamlConfiguration.set("Cooldown", (Object) this.cooldown);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public int getTrippleXpMinutes() {
        return this.trippleXpMinutes;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }
}


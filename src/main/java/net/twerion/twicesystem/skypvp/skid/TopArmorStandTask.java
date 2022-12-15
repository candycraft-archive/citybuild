/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.v1_13_R2.entity.CraftArmorStand
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.onlyskid.project.NPCPlugin
 *  org.onlyskid.project.npc.NPCEntry
 *  org.onlyskid.project.npc.api.PlayerAPI
 */
package net.twerion.twicesystem.skypvp.skid;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.twerion.twicesystem.skypvp.TwiceSystem;
import net.twerion.twicesystem.skypvp.impl.ItemStackBuilder;
import net.twerion.twicesystem.skypvp.skid.builder.LocationBuilder;
import net.twerion.twicesystem.skypvp.skid.fetch.GameProfileBuilder;
import net.twerion.twicesystem.skypvp.skid.fetch.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TopArmorStandTask {
    private final List<Integer> npcs = new ArrayList<Integer>();
    private final List<ArmorStand> armorStands = new ArrayList<ArmorStand>();
    private boolean updateLocationBuilder;

    public TopArmorStandTask() {
        LocationBuilder.onEnablePlugin();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;
                LocationBuilder.getLOCATION_CACHE().values().forEach(location -> {
                    if (entity.getWorld().getName().equals(location.getWorld().getName()) && entity.getLocation().distance(location) <= 2.0) {
                        entity.remove();
                    }
                });
            }
        }
        new BukkitRunnable() {

            public void run() {
                TopArmorStandTask.this.removeArmorStands();
                AtomicInteger atomicInteger = new AtomicInteger(1);
                TwiceSystem.getInstance().getMysql().getTop10(map -> {
                    if (atomicInteger.get() == 5) {
                        return;
                    }
                    atomicInteger.getAndAdd(1);
                    Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> {
                        int displayRank = 1;
                        for (String player : map.keySet()) {
                            Location location = LocationBuilder.getLOCATION_CACHE().get("rtop" + displayRank);
                            if (location == null) continue;
                            if (location.getWorld() == null) {
                                if (!TopArmorStandTask.this.updateLocationBuilder) {
                                    LocationBuilder.onDisablePlugin();
                                    LocationBuilder.onEnablePlugin();
                                    for (World world : Bukkit.getWorlds()) {
                                        for (Entity entity : world.getEntities()) {
                                            if (!(entity instanceof ArmorStand)) continue;
                                            LocationBuilder.getLOCATION_CACHE().values().forEach(location1 -> {
                                                if (entity.getLocation().distance(location1) <= 2.0) {
                                                    entity.remove();
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    return;
                                }
                            }
                            Location hologramLocation = new Location(location.getWorld(), location.getX(), location.getY() + 0.2, location.getZ());
                            Location hologramLocation1 = new Location(hologramLocation.getWorld(), hologramLocation.getX(), hologramLocation.getY() + 0.35, hologramLocation.getZ());
                            Location hologramLocation2 = new Location(hologramLocation1.getWorld(), hologramLocation1.getX(), hologramLocation1.getY() + 0.35, hologramLocation1.getZ());
                            String skinValue = "eyJ0aW1lc3RhbXAiOjE0NDg2MzQ1ODcyNDIsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMzMyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUxNjNkYWZhYzFkOTFhOGM5MWRiNTc2Y2FhYzc4NDMzNjc5MWE2ZTE4ZDhmN2Y2Mjc3OGZjNDdiZjE0NmI2In19fQ==";
                            String skinSignature = "eosGuYbzqNK8UBllFajP+cVBbak0Qnqt2NafONsp7U18WIlVajvoKl0ulnEYlAPXKmV4X2B4XF0KklKHT4jwf4sa1HNLyJfYu7lyxJPKGFrYpdGEL2LAiMRKrP7U/b2ZmQHQQ8cKmPScsf/ltLggG9J0mQBxrd4VuoSIje21Y7QxXOMU80QdGyG8MlBqoEM15zUYlsYp2+nTpK0SqaRdJLymdHK9rdShl+hAgti8FKrRgS5Sr/OjICz6QXSLUrvpis7HoZk47GL/D+SmNNmtv+pT95UZz0ObYP3iEFer7ZHsKLS4d6qGe8qcyu+CLJ3cvFf66kuqOc8YbPjzHETVT9KrZkDRkFWoyL4jgcjFMc2l70wLL4Uhz94Rb7flBVeT4BXCoXjn9hXT2oZbRdtQsXMG87Bik0MGeqoQgtqaeCi8uwOeW2kbjl4eD7iley9I4axq+J74tYZL9+734fLdK0LMLZ2ncy3vGc4Kj2NFoKSttFyA3Vei6YeUg6b2hTiDS+meVsdpdO6NkQVWG3dlpENZk0zm+p3U33KMNWcjAxXzsWmw+a68ktUjuAbj1oZniVkPI6VPMm21/a1N92mVBKFQMX2rj1oiUIByIZe/iHPQe3luRiRBLIUwU1L+0LEzdPpq0eM2R6PQj8GNz/fxbpTCy+qcf1Pe9/4HoDGLxE8=";
                            DecimalFormat decimalFormat = new DecimalFormat();
                            if (player == null) {
                                player = "Unknwon";
                            }
                            GameProfile gameProfile = null;
                            try {
                                gameProfile = GameProfileBuilder.fetch(UUIDFetcher.getUUID(player));
                                Property textures = gameProfile.getProperties().get("textures").iterator().next();
                                skinValue = textures.getValue();
                                skinSignature = textures.getSignature();
                            } catch (Exception textures) {
                                // empty catch block
                            }
                            ArmorStand hologram2 = (ArmorStand) location.getWorld().spawn(hologramLocation2, ArmorStand.class);
                            hologram2.setVisible(false);
                            ((CraftArmorStand) hologram2).setGravity(false);
                            hologram2.setCustomNameVisible(true);
                            hologram2.setCustomName("\u00a7c\u2764 \u00a7aSkyPvP \u00a7c\u2764");
                            TopArmorStandTask.this.armorStands.add(hologram2);
                            ArmorStand hologram1 = (ArmorStand) location.getWorld().spawn(hologramLocation1, ArmorStand.class);
                            ((CraftArmorStand) hologram1).setVisible(false);
                            ((CraftArmorStand) hologram1).setGravity(false);
                            hologram1.setCustomNameVisible(true);
                            hologram1.setCustomName("\u00a78\u27a4 \u00a7ePlatz\u00a78: \u00a76" + displayRank);
                            TopArmorStandTask.this.armorStands.add(hologram1);
                            ArmorStand hologram = (ArmorStand) location.getWorld().spawn(hologramLocation, ArmorStand.class);
                            ((CraftArmorStand) hologram).setVisible(false);
                            ((CraftArmorStand) hologram).setGravity(false);
                            hologram.setCustomNameVisible(true);
                            TopArmorStandTask.this.armorStands.add(hologram);
                            if (map.get(player) != null) {
                                hologram.setCustomName("\u00a78\u27a4 \u00a7eKills\u00a78: \u00a76" + decimalFormat.format(map.get(player)).replace(",", "."));
                            } else {
                                hologram.setCustomName("\u00a78\u27a4 \u00a7eKills\u00a78: \u00a760");
                            }
                            String displayName = gameProfile != null ? "\u00a7a" + gameProfile.getName() : "\u00a7a" + player;
                            NPCEntry npcEntry = NPCPlugin.getPlugin().getPlayerAPI().spawn(null, skinValue, skinSignature, displayName, location, new Random().nextInt(8888), 40, 40);
                            npcEntry.setHeldItem(new ItemStackBuilder(Material.DIAMOND_SWORD).setEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
                            TopArmorStandTask.this.npcs.add(npcEntry.getEntityId());
                            ++displayRank;
                        }
                    });
                });
            }
        }.runTaskTimer((Plugin) TwiceSystem.getInstance(), 0L, 5680L);
    }

    public final void removeArmorStands() {
        this.npcs.forEach(entityId -> NPCPlugin.getPlugin().getPlayerAPI().despawn(entityId.intValue()));
        this.armorStands.forEach(armorStand -> armorStand.remove());
        this.armorStands.clear();
        this.npcs.clear();
    }

}


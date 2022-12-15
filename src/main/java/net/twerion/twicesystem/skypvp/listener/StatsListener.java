/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.StructureModifier
 *  com.comphenix.protocol.wrappers.EnumWrappers
 *  com.comphenix.protocol.wrappers.EnumWrappers$ClientCommand
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package net.twerion.twicesystem.skypvp.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.twerion.twicesystem.skypvp.TwiceSystem;
import net.twerion.twicesystem.skypvp.impl.Perk;
import net.twerion.twicesystem.skypvp.manager.PerkInventoryManager;
import net.twerion.twicesystem.skypvp.utils.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class StatsListener
        implements Listener {
    public static HashMap<Player, Integer> killStreak = new HashMap();
    private final TwiceSystem twiceSystem;

    public StatsListener(TwiceSystem twiceSystem) {
        this.twiceSystem = twiceSystem;
        this.twiceSystem.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) twiceSystem);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        PerkInventoryManager.queryAndSavePerkStatesLocal(e.getPlayer());
        Player player = e.getPlayer();
        this.twiceSystem.getMysql().containsPlayer(e.getPlayer().getName(), this.twiceSystem.getMysql().table_stats, t -> {
            if (!t.booleanValue()) {
                this.twiceSystem.getMysql().createPlayer(e.getPlayer().getName());
            }
        });
        if (!player.hasMetadata("scoreboardcolor")) {
            this.twiceSystem.getMysql().getColor(player.getName(), t -> player.setMetadata("scoreboardcolor", (MetadataValue) new FixedMetadataValue((Plugin) this.twiceSystem, t)));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) TwiceSystem.getInstance(), () -> this.twiceSystem.getMysql().getFriedeListOfPlayer(e.getPlayer().getName(), friede_list -> Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> {
            friede_list.stream().filter(other -> !this.twiceSystem.getFriedeManager().isPeace(e.getPlayer().getName(), (String) other)).forEachOrdered(other -> this.twiceSystem.getFriedeManager().getFRIEDE_LIST().add(new String[]{e.getPlayer().getName(), other}));
            if (!friede_list.isEmpty()) {
                e.getPlayer().performCommand("friede list");
            }
        })), 20L);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player death = e.getEntity();
        Player killer = e.getEntity().getKiller();
        if (killer == null) {
            killer = TwiceSystem.getKillerWithLastDamage(death);
        }
        if (this.twiceSystem.flyMap.containsKey((Object) death)) {
            this.twiceSystem.flyMap.put(death, false);
        }
        if (killer != null && !killer.equals((Object) death)) {
            this.twiceSystem.getMysql().increaseKills(killer);
            if (!killStreak.containsKey((Object) killer)) {
                killStreak.put(killer, 0);
            }
            if (this.twiceSystem.getDailyReward().isEvent()) {
                killer.giveExpLevels(3);
                ActionBarAPI.sendActionBar(killer, "\u00a78\u27a4 \u00a74Trippel XP Event");
            }
            TwiceSystem.addItemToInventoryAndDropIfFull(killer, new ItemStack(Material.EMERALD, 1));
            int streak = killStreak.get((Object) killer);
            killStreak.put(killer, ++streak);
            if (streak == 3) {
                Bukkit.broadcastMessage((String) ("\u00a7c" + killer.getName() + " hat eine \u00a743-er Killstreak \u00a7cerreicht"));
                killer.sendMessage("\u00a7c + 2 Emeralds");
                TwiceSystem.addItemToInventoryAndDropIfFull(killer, new ItemStack(Material.EMERALD, 2));
            }
            if (streak == 10) {
                Bukkit.broadcastMessage((String) ("\u00a7c" + killer.getName() + " hat eine \u00a7410-er Killstreak \u00a7cerreicht"));
                killer.sendMessage("\u00a7c + 8 Emeralds");
                TwiceSystem.addItemToInventoryAndDropIfFull(killer, new ItemStack(Material.EMERALD, 8));
            }
            if (streak == 20) {
                Bukkit.broadcastMessage((String) ("\u00a7c" + killer.getName() + " hat eine \u00a7420-er Killstreak \u00a7cerreicht"));
                killer.sendMessage("\u00a7c + 17 Emeralds");
                TwiceSystem.addItemToInventoryAndDropIfFull(killer, new ItemStack(Material.EMERALD, 17));
            }
            if (streak == 50) {
                Bukkit.broadcastMessage((String) ("\u00a7c" + killer.getName() + " hat eine \u00a7450-er Killstreak \u00a7cerreicht"));
                killer.sendMessage("\u00a7c + 30 Emeralds");
                TwiceSystem.addItemToInventoryAndDropIfFull(killer, new ItemStack(Material.EMERALD, 30));
            }
            if (streak == 100) {
                Bukkit.broadcastMessage((String) ("\u00a7c" + killer.getName() + " hat eine \u00a74100-er Killstreak \u00a7cerreicht"));
                killer.sendMessage("\u00a7c + 40 Emeralds");
                TwiceSystem.addItemToInventoryAndDropIfFull(killer, new ItemStack(Material.EMERALD, 40));
            }
            int streak_async = streak;
            Player killer_async = killer;
            this.twiceSystem.getMysql().getKillstreak(killer.getName(), killstreak -> {
                if (killstreak < streak_async) {
                    Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> this.twiceSystem.getMysql().updateKillstreak(killer_async, streak_async));
                }
            }, false);
            if (Perk.DROPPER.canBeUsed(killer)) {
                ItemStack[] isArray = new ItemStack[e.getDrops().size()];
                for (int i = 0; i < e.getDrops().size(); ++i) {
                    isArray[i] = (ItemStack) e.getDrops().get(i);
                }
                HashMap<Integer, ItemStack> overflow = killer.getInventory().addItem(isArray);
                overflow.values().forEach(oIs -> death.getWorld().dropItem(death.getLocation(), oIs));
                e.getDrops().clear();
            }
        }
        this.twiceSystem.getMysql().increaseDeaths(death);
        if (killStreak.containsKey((Object) death)) {
            killStreak.remove((Object) death);
        }
        e.setKeepLevel(Perk.KEEP_XP.canBeUsed(death));
        if (Perk.KEEP_XP.canBeUsed(death) && !this.twiceSystem.getDailyReward().isEvent()) {
            e.setDroppedExp(0);
        }
        e.setDeathMessage(null);
        Bukkit.getScheduler().scheduleSyncDelayedTask(TwiceSystem.getInstance(), () -> {
            if (death.isOnline() && death.isDead()) {
                PacketContainer packet = new PacketContainer(PacketType.Play.Client.CLIENT_COMMAND);
                packet.getClientCommands().write(0, EnumWrappers.ClientCommand.PERFORM_RESPAWN);
                try {
                    ProtocolLibrary.getProtocolManager().recieveClientPacket(death, packet);
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }, 10L);
        e.getEntity().setLastDamageCause(null);
    }

    @EventHandler
    public final void onPlayerInteract(PlayerInteractEvent event) {
        Block block;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && ((block = event.getClickedBlock()).getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
            Sign sign = (Sign) block.getState();
            TwiceSystem.getInstance().getDailyReward().addSignClicks();
            if (sign.getLine(2).equalsIgnoreCase("\u00a71[Klick mich]")) {
                sign.setLine(3, event.getPlayer().getName());
                sign.setLine(1, new DecimalFormat().format(TwiceSystem.getInstance().getDailyReward().getSignClicks()).replace(",", "."));
                sign.update();
            }
        }
    }

    @EventHandler
    public final void onPlayerMove(PlayerMoveEvent event) {
        try {
            if (event.getPlayer().getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType() == Material.SPONGE) {
                Player player = event.getPlayer();
                double high = 0.35;
                double multiply = 1.25;
                player.setVelocity(player.getLocation().getDirection().multiply(2.25).setY(1.35));
            }
        } catch (Exception player) {
            // empty catch block
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) TwiceSystem.getInstance(), () -> {
            if (Perk.RUNNER.canBeUsed(e.getPlayer()) && !e.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            }
            if (Perk.NIGHT_VISION.canBeUsed(e.getPlayer()) && !e.getPlayer().hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
            }
        }, 20L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getLocation().getY() < -50.0 && e.getPlayer().getGameMode() != GameMode.CREATIVE && !e.getPlayer().isDead()) {
            e.getPlayer().setHealth(0.0);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.twiceSystem.quit(e.getPlayer());
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.twiceSystem.quit(e.getPlayer());
        e.setLeaveMessage(null);
    }
}


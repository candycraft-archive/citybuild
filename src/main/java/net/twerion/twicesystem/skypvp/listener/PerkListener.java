/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerExpChangeEvent
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.event.player.PlayerToggleFlightEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.projectiles.ProjectileSource
 *  org.bukkit.util.Vector
 */
package net.twerion.twicesystem.skypvp.listener;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import net.twerion.twicesystem.skypvp.impl.Perk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class PerkListener
        implements Listener {
    public static HashMap<UUID, Long> lastPotionClear = new HashMap();
    private final TwiceSystem twiceSystem;

    public PerkListener(TwiceSystem twiceSystem) {
        this.twiceSystem = twiceSystem;
        this.twiceSystem.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) twiceSystem);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            Player damager = null;
            if (e.getDamager() instanceof Player) {
                damager = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player) {
                damager = (Player) ((Arrow) e.getDamager()).getShooter();
            }
            if (damager != null && !damager.equals((Object) victim)) {
                this.twiceSystem.lastHits.put(victim, damager);
            }
            if (e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player && Perk.ARROW_POTION.canBeUsed((Player) ((Arrow) e.getDamager()).getShooter())) {
                ((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            e.getCause();
            if (EntityDamageEvent.DamageCause.FALL == EntityDamageEvent.DamageCause.FALL && Perk.ANTI_FALL_DAMAGE.canBeUsed(p)) {
                e.setCancelled(true);
            }
            if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                if (Perk.NO_FIRE_DAMAGE.canBeUsed(p)) {
                    e.setCancelled(true);
                }
            } else if (e.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                if (Perk.NO_WATER_DAMAGE.canBeUsed(p)) {
                    e.setCancelled(true);
                }
            } else if (e.getCause() == EntityDamageEvent.DamageCause.POISON && Perk.ANTI_POISON.canBeUsed(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (Perk.NO_HUNGER.canBeUsed((Player) e.getEntity())) {
            e.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onXP(PlayerExpChangeEvent e) {
        if (Perk.DOUBLE_XP.canBeUsed(e.getPlayer()) && e.getAmount() > 0) {
            e.setAmount(e.getAmount() * 2);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        boolean flying = false;
        if (this.twiceSystem.flyMap.containsKey((Object) e.getPlayer())) {
            flying = this.twiceSystem.flyMap.get((Object) e.getPlayer());
        }
        if (Perk.DOUBLE_JUMP.canBeUsed(e.getPlayer()) && !flying) {
            if (e.getPlayer().isOnGround()) {
                e.getPlayer().setAllowFlight(true);
            } else if (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.AIR && e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getFallDistance() > 3.0f) {
                e.getPlayer().setAllowFlight(false);
            }
            if (e.getPlayer().isFlying() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.getPlayer().setFlying(false);
            }
        } else if (!e.getPlayer().hasPermission("essentials.fly") && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.getPlayer().setAllowFlight(false);
        }
        if (Perk.RUNNER.canBeUsed(e.getPlayer()) && !e.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }
        if (Perk.FAST_BREAK.canBeUsed(e.getPlayer()) && !e.getPlayer().hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2));
        }
        if (Perk.NIGHT_VISION.canBeUsed(e.getPlayer()) && !e.getPlayer().hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        boolean flying = false;
        if (this.twiceSystem.flyMap.containsKey((Object) e.getPlayer())) {
            flying = this.twiceSystem.flyMap.get((Object) e.getPlayer());
        }
        if (flying) {
            return;
        }
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
        if (Perk.DOUBLE_JUMP.canBeUsed(e.getPlayer()) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.getPlayer().setAllowFlight(false);
            e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().normalize().setY(0.9f).multiply(0.6f));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof Player) {
            Player toRemove = (Player) e.getRightClicked();
            Player p = e.getPlayer();
            if (Perk.POTION_CLEAR.canBeUsed(p) && p.isSneaking()) {
                if (toRemove.getActivePotionEffects().isEmpty()) {
                    p.sendMessage("\u00a7cDieser Spieler hat gerade keine Effekte");
                } else if (!lastPotionClear.containsKey(p.getUniqueId()) || System.currentTimeMillis() - lastPotionClear.get(p.getUniqueId()) > 900000L) {
                    ArrayList<PotionEffect> pes = new ArrayList<PotionEffect>();
                    pes.addAll(toRemove.getActivePotionEffects());
                    pes.forEach(pe -> toRemove.removePotionEffect(pe.getType()));
                    p.sendMessage("\u00a73Du hast Potion-Clear verwendet");
                    lastPotionClear.put(p.getUniqueId(), System.currentTimeMillis());
                } else {
                    p.sendMessage("\u00a7cBitte warte noch bevor du Potion-Clear erneut verwendest");
                }
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (Perk.ITEM_NAME.canBeUsed(e.getPlayer()) && e.getItem().getItemStack().getType() != Material.EMERALD && e.getItem().getItemStack().getType() != Material.TRIPWIRE_HOOK) {
            e.setCancelled(true);
            ItemStack is = e.getItem().getItemStack().clone();
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName("\u00a74\u00a7km\u00a7r \u00a7c" + e.getPlayer().getName() + " \u00a74\u00a7km");
            is.setItemMeta(meta);
            HashMap overflow = e.getPlayer().getInventory().addItem(new ItemStack[]{is});
            if (overflow.isEmpty()) {
                e.getItem().remove();
            } else {
                e.getItem().setItemStack((ItemStack) overflow.values().toArray()[0]);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("plots")) {
            for (String cmd : Arrays.asList("/tnt", "/essentials:tnt", "/antioch", "/essentials:antioch")) {
                if (!message.toLowerCase().startsWith(cmd)) continue;
                e.getPlayer().sendMessage("\u00a7cDieser Befehl ist in dieser Welt deaktiviert.");
                e.setCancelled(true);
                return;
            }
        }
        if (message.equalsIgnoreCase("/fly")) {
            if (e.isCancelled()) {
                return;
            }
            if (e.getPlayer().getLocation().getY() <= 0.0) {
                return;
            }
            if (e.getPlayer().hasPermission("essentials.fly")) {
                e.setCancelled(true);
                boolean flying = false;
                if (this.twiceSystem.flyMap.containsKey((Object) e.getPlayer())) {
                    flying = this.twiceSystem.flyMap.get((Object) e.getPlayer());
                }
                if (flying) {
                    this.twiceSystem.flyMap.put(e.getPlayer(), false);
                    e.getPlayer().setFlying(false);
                    e.getPlayer().setAllowFlight(false);
                    e.getPlayer().sendMessage("\u00a7bFlugmodus \u00a7ldeaktiviert");
                } else {
                    this.twiceSystem.flyMap.put(e.getPlayer(), true);
                    e.getPlayer().setAllowFlight(true);
                    e.getPlayer().sendMessage("\u00a7bFlugmodus \u00a7laktiviert");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getBlock().getType() == Material.MOB_SPAWNER && Perk.MOB_SPAWNER.canBeUsed(e.getPlayer())) {
            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.MOB_SPAWNER, 1, (short) e.getBlock().getData()));
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getInventory().getTitle() != null && e.getInventory().getTitle().startsWith("Inventar: ")) {
            e.setCancelled(true);
        }
    }
}


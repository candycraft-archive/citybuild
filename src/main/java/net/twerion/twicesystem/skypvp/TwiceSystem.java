/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.FileConfigurationOptions
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.ServicesManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.projectiles.ProjectileSource
 *  org.bukkit.scheduler.BukkitTask
 */
package net.twerion.twicesystem.skypvp;

import net.milkbowl.vault.economy.Economy;
import net.twerion.twicesystem.skypvp.commands.HandelnCommand;
import net.twerion.twicesystem.skypvp.commands.SimpleCommand;
import net.twerion.twicesystem.skypvp.commands.SkidCommand;
import net.twerion.twicesystem.skypvp.commands.StatsCommand;
import net.twerion.twicesystem.skypvp.impl.Perk;
import net.twerion.twicesystem.skypvp.listener.PerkListener;
import net.twerion.twicesystem.skypvp.listener.StatsListener;
import net.twerion.twicesystem.skypvp.manager.FriedeManager;
import net.twerion.twicesystem.skypvp.manager.PerkInventoryManager;
import net.twerion.twicesystem.skypvp.skid.daily.DailyReward;
import net.twerion.twicesystem.skypvp.sql.Mysql;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TwiceSystem
        extends JavaPlugin {
    private static TwiceSystem instance;
    private static boolean globalMute;

    static {
        globalMute = false;
    }

    public Mysql mysql;
    public HashMap<Player, Boolean> flyMap;
    public Map<Player, Player> lastHits;
    private HashMap<Player, Integer> ranking;
    private HashMap<Perk, String> perkPermissions;
    private PerkInventoryManager perkInventoryManager;
    private FriedeManager friedeManager;
    private PerkListener perkListener;
    private StatsListener statsListener;
    private HandelnCommand handelnCommand;
    private SimpleCommand simpleCommand;
    private StatsCommand statsCommand;
    private DailyReward dailyReward;
    private Economy economy = null;

    public static TwiceSystem getInstance() {
        return instance;
    }

    public static void addItemToInventoryAndDropIfFull(Player invPlayer, ItemStack item) {
        HashMap map = invPlayer.getInventory().addItem(new ItemStack[]{item});
        map.values().forEach(value -> invPlayer.getWorld().dropItem(invPlayer.getLocation(), value));
    }

    public static void addItemToInventoryAndDropGlasses(Player invPlayer, ItemStack item) {
        HashMap map = invPlayer.getInventory().addItem(new ItemStack[]{item});
        map.values().stream().map(value -> value.clone()).map(itemStack -> {
            itemStack.setType(Material.GLASS_BOTTLE);
            return itemStack;
        }).forEachOrdered(itemStack -> invPlayer.getWorld().dropItem(invPlayer.getLocation(), itemStack));
    }

    public static String getPerkPermission(Perk perk) {
        return TwiceSystem.getInstance().getPerkPermissions().getOrDefault((Object) perk, "perks." + perk.getPerkName() + ".perm");
    }

    public static Set<String> getPerks() {
        return TwiceSystem.getInstance().getConfig().getConfigurationSection("perks").getKeys(false);
    }

    public static String getAdminPermission() {
        return TwiceSystem.getInstance().getConfig().getString("admin-permission");
    }

    public static String getVIPPermission() {
        return TwiceSystem.getInstance().getConfig().getString("vip-permission");
    }

    public static String getAnvilPermission() {
        return TwiceSystem.getInstance().getConfig().getString("anvil-permission");
    }

    public static String getFillPermission() {
        return TwiceSystem.getInstance().getConfig().getString("fill-permission");
    }

    public static String getGoldswitchPermission() {
        return TwiceSystem.getInstance().getConfig().getString("goldswitch-permission");
    }

    public static String getZaubertischPermission() {
        return TwiceSystem.getInstance().getConfig().getString("zaubertisch-permission");
    }

    public static String getPotionsPermission() {
        return TwiceSystem.getInstance().getConfig().getString("potions-permission");
    }

    public static String getBodyseePermission() {
        return TwiceSystem.getInstance().getConfig().getString("bodysee-permission");
    }

    public static boolean isRespawnItems() {
        return TwiceSystem.getInstance().getConfig().getBoolean("respawn-items");
    }

    public static boolean isItemNamePerk() {
        return TwiceSystem.getInstance().getConfig().getBoolean("item-name-perk");
    }

    public static Player getKillerWithLastDamage(Player death) {
        EntityDamageEvent event = death.getLastDamageCause();
        if (event != null && event instanceof EntityDamageByEntityEvent) {
            Projectile projectile;
            EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
            if (event2.getDamager() instanceof Player) {
                return (Player) event2.getDamager();
            }
            if (event2.getDamager() instanceof Projectile && (projectile = (Projectile) event2.getDamager()).getShooter() != null && projectile.getShooter() instanceof Player) {
                Player player = (Player) projectile.getShooter();
                if (death == player) {
                    return TwiceSystem.getInstance().getLastHits().get((Object) death);
                }
                return player;
            }
        }
        return null;
    }

    public static boolean isGlobalMute() {
        return globalMute;
    }

    public static void setGlobalMute(boolean globalMute) {
        TwiceSystem.globalMute = globalMute;
    }

    public void onEnable() {
        System.out.println("TwiceSystem - Copyright by RoboTricker");
        instance = this;
        this.perkInventoryManager = new PerkInventoryManager(this);
        this.friedeManager = new FriedeManager(this);
        this.perkListener = new PerkListener(this);
        this.statsListener = new StatsListener(this);
        this.handelnCommand = new HandelnCommand(this);
        this.simpleCommand = new SimpleCommand(this);
        this.statsCommand = new StatsCommand(this);
        this.flyMap = new HashMap();
        this.lastHits = new HashMap<Player, Player>();
        this.ranking = new HashMap();
        this.perkPermissions = new HashMap();
        this.setupEconomy();
        this.reloadConfig();
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        for (Perk value : Perk.values()) {
            this.perkPermissions.put(value, TwiceSystem.getInstance().getConfig().getString("perks." + value.getPerkName().toLowerCase() + ".perm"));
        }
        for (Perk value : Perk.values()) {
            if (this.getConfig().contains("perks." + value.getPerkName() + ".perm")) continue;
            this.getConfig().set("perks." + value.getPerkName() + ".perm", (Object) ("perks." + value.getPerkName()));
        }
        this.saveConfig();
        String host = this.getConfig().getString("host");
        String user = this.getConfig().getString("user");
        String pass = this.getConfig().getString("password");
        String db = this.getConfig().getString("database");
        this.mysql = new Mysql(host, user, pass, db);
        if (!this.mysql.connect()) {
            System.err.println("--------------------------");
            System.err.println("Verbindung zu Mysql fehlgeschlagen!");
            System.err.println("--------------------------");
            Bukkit.getPluginManager().disablePlugin((Plugin) this);
            return;
        }
        System.out.println("--------------------------");
        System.out.println("Verbindung zu Mysql hergestellt");
        System.out.println("--------------------------");
        super.getCommand("skid").setExecutor((CommandExecutor) new SkidCommand());
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) this, () -> {
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                if (viewer.getOpenInventory() == null || viewer.getOpenInventory().getTitle() == null || !viewer.getOpenInventory().getTitle().startsWith("Inventar: "))
                    continue;
                String body = viewer.getOpenInventory().getTitle().replace("Inventar: ", "");
                if (Bukkit.getPlayer((String) body) != null && Bukkit.getPlayer((String) body).isOnline()) {
                    SimpleCommand.updateBodyseeInv(viewer, Bukkit.getPlayer((String) body));
                    continue;
                }
                viewer.closeInventory();
            }
        }, 2L, 2L);
        this.dailyReward = new DailyReward();
        this.dailyReward.setSignClicks(this.dailyReward.getYamlConfiguration().getInt("Adjuster.SignClicks"));
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = (Economy) rsp.getProvider();
        return this.economy != null;
    }

    public void onDisable() {
        this.mysql.close();
        this.dailyReward.saveSignClicks();
    }

    public void quit(Player player) {
        Player killer;
        Inventory finalInv = null;
        for (Inventory inv : this.handelnCommand.handel.keySet()) {
            if (!this.handelnCommand.handel.get((Object) inv)[0].equals((Object) player) && !this.handelnCommand.handel.get((Object) inv)[1].equals((Object) player))
                continue;
            finalInv = inv;
        }
        if (finalInv != null) {
            Player pLeft = this.handelnCommand.handel.get((Object) finalInv)[0];
            Player pRight = this.handelnCommand.handel.get((Object) finalInv)[1];
            this.handelnCommand.handel.remove((Object) finalInv);
            if (this.handelnCommand.countdown.containsKey((Object) finalInv)) {
                this.handelnCommand.countdown.remove((Object) finalInv);
            }
            if (pLeft.equals((Object) player)) {
                HandelnCommand.giveItems(finalInv, pRight, false);
                pRight.sendMessage("\u00a7cDer Handel wurde abgebrochen");
                pRight.closeInventory();
            } else {
                HandelnCommand.giveItems(finalInv, pLeft, true);
                pLeft.sendMessage("\u00a7cDer Handel wurde abgebrochen");
                pLeft.closeInventory();
            }
        }
        if ((killer = TwiceSystem.getKillerWithLastDamage(player)) != null && !killer.equals((Object) player)) {
            this.mysql.increaseKills(killer);
            if (!StatsListener.killStreak.containsKey((Object) killer)) {
                StatsListener.killStreak.put(killer, 0);
            }
            TwiceSystem.addItemToInventoryAndDropIfFull(killer, new ItemStack(Material.EMERALD, 1));
            int streak = StatsListener.killStreak.get((Object) killer);
            StatsListener.killStreak.put(killer, ++streak);
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
                Bukkit.broadcastMessage((String) ("\u00a7c" + killer.getName() + " hat eine \u00a7450-er Killstreak \u00a7cerreicht"));
                killer.sendMessage("\u00a7c + 40 Emeralds");
                TwiceSystem.addItemToInventoryAndDropIfFull(killer, new ItemStack(Material.EMERALD, 40));
            }
            int streak_async = streak;
            this.mysql.getKillstreak(killer.getName(), killstreak -> {
                if (killstreak < streak_async) {
                    Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> TwiceSystem.getInstance().getMysql().updateKillstreak(killer, streak_async));
                }
            }, false);
        }
        if (this.friedeManager.getFRIEDE_ANFRAGEN().containsKey((Object) player)) {
            this.friedeManager.getFRIEDE_ANFRAGEN().remove((Object) player);
        }
        if (this.friedeManager.getFRIEDE_ANFRAGEN().containsValue((Object) player)) {
            ArrayList<Player> toRemove = new ArrayList<Player>();
            this.friedeManager.getFRIEDE_ANFRAGEN().keySet().stream().filter(to -> this.friedeManager.getFRIEDE_ANFRAGEN().get(to).equals((Object) player)).forEachOrdered(to -> toRemove.add((Player) to));
            toRemove.forEach(toRem -> this.friedeManager.getFRIEDE_ANFRAGEN().remove(toRem));
        }
    }

    public Mysql getMysql() {
        return this.mysql;
    }

    public HashMap<Player, Boolean> getFlyMap() {
        return this.flyMap;
    }

    public Map<Player, Player> getLastHits() {
        return this.lastHits;
    }

    public HashMap<Player, Integer> getRanking() {
        return this.ranking;
    }

    public HashMap<Perk, String> getPerkPermissions() {
        return this.perkPermissions;
    }

    public PerkInventoryManager getPerkInventoryManager() {
        return this.perkInventoryManager;
    }

    public FriedeManager getFriedeManager() {
        return this.friedeManager;
    }

    public PerkListener getPerkListener() {
        return this.perkListener;
    }

    public DailyReward getDailyReward() {
        return this.dailyReward;
    }

    public Economy getEconomy() {
        return this.economy;
    }
}


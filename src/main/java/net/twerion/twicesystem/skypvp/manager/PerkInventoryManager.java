/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.material.MaterialData
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitTask
 */
package net.twerion.twicesystem.skypvp.manager;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import net.twerion.twicesystem.skypvp.impl.ItemStackBuilder;
import net.twerion.twicesystem.skypvp.impl.Perk;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PerkInventoryManager
        implements Listener {
    private static final int MAX_INV_ROWS = 5;
    private static final HashMap<Player, Integer> currentSites = new HashMap();
    private static final HashMap<Player, HashMap<Perk, Long>> lastPerkChanges = new HashMap();
    private static final HashMap<Player, HashMap<Perk, Boolean>> perkActives = new HashMap();
    private final TwiceSystem twiceSystem;

    public PerkInventoryManager(TwiceSystem twiceSystem) {
        this.twiceSystem = twiceSystem;
        this.twiceSystem.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) twiceSystem);
    }

    public static void openPerksInventory(Player p) {
        p.openInventory(Bukkit.createInventory(null, (int) 54, (String) "\u00a7rPerks"));
        PerkInventoryManager.updatePerksInv(p, 0, null);
        PerkInventoryManager.queryAllPerksAndUpdateInv(p);
    }

    public static void updatePerksInv(Player p, int site, List<Boolean> mysqlResult) {
        if (p.getOpenInventory() != null && p.getOpenInventory().getTopInventory() != null && p.getOpenInventory().getTopInventory().getTitle() != null && p.getOpenInventory().getTopInventory().getTitle().equals("\u00a7rPerks")) {
            int i;
            Inventory inv = p.getOpenInventory().getTopInventory();
            int maxPages = (int) Math.ceil((float) Perk.values().length / 10.0f);
            for (i = 0; i < inv.getSize(); ++i) {
                inv.setItem(i, null);
            }
            i = 0;
            for (Perk perk : Perk.values()) {
                int row = i - site * 10;
                if (row >= 0 && row <= 9) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.addAll(perk.lore);
                    boolean glow = false;
                    if (mysqlResult != null) {
                        boolean active = mysqlResult.get(i);
                        lore.add("");
                        if (perk.isDeactivated()) {
                            lore.add("\u00a7cNicht verf\u00fcgbar");
                        } else if (perk.hasPermission(p)) {
                            if (active) {
                                lore.add("\u00a7aAktiviert");
                                glow = true;
                            } else {
                                lore.add("\u00a7cDeaktiviert");
                            }
                        } else {
                            lore.add("\u00a76Perk kaufen: \u00a7eshop.candysucht.net");
                        }
                    }
                    ItemStack item = PerkInventoryManager.createItemStack(perk.material, 1, perk.data, "\u00a7a" + perk.name(), lore);
                    if (glow) {
                        item = PerkInventoryManager.addGlow(item);
                    }
                    if (row < 5) {
                        inv.setItem(row * 9 + 1, item);
                        inv.setItem(row * 9 + 2, PerkInventoryManager.createItemStack(Material.INK_SACK, 1, (short) 8, "\u00a7cDeaktivieren"));
                        inv.setItem(row * 9 + 3, PerkInventoryManager.createItemStack(Material.INK_SACK, 1, (short) 10, "\u00a7aAktivieren"));
                    } else {
                        inv.setItem((row - 5) * 9 + 5, item);
                        inv.setItem((row - 5) * 9 + 6, PerkInventoryManager.createItemStack(Material.INK_SACK, 1, (short) 8, "\u00a7cDeaktivieren"));
                        inv.setItem((row - 5) * 9 + 7, PerkInventoryManager.createItemStack(Material.INK_SACK, 1, (short) 10, "\u00a7aAktivieren"));
                    }
                }
                ++i;
            }
            if (site > 0) {
                inv.setItem(45, PerkInventoryManager.createItemStack(Material.FEATHER, 1, (short) 0, "\u00a7aZur\u00fcck"));
            }
            if (site < maxPages - 1) {
                inv.setItem(53, PerkInventoryManager.createItemStack(Material.FEATHER, 1, (short) 0, "\u00a7aWeiter"));
            }
            inv.setItem(49, PerkInventoryManager.createItemStack(Material.PAPER, 1, (short) 0, "\u00a7aSeite \u00a7e" + (site + 1)));
            currentSites.put(p, site);
            p.updateInventory();
        }
    }

    private static int getCurrentSite(Player p) {
        if (currentSites.containsKey((Object) p)) {
            return currentSites.get((Object) p);
        }
        return 0;
    }

    public static void queryAllPerksAndUpdateInv(Player p) {
        ArrayList boolList = new ArrayList();
        TwiceSystem.getInstance().getMysql().isPerkActive(p, Perk.values()[0], b -> {
            boolList.add(b);
            PerkInventoryManager.queryPerk(p, boolList, 1);
            Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> {
                for (int i = 0; i < boolList.size(); ++i) {
                    boolean active = (Boolean) boolList.get(i);
                    Perk perk = Perk.values()[i];
                    PerkInventoryManager.savePerkStateLocal(p, perk, active);
                }
                PerkInventoryManager.updatePerksInv(p, PerkInventoryManager.getCurrentSite(p), boolList);
            });
        }, false);
    }

    private static void queryPerk(Player p, List<Boolean> boolList, int i) {
        if (Perk.values().length > i) {
            TwiceSystem.getInstance().getMysql().isPerkActive(p, Perk.values()[i], b -> {
                boolList.add((Boolean) b);
                PerkInventoryManager.queryPerk(p, boolList, i + 1);
            }, true);
        }
    }

    private static ItemStack createItemStack(Material material, int amount, short data, String displayName, List<String> lore) {
        ItemStack is = new ItemStack(material, amount, data);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) {
            meta.setLore(lore);
        }
        is.setItemMeta(meta);
        return is;
    }

    private static ItemStack createItemStack(Material material, int amount, short data, String displayName) {
        return PerkInventoryManager.createItemStack(material, amount, data, displayName, null);
    }

    private static ItemStack addGlow(ItemStack item) {
        return new ItemStackBuilder(item).setEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setItemFlag(ItemFlag.HIDE_ENCHANTS).build();
    }

    private static void savePerkStateLocal(Player p, Perk perk, boolean active) {
        HashMap perkMap = perkActives.containsKey((Object) p) ? perkActives.get((Object) p) : new HashMap();
        perkMap.put((Object) perk, active);
        perkActives.put(p, perkMap);
        if (perk == Perk.RUNNER && !active) {
            if (p.hasPotionEffect(PotionEffectType.SPEED)) {
                p.removePotionEffect(PotionEffectType.SPEED);
            }
        } else if (perk == Perk.NIGHT_VISION && !active) {
            if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        } else if (!(perk != Perk.DOUBLE_JUMP || active || TwiceSystem.getInstance().flyMap.containsKey((Object) p) && TwiceSystem.getInstance().flyMap.get((Object) p).booleanValue())) {
            p.setFlying(false);
            p.setAllowFlight(false);
        }
    }

    public static void queryAndSavePerkStatesLocal(Player p) {
        ArrayList boolList = new ArrayList();
        TwiceSystem.getInstance().getMysql().isPerkActive(p, Perk.values()[0], b -> {
            boolList.add(b);
            PerkInventoryManager.queryPerk(p, boolList, 1);
            Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> {
                for (int i = 0; i < boolList.size(); ++i) {
                    boolean active = (Boolean) boolList.get(i);
                    Perk perk = Perk.values()[i];
                    PerkInventoryManager.savePerkStateLocal(p, perk, active);
                }
            });
        }, false);
    }

    public static boolean getPerkStateLocal(Player p, Perk perk) {
        if (perkActives.containsKey((Object) p)) {
            if (perkActives.get((Object) p).containsKey((Object) perk)) {
                return perkActives.get((Object) p).get((Object) perk);
            }
            return true;
        }
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory() != null && e.getInventory().getTitle() != null && e.getInventory().getTitle().equals("\u00a7rPerks")) {
            e.setCancelled(true);
            int site = PerkInventoryManager.getCurrentSite(p);
            int maxPages = (int) Math.ceil((float) Perk.values().length / 10.0f);
            if (e.getRawSlot() == 45) {
                if (site > 0) {
                    PerkInventoryManager.updatePerksInv(p, site - 1, null);
                    PerkInventoryManager.queryAllPerksAndUpdateInv(p);
                }
            } else if (e.getRawSlot() == 53) {
                if (site < maxPages - 1) {
                    PerkInventoryManager.updatePerksInv(p, site + 1, null);
                    PerkInventoryManager.queryAllPerksAndUpdateInv(p);
                }
            } else if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK && (e.getCurrentItem().getData().getData() == 8 || e.getCurrentItem().getData().getData() == 10)) {
                boolean setActive = e.getCurrentItem().getData().getData() == 10;
                ItemStack perkStack = e.getInventory().getItem(e.getRawSlot() - (setActive ? 2 : 1));
                if (perkStack != null) {
                    Perk perk = Perk.valueOf(perkStack.getItemMeta().getDisplayName().substring(2));
                    long lastPerkChange = 0L;
                    if (lastPerkChanges.containsKey((Object) p) && lastPerkChanges.get((Object) p).containsKey((Object) perk)) {
                        lastPerkChange = lastPerkChanges.get((Object) p).get((Object) perk);
                    }
                    if (System.currentTimeMillis() - lastPerkChange < 5000L) {
                        p.sendMessage("\u00a7cBitte warte kurz...");
                    } else if (perk.isDeactivated()) {
                        p.sendMessage("\u00a7cPerk nicht verf\u00fcgbar");
                    } else if (!perk.hasPermission(p)) {
                        p.sendMessage("\u00a76Perk kaufen: \u00a7eshop.candysucht.net");
                    } else {
                        TwiceSystem.getInstance().getMysql().updatePerk(p, perk, setActive);
                        if (lastPerkChanges.containsKey((Object) p)) {
                            lastPerkChanges.get((Object) p).put(perk, System.currentTimeMillis());
                        } else {
                            HashMap<Perk, Long> map = new HashMap<Perk, Long>();
                            map.put(perk, System.currentTimeMillis());
                            lastPerkChanges.put(p, map);
                        }
                    }
                }
            }
        }
    }
}


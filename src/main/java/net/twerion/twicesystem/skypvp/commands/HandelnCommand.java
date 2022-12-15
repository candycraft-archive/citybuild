/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.material.MaterialData
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package net.twerion.twicesystem.skypvp.commands;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HandelnCommand
        implements CommandExecutor,
        Listener {
    public final HashMap<Player, Player> anfragen;
    public final int INV_ROWS = 5;
    public final HashMap<Inventory, Integer> countdown;
    public final HashMap<Inventory, Player[]> handel;
    public final String MIDDLE = " \u00a77- \u00a7r";
    public final ArrayList<Player> wartecount;
    public final TwiceSystem citybuild;

    public HandelnCommand(TwiceSystem citybuild) {
        this.citybuild = citybuild;
        this.citybuild.getCommand("handeln").setExecutor((CommandExecutor) this);
        this.citybuild.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) citybuild);
        this.anfragen = new HashMap();
        this.countdown = new HashMap();
        this.handel = new HashMap();
        this.wartecount = new ArrayList();
    }

    public static void giveItems(Inventory inv, Player p, boolean left) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int slot = 0; slot < 45; ++slot) {
            if (left) {
                if (!(slot < 4 || slot >= 9 && slot < 13 || slot >= 18 && slot < 22) && (slot < 27 || slot >= 31) || inv.getItem(slot) == null)
                    continue;
                list.add(slot);
                continue;
            }
            if (!(slot >= 5 && slot <= 8 || slot >= 14 && slot <= 17 || slot >= 23 && slot <= 26) && (slot < 32 || slot > 35) || inv.getItem(slot) == null)
                continue;
            list.add(slot);
        }
        Iterator localIterator1 = list.iterator();
        while (localIterator1.hasNext()) {
            int l = (Integer) localIterator1.next();
            HashMap map = p.getInventory().addItem(new ItemStack[]{inv.getItem(l)});
            Iterator localIterator2 = map.values().iterator();
            while (localIterator2.hasNext()) {
                ItemStack is = (ItemStack) localIterator2.next();
                p.getWorld().dropItem(p.getLocation(), is);
            }
            localIterator2.hasNext();
        }
    }

    public static int addItem(Inventory inv, ItemStack item, boolean left) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int slot = 0; slot < 45; ++slot) {
            if (left) {
                if (!(slot < 4 || slot >= 9 && slot < 13 || slot >= 18 && slot < 22) && (slot < 27 || slot >= 31))
                    continue;
                list.add(slot);
                continue;
            }
            if (!(slot >= 5 && slot <= 8 || slot >= 14 && slot <= 17 || slot >= 23 && slot <= 26) && (slot < 32 || slot > 35))
                continue;
            list.add(slot);
        }
        HashMap<Integer, ItemStack> before = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < 45; ++i) {
            if (list.contains(i)) continue;
            before.put(i, inv.getItem(i));
            inv.setItem(i, new ItemStack(Material.getMaterial((int) 166)));
        }
        HashMap map = inv.addItem(new ItemStack[]{item});
        for (int i = 0; i < 45; ++i) {
            if (list.contains(i)) continue;
            inv.setItem(i, (ItemStack) before.get(i));
        }
        return map.isEmpty() ? 0 : ((ItemStack) map.values().toArray()[0]).getAmount();
    }

    public static ItemStack getColoredWool(String name, short colorByte) {
        ItemStack wool = new ItemStack(Material.WOOL, 1, colorByte);
        ItemMeta meta = wool.getItemMeta();
        meta.setDisplayName(name);
        wool.setItemMeta(meta);
        return wool;
    }

    public static ItemStack getStatus(boolean active) {
        ItemStack wool = new ItemStack(Material.INK_SACK, 1, (short) (active ? 10 : 8));
        ItemMeta meta = wool.getItemMeta();
        meta.setDisplayName(active ? "\u00a7aBereit" : "\u00a77Warten...");
        wool.setItemMeta(meta);
        return wool;
    }

    public static boolean isCorrectKey(String pSchluessel) {
        boolean istKorrekt;
        istKorrekt = false;
        try {
            System.out.println("checking license with key: " + pSchluessel);
            String lizenzSystemLink = "http://frontfight.net/lizenz/pruefe.php?programm=twerionsystem&key=" + pSchluessel;
            URL lizenzSystemURL = new URL(lizenzSystemLink);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(lizenzSystemURL.openStream()));) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (!Boolean.parseBoolean(inputLine)) continue;
                    istKorrekt = true;
                }
            }
        } catch (IOException localException) {
            localException.printStackTrace();
        }
        return istKorrekt;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            Player p = (Player) cs;
            if (args.length == 1) {
                Player other = Bukkit.getPlayer((String) args[0]);
                if (other != null && other.isOnline()) {
                    if (this.anfragen.containsKey((Object) other) && this.anfragen.get((Object) other).equals((Object) p)) {
                        this.anfragen.remove((Object) other);
                        String title = other.getName() + " \u00a77- \u00a7r" + p.getName();
                        if (title.toCharArray().length > 32) {
                            title = title.substring(0, 32);
                        }
                        Inventory inv = Bukkit.createInventory(null, (int) 45, (String) title);
                        ItemStack fence = new ItemStack(Material.IRON_FENCE);
                        ItemStack status = HandelnCommand.getStatus(false);
                        ItemStack cancel = HandelnCommand.getColoredWool("\u00a74Abbrechen", (short) 14);
                        ItemStack confirm = HandelnCommand.getColoredWool("\u00a7aBest\u00e4tigen", (short) 5);
                        for (int i = 0; i < 5; ++i) {
                            inv.setItem(9 * i + 4, fence);
                        }
                        inv.setItem(36, confirm);
                        inv.setItem(37, cancel);
                        inv.setItem(39, status);
                        inv.setItem(44, confirm);
                        inv.setItem(43, cancel);
                        inv.setItem(41, status);
                        p.openInventory(inv);
                        other.openInventory(inv);
                        this.handel.put(inv, new Player[]{other, p});
                    } else if (!other.equals((Object) p)) {
                        this.anfragen.put(p, other);
                        p.sendMessage("\u00a78\u27a4 \u00a7aAnfrage an \u00a7e" + other.getName() + " \u00a7ageschickt!");
                        other.sendMessage("\u00a78\u27a4 \u00a7e" + p.getName() + " \u00a7am\u00f6chte mit dir handeln!");
                        other.sendMessage("\u00a78\u27a4 \u00a7c/handeln " + p.getName());
                    } else {
                        p.sendMessage("\u00a78\u27a4 \u00a7cDu kannst nicht mit dir selbst handeln");
                    }
                } else {
                    p.sendMessage("\u00a78\u27a4 \u00a7cDieser Spieler ist nicht online");
                }
            } else {
                p.sendMessage("\u00a78\u27a4 \u00a7c/handeln <Spieler>");
            }
        } else {
            cs.sendMessage("\u00a7cDu bist die Konsole");
        }
        return true;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getName() != null && this.handel.containsKey((Object) e.getInventory())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHandelDiablePickUp(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if (p.getOpenInventory().getTitle().contains(p.getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHandelClick(InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        if (e.getInventory().getName() != null && this.handel.containsKey((Object) e.getInventory()) && e.getInventory() != null) {
            Player pLeft = this.handel.get((Object) e.getInventory())[0];
            Player pRight = this.handel.get((Object) e.getInventory())[1];
            if (e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT && e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT && e.getClick() != ClickType.NUMBER_KEY) {
                e.setCancelled(true);
                return;
            }
            if (e.getInventory().getType() == InventoryType.CHEST) {
                ItemStack status;
                boolean activeClick = false;
                boolean cancelClick = false;
                if (pLeft.equals((Object) p)) {
                    if (e.getRawSlot() < 4 || e.getRawSlot() >= 9 && e.getRawSlot() < 13 || e.getRawSlot() >= 18 && e.getRawSlot() < 22 || e.getRawSlot() >= 27 && e.getRawSlot() < 31) {
                        if (this.countdown.containsKey((Object) e.getInventory())) {
                            e.setCancelled(true);
                        }
                    } else {
                        status = e.getInventory().getItem(39);
                        if (e.getRawSlot() == 36) {
                            status = HandelnCommand.getStatus(true);
                            activeClick = true;
                        } else if (e.getRawSlot() == 37) {
                            status = HandelnCommand.getStatus(false);
                            cancelClick = true;
                        }
                        e.getInventory().setItem(39, status);
                        e.setCancelled(true);
                    }
                }
                if (pRight.equals((Object) p)) {
                    if (e.getRawSlot() >= 5 && e.getRawSlot() <= 8 || e.getRawSlot() >= 14 && e.getRawSlot() <= 17 || e.getRawSlot() >= 23 && e.getRawSlot() <= 26 || e.getRawSlot() >= 32 && e.getRawSlot() <= 35) {
                        if (this.countdown.containsKey((Object) e.getInventory())) {
                            e.setCancelled(true);
                        }
                    } else {
                        status = e.getInventory().getItem(41);
                        if (e.getRawSlot() == 44) {
                            status = HandelnCommand.getStatus(true);
                            activeClick = true;
                        } else if (e.getRawSlot() == 43) {
                            status = HandelnCommand.getStatus(false);
                            cancelClick = true;
                        }
                        e.getInventory().setItem(41, status);
                        e.setCancelled(true);
                    }
                }
                if (activeClick) {
                    if (e.getInventory().getItem(41).getData().getData() == 10 && e.getInventory().getItem(39).getData().getData() == 10 && !this.wartecount.contains((Object) p)) {
                        this.wartecount.add(p);
                        this.startCountdown(e.getInventory());
                        new BukkitRunnable() {

                            public void run() {
                                HandelnCommand.this.wartecount.remove((Object) p);
                            }
                        }.runTaskLater((Plugin) TwiceSystem.getInstance(), 140L);
                    }
                } else if (cancelClick) {
                    this.cancelCountdown(e.getInventory());
                }
            } else if (e.getInventory().getType() == InventoryType.PLAYER) {
                e.setCancelled(true);
                if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR || e.getAction() == InventoryAction.PLACE_ALL) {
                    e.setCancelled(false);
                    return;
                }
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().getAmount() > 1) {
                        if (e.isShiftClick()) {
                            int leftAmount = HandelnCommand.addItem(e.getInventory(), e.getCurrentItem(), pLeft.equals((Object) p));
                            if (leftAmount == 0) {
                                e.setCurrentItem(null);
                            } else {
                                ItemStack currentItem = e.getCurrentItem().clone();
                                currentItem.setAmount(leftAmount);
                                e.setCurrentItem(currentItem);
                            }
                        } else {
                            ItemStack currentItem = e.getCurrentItem().clone();
                            currentItem.setAmount(1);
                            boolean hasAdded = HandelnCommand.addItem(e.getInventory(), currentItem, pLeft.equals((Object) p)) == 0;
                            ItemStack currentItemReplace = e.getCurrentItem().clone();
                            currentItemReplace.setAmount(e.getCurrentItem().getAmount() - (hasAdded ? 1 : 0));
                            e.setCurrentItem(currentItemReplace);
                        }
                    } else if (HandelnCommand.addItem(e.getInventory(), e.getCurrentItem(), pLeft.equals((Object) p)) == 0) {
                        e.setCurrentItem(null);
                    }
                }
            }
        }
    }

    public void startCountdown(final Inventory inv) {
        this.countdown.put(inv, 4);
        ItemStack fence = new ItemStack(Material.IRON_FENCE, 5);
        for (int i = 0; i < 5; ++i) {
            inv.setItem(9 * i + 4, fence);
        }
        final Player pLeft = this.handel.get((Object) inv)[0];
        final Player pRight = this.handel.get((Object) inv)[1];
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) TwiceSystem.getInstance(), new Runnable() {

            @Override
            public void run() {
                if (HandelnCommand.this.countdown.containsKey((Object) inv)) {
                    int count = HandelnCommand.this.countdown.get((Object) inv);
                    if (count == 0) {
                        HandelnCommand.this.countdown.remove((Object) inv);
                        if (HandelnCommand.this.handel.containsKey((Object) inv)) {
                            HandelnCommand.this.handel.remove((Object) inv);
                            pLeft.closeInventory();
                            pRight.closeInventory();
                            HandelnCommand.giveItems(inv, pRight, true);
                            HandelnCommand.giveItems(inv, pLeft, false);
                        }
                    } else {
                        ItemStack fence = new ItemStack(Material.IRON_FENCE, count);
                        for (int i = 0; i < 5; ++i) {
                            inv.setItem(9 * i + 4, fence);
                        }
                        HandelnCommand.this.countdown.put(inv, --count);
                        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) TwiceSystem.getInstance(), (Runnable) this, 20L);
                    }
                }
            }
        }, 20L);
    }

    private void cancelCountdown(Inventory inv) {
        if (this.countdown.containsKey((Object) inv)) {
            this.countdown.remove((Object) inv);
        }
        ItemStack fence = new ItemStack(Material.IRON_FENCE);
        for (int i = 0; i < 5; ++i) {
            inv.setItem(9 * i + 4, fence);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getName() != null && this.handel.containsKey((Object) e.getInventory())) {
            Player p = (Player) e.getPlayer();
            Inventory finalInv = null;
            for (Inventory inv : this.handel.keySet()) {
                if (!this.handel.get((Object) inv)[0].equals((Object) p) && !this.handel.get((Object) inv)[1].equals((Object) p))
                    continue;
                finalInv = inv;
            }
            if (finalInv != null) {
                Player pLeft = this.handel.get((Object) finalInv)[0];
                Player pRight = this.handel.get((Object) finalInv)[1];
                HandelnCommand.giveItems(finalInv, pRight, false);
                pRight.sendMessage("\u00a7cHandel abgebrochen");
                HandelnCommand.giveItems(finalInv, pLeft, true);
                pLeft.sendMessage("\u00a7cHandel abgebrochen");
                this.handel.remove((Object) finalInv);
                if (this.countdown.containsKey((Object) finalInv)) {
                    this.countdown.remove((Object) finalInv);
                }
                pRight.closeInventory();
                pLeft.closeInventory();
            }
        }
    }

    public HashMap<Player, Player> getAnfragen() {
        return this.anfragen;
    }

    public HashMap<Inventory, Integer> getCountdown() {
        return this.countdown;
    }

    public HashMap<Inventory, Player[]> getHandel() {
        return this.handel;
    }

    public ArrayList<Player> getWartecount() {
        return this.wartecount;
    }

}


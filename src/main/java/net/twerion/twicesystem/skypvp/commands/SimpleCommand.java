/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package net.twerion.twicesystem.skypvp.commands;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import net.twerion.twicesystem.skypvp.manager.PerkInventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class SimpleCommand
        implements CommandExecutor {
    public static HashMap<Player, Inventory> potionInvs = new HashMap();
    public static HashMap<UUID, Long> potions_delay = new HashMap();
    private final TwiceSystem twiceSystem;

    public SimpleCommand(TwiceSystem twiceSystem) {
        this.twiceSystem = twiceSystem;
        this.twiceSystem.getCommand("resetstats").setExecutor((CommandExecutor) this);
        this.twiceSystem.getCommand("clearlagg").setExecutor((CommandExecutor) this);
        this.twiceSystem.getCommand("fill").setExecutor((CommandExecutor) this);
        this.twiceSystem.getCommand("goldconvert").setExecutor((CommandExecutor) this);
        this.twiceSystem.getCommand("bodysee").setExecutor((CommandExecutor) this);
        this.twiceSystem.getCommand("giveall").setExecutor((CommandExecutor) this);
        this.twiceSystem.getCommand("potion").setExecutor(this);
        this.twiceSystem.getCommand("perks").setExecutor(this);
    }

    public static void updateBodyseeInv(Player viewer, Player body) {


    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            Player p = (Player) cs;
            if (cmd.getName().equalsIgnoreCase("resetstats")) {
                if (p.hasPermission("twerion.resetstats")) {
                    if (args.length > 0) {
                        this.twiceSystem.getMysql().resetStats(args[0], p);
                    } else {
                        p.sendMessage("\u00a7cVerwendung: /resetstats <Spieler>");
                    }
                } else {
                    p.sendMessage("\u00a7cKeine Berechtigung f\u00fcr diesen Befehl!");
                }
            }
            if (cmd.getName().equalsIgnoreCase("potion") || cmd.getName().equalsIgnoreCase("potions") || cmd.getName().equalsIgnoreCase("braustand")) {
                if (p.hasPermission(TwiceSystem.getPotionsPermission())) {
                    if (!potions_delay.containsKey(p.getUniqueId()) || System.currentTimeMillis() - potions_delay.get(p.getUniqueId()) >= 3600000L) {
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16417));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16418));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16451));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16420));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16421));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16454));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16456));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16425));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16458));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16427));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16428));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16461));
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.POTION, 1, (short) 16462));
                        p.sendMessage("\u00a7bTr\u00e4nke erhalten");
                        potions_delay.put(p.getUniqueId(), System.currentTimeMillis());
                    } else {
                        p.sendMessage("\u00a7cDiesen Befehl kannst du nur jede Stunde ausf\u00fchren");
                    }
                } else {
                    p.sendMessage("\u00a7cKeine Berechtigung f\u00fcr diesen Befehl!");
                }
            } else if (cmd.getName().equalsIgnoreCase("giveall") || cmd.getName().equalsIgnoreCase("ga")) {
                if (p.hasPermission(TwiceSystem.getAdminPermission())) {
                    ItemStack is = p.getItemInHand();
                    if (is == null || is.getType() == Material.AIR) {
                        p.sendMessage("\u00a7cDu hast kein Item in der Hand");
                        return false;
                    }
                    Bukkit.getOnlinePlayers().stream().filter(on -> on != p).map(on -> {
                        TwiceSystem.addItemToInventoryAndDropIfFull(on, p.getItemInHand());
                        return on;
                    }).forEachOrdered(on -> on.sendMessage("\u00a77[\u00a75GiveAll\u00a77] \u00a75" + p.getItemInHand().getType().name() + " erhalten"));
                    p.sendMessage("\u00a77[\u00a75GiveAll\u00a77] \u00a75Items verteilt");
                } else {
                    p.sendMessage("\u00a7cDu hast keine Rechte f\u00fcr diesen Befehl");
                }
            } else if (cmd.getName().equalsIgnoreCase("bodysee")) {
                if (p.hasPermission(TwiceSystem.getBodyseePermission())) {
                    if (args.length == 1) {
                        Player look = Bukkit.getPlayer((String) args[0]);
                        if (look != null && look.isOnline()) {
                            if (look.equals((Object) p)) {
                                p.sendMessage("\u00a7cDu kannst dein eigenes Inventar nicht anschauen");
                                return true;
                            }
                            p.openInventory(Bukkit.createInventory(null, (int) 9, (String) ("Inventar: " + look.getName())));
                            SimpleCommand.updateBodyseeInv(p, look);
                        } else {
                            p.sendMessage("\u00a7cSpieler nicht online");
                        }
                    } else {
                        p.sendMessage("\u00a7c/invsee <Spieler>");
                    }
                } else {
                    p.sendMessage("\u00a7cDu hast keine Rechte f\u00fcr diesen Befehl");
                }
            } else if (cmd.getName().equalsIgnoreCase("goldconvert") || cmd.getName().equalsIgnoreCase("goldswitch")) {
                if (p.hasPermission(TwiceSystem.getGoldswitchPermission())) {
                    int nuggetCount = 0;
                    for (int i = 0; i < p.getInventory().getContents().length; ++i) {
                        ItemStack is = p.getInventory().getContents()[i];
                        if (is == null || is.getType() != Material.GOLD_NUGGET) continue;
                        nuggetCount += is.getAmount();
                    }
                    p.getInventory().remove(Material.GOLD_NUGGET);
                    if (nuggetCount >= 9) {
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.GOLD_INGOT, (int) Math.floor((float) nuggetCount / 9.0f)));
                    }
                    if (nuggetCount % 9 != 0) {
                        TwiceSystem.addItemToInventoryAndDropIfFull(p, new ItemStack(Material.GOLD_NUGGET, nuggetCount % 9));
                    }
                    p.sendMessage("\u00a7bGoldconvert ausgef\u00fchrt");
                } else {
                    p.sendMessage("\u00a7cKeine Berechtigung f\u00fcr diesen Befehl!");
                }
            } else if (cmd.getName().equalsIgnoreCase("fill")) {
                if (p.hasPermission(TwiceSystem.getFillPermission())) {
                    int i;
                    int amount = 0;
                    for (i = 0; i < p.getInventory().getContents().length; ++i) {
                        ItemStack is = p.getInventory().getContents()[i];
                        if (is == null || is.getType() != Material.GLASS_BOTTLE) continue;
                        amount += is.getAmount();
                    }
                    p.getInventory().remove(Material.GLASS_BOTTLE);
                    for (i = 0; i < amount; ++i) {
                        TwiceSystem.addItemToInventoryAndDropGlasses(p, new ItemStack(Material.POTION));
                    }
                    p.sendMessage("\u00a7bFill ausgef\u00fchrt");
                } else {
                    p.sendMessage("\u00a7cKeine Berechtigung f\u00fcr diesen Befehl!");
                }
            } else if (cmd.getName().equalsIgnoreCase("clearlagg")) {
                if (p.hasPermission(TwiceSystem.getAdminPermission())) {
                    p.getWorld().getEntitiesByClass(Item.class).forEach(e -> e.remove());
                    p.sendMessage("\u00a73Items aus der Welt " + p.getWorld().getName() + " entfernt!");
                } else {
                    p.sendMessage("\u00a7cDu darfst das nicht!");
                }
            }
            if (cmd.getName().equalsIgnoreCase("perks") || cmd.getName().equalsIgnoreCase("perk")) {
                PerkInventoryManager.openPerksInventory(p);
            }
        } else {
            cs.sendMessage("\u00a7cDu bist kein Spieler");
        }
        return true;
    }
}


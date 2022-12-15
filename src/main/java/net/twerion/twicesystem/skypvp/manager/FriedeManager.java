/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.projectiles.ProjectileSource
 */
package net.twerion.twicesystem.skypvp.manager;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriedeManager
        implements CommandExecutor,
        Listener {
    private final TwiceSystem twiceSystem;
    private final HashMap<Player, Player> FRIEDE_ANFRAGEN = new HashMap();
    private final List<String[]> FRIEDE_LIST = new ArrayList<String[]>();

    public FriedeManager(TwiceSystem twiceSystem) {
        this.twiceSystem = twiceSystem;
        this.twiceSystem.getCommand("friede").setExecutor((CommandExecutor) this);
        this.twiceSystem.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) twiceSystem);
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            Player p = (Player) cs;
            if (cmd.getName().equalsIgnoreCase("friede")) {
                if (args.length != 1) {
                    p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7cRichtige Benutzung:");
                    p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7c/friede <Spieler> - Toggle den Friede-Status");
                    p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7c/friede list           - Zeigt die Friedenliste an");
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (this.FRIEDE_LIST.size() != 0) {
                        p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7aSpieler mit denen du \u00a7eFrieden \u00a7ageschlossen hast:");
                        StringBuilder stringBuilder = new StringBuilder();
                        int current = -1;
                        for (String[] pp : this.FRIEDE_LIST) {
                            ++current;
                            if (pp[0].equals(p.getName())) {
                                if (current == this.FRIEDE_LIST.size()) {
                                    stringBuilder.append("\u00a7a" + pp[1]);
                                    continue;
                                }
                                stringBuilder.append("\u00a7a" + pp[1] + "\u00a77, ");
                                continue;
                            }
                            if (!pp[1].equals(p.getName())) continue;
                            if (current == this.FRIEDE_LIST.size()) {
                                stringBuilder.append("\u00a7a" + pp[0]);
                                continue;
                            }
                            stringBuilder.append("\u00a7a" + pp[0] + "\u00a77, ");
                        }
                        p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] " + stringBuilder.toString());
                    } else {
                        p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7cDu hast derzeit mit keinem Spieler \u00a7eFrieden \u00a7cgeschlossen.");
                    }
                } else {
                    Player to = Bukkit.getPlayer((String) args[0]);
                    if (to != null && to.isOnline()) {
                        if (to.equals((Object) p)) {
                            p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7cDu kannst nicht mit dir selber Frieden schlie\u00dfen.");
                            return false;
                        }
                        boolean isPeace = false;
                        String[] toRemove = null;
                        for (String[] pp : this.FRIEDE_LIST) {
                            if (!(pp[0].equals(p.getName()) && pp[1].equals(to.getName()) || pp[0].equals(to.getName()) && pp[1].equals(p.getName())))
                                continue;
                            isPeace = true;
                            toRemove = pp;
                        }
                        if (isPeace) {
                            if (Bukkit.getPlayer((String) toRemove[0]) != null && Bukkit.getPlayer((String) toRemove[0]).isOnline()) {
                                Bukkit.getPlayer((String) toRemove[0]).sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7cDer Friede zwischen dir und \u00a7e" + toRemove[1] + " \u00a7cwurde aufgel\u00f6st.");
                            }
                            if (Bukkit.getPlayer((String) toRemove[1]) != null && Bukkit.getPlayer((String) toRemove[1]).isOnline()) {
                                Bukkit.getPlayer((String) toRemove[1]).sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7cDer Friede zwischen dir und \u00a7e" + toRemove[0] + " \u00a7cwurde aufgel\u00f6st.");
                            }
                            this.FRIEDE_LIST.remove(toRemove);
                            TwiceSystem.getInstance().getMysql().quitFriede(toRemove[0], toRemove[1]);
                        } else if (this.FRIEDE_ANFRAGEN.containsKey((Object) to) && this.FRIEDE_ANFRAGEN.get((Object) to).equals((Object) p)) {
                            this.FRIEDE_ANFRAGEN.remove((Object) to);
                            p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7aDu hast mit \u00a76" + to.getName() + " \u00a7aFrieden geschlossen.");
                            to.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7aDu hast mit \u00a76" + p.getName() + " \u00a7aFrieden geschlossen.");
                            this.FRIEDE_LIST.add(new String[]{to.getName(), p.getName()});
                            TwiceSystem.getInstance().getMysql().setFriede(p.getName(), to.getName());
                        } else {
                            this.FRIEDE_ANFRAGEN.put(p, to);
                            p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7aDu hast eine Friedensanfrage an \u00a76" + to.getName() + " \u00a7ageschickt.");
                            to.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a76" + p.getName() + " \u00a7awill mit dir Frieden schlie\u00dfen.");
                            to.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7a/friede " + p.getName());
                        }
                    } else {
                        p.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7cDer Spieler muss online sein.");
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p1 = (Player) e.getEntity();
            Player p2 = null;
            if (e.getDamager() instanceof Player) {
                p2 = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
                p2 = (Player) ((Projectile) e.getDamager()).getShooter();
            }
            if (p2 != null && this.isPeace(p1, p2)) {
                e.setCancelled(true);
                p2.sendMessage("\u00a78[\u00a7dCandySucht\u00a78] \u00a7cDu hast mit diesem Spieler Frieden geschlossen!");
            }
        }
    }

    public boolean isPeace(Player p1, Player p2) {
        for (String[] pp : this.FRIEDE_LIST) {
            if (!(pp[0].equals(p1.getName()) && pp[1].equals(p2.getName()) || pp[0].equals(p2.getName()) && pp[1].equals(p1.getName())))
                continue;
            return true;
        }
        return false;
    }

    public boolean isPeace(String p1, String p2) {
        for (String[] pp : this.FRIEDE_LIST) {
            if (!(pp[0].equals(p1) && pp[1].equals(p2) || pp[0].equals(p2) && pp[1].equals(p1))) continue;
            return true;
        }
        return false;
    }

    public TwiceSystem getTwiceSystem() {
        return this.twiceSystem;
    }

    public HashMap<Player, Player> getFRIEDE_ANFRAGEN() {
        return this.FRIEDE_ANFRAGEN;
    }

    public List<String[]> getFRIEDE_LIST() {
        return this.FRIEDE_LIST;
    }
}


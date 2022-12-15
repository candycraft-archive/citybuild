/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package net.twerion.twicesystem.skypvp.commands;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class StatsCommand
        implements CommandExecutor {
    private final TwiceSystem twiceSystem;

    public StatsCommand(TwiceSystem twiceSystem) {
        this.twiceSystem = twiceSystem;
        this.twiceSystem.getCommand("stats").setExecutor((CommandExecutor) this);
        this.twiceSystem.getCommand("ranking").setExecutor((CommandExecutor) this);
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            Player p = (Player) cs;
            if (cmd.getName().equalsIgnoreCase("stats")) {
                if (args.length == 0) {
                    this.sendStatsMessage(p, p.getName());
                } else if (args.length == 1) {
                    String name = args[0];
                    Player other = Bukkit.getPlayer((String) args[0]);
                    if (other != null) {
                        name = other.getName();
                    }
                    this.sendStatsMessage(p, name);
                } else {
                    p.sendMessage("\u00a78[\u00a7cCandySucht\u00a78] \u00a7cRichtige Benutzung: /stats <Spieler>");
                }
            } else if (cmd.getName().equalsIgnoreCase("ranking")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("1")) {
                        this.sendRankingMessage(p, 1);
                    } else if (args[0].equalsIgnoreCase("2")) {
                        this.sendRankingMessage(p, 2);
                    } else {
                        this.sendRankingMessage(p, 1);
                    }
                } else {
                    this.sendRankingMessage(p, 1);
                }
            }
        } else {
            cs.sendMessage("\u00a7cDu bist kein Spieler");
        }
        return true;
    }

    public void sendStatsMessage(Player p, String name) {
        this.twiceSystem.getMysql().containsPlayer(name, this.twiceSystem.getMysql().table_stats, t -> {
            if (t.booleanValue()) {
                this.twiceSystem.getMysql().getKills(name, kills -> this.twiceSystem.getMysql().getDeaths(name, deaths -> {
                    float kd = deaths != 0 ? (float) Math.round((float) kills.intValue() / 1.0f / (float) deaths.intValue() * 100.0f) / 100.0f : (float) kills.intValue();
                    this.twiceSystem.getMysql().getKillstreak(name, killstreak -> this.twiceSystem.getMysql().getRang(name, rang -> Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> {
                        p.sendMessage("\u00a76 - Stats von \u00a7e" + name + " \u00a76- ");
                        p.sendMessage("\u00a76 Rang: \u00a7e#" + rang);
                        p.sendMessage("\u00a76 Kills: \u00a7e" + kills);
                        p.sendMessage("\u00a76 Deaths: \u00a7e" + deaths);
                        p.sendMessage("\u00a76 K/D: \u00a7e" + kd);
                        p.sendMessage("\u00a76 max. Killstreak: \u00a7e" + killstreak);
                        p.sendMessage("\u00a76 - Stats von \u00a7e" + name + " \u00a76- ");
                    }), true), true);
                }, true), false);
            } else {
                p.sendMessage("\u00a78[\u00a7cCandySucht\u00a78] \u00a7cDer Spieler \u00a7e" + name + " \u00a7chat noch kein Citybuild gespielt.");
            }
        });
    }

    public void sendRankingMessage(Player p, int site) {
        if (site == 1) {
            this.twiceSystem.getMysql().getTop10(map -> Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> {
                p.sendMessage("\u00a78\u27a4 \u00a7aRanking nach Kills, Seite\u00a78: \u00a7e1");
                int nr = 0;
                p.sendMessage("");
                for (String player : map.keySet()) {
                    p.sendMessage("   \u00a78#\u00a7e" + ++nr + " \u00a7a" + player + " \u00a78\u27a4 \u00a7eKills\u00a78: \u00a76" + map.get(player));
                }
                p.sendMessage("");
            }));
        } else if (site == 2) {
            this.twiceSystem.getMysql().getTop(map -> {
                if (map.isEmpty()) {
                    p.sendMessage("\u00a78[\u00a7cCandySucht\u00a78] \u00a7cEs gibt keinen 11. Platz!");
                    return;
                }
                Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> {
                    p.sendMessage("\u00a78\u27a4 \u00a7aRanking nach Kills, Seite\u00a78: \u00a7e2");
                    int nr = 10;
                    p.sendMessage("");
                    for (String player : map.keySet()) {
                        p.sendMessage("   \u00a78#\u00a7e" + ++nr + " \u00a7a" + player + " \u00a78\u27a4 \u00a7eKills\u00a78: \u00a76" + map.get(player));
                    }
                    p.sendMessage("");
                });
            }, 10, 20);
        } else {
            this.twiceSystem.getMysql().getTop10(map -> Bukkit.getScheduler().runTask((Plugin) TwiceSystem.getInstance(), () -> {
                p.sendMessage("\u00a78\u27a4 \u00a7aRanking nach Kills, Seite\u00a78: \u00a7e3");
                int nr = 0;
                p.sendMessage("");
                for (String player : map.keySet()) {
                    p.sendMessage("   \u00a78#\u00a7e" + ++nr + " \u00a7a" + player + " \u00a78\u27a4 \u00a7eKills\u00a78: \u00a76" + map.get(player));
                }
                p.sendMessage("");
            }));
        }
    }
}


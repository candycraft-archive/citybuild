/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package net.twerion.twicesystem.skypvp.sql;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import net.twerion.twicesystem.skypvp.impl.Perk;
import net.twerion.twicesystem.skypvp.manager.PerkInventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mysql {
    public String host = null;
    public String user = null;
    public String pass = null;
    public String db = null;
    public Connection conn = null;
    public Statement st = null;
    public String table_stats = "twicesystem_stats";
    public String table_friede = "twicesystem_friede";
    public String table_perks = "twicesystem_perks";
    public String table_scoreboard = "twicesystem_scoreboard";

    public Mysql(String host, String user, String pass, String db) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.db = db;
    }

    public boolean connect() {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":3306/" + this.db + "?user=" + this.user + "&password=" + this.pass + "&autoReconnect=true");
            this.createTables();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isconnected() {
        try {
            if (!this.conn.isValid(3)) {
                this.reconnect();
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void reconnect() {
        this.close();
        this.connect();
    }

    public void updateSync(String query) {
        try {
            if (!this.isconnected()) {
                this.reconnect();
            }
            this.st = this.conn.createStatement();
            this.st.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void updateSyncWithoutError(String query) {
        try {
            if (!this.isconnected()) {
                this.reconnect();
            }
            this.st = this.conn.createStatement();
            this.st.executeUpdate(query);
        } catch (SQLException sQLException) {
            sQLException.printStackTrace();
        }
    }

    public void updateAsync(String query) {
        Bukkit.getScheduler().runTaskAsynchronously((Plugin) TwiceSystem.getInstance(), () -> this.updateSync(query));
    }

    public ResultSet querySync(String query) {
        ResultSet rs = null;
        try {
            if (!this.isconnected()) {
                this.reconnect();
            }
            this.st = this.conn.createStatement();
            rs = this.st.executeQuery(query);
        } catch (SQLException sQLException) {
            sQLException.printStackTrace();
        }
        return rs;
    }

    public void queryAsync(String query, MysqlCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously((Plugin) TwiceSystem.getInstance(), () -> callback.callback(this.querySync(query)));
    }

    public void createTables() {
        this.updateSync("CREATE TABLE IF NOT EXISTS " + this.table_stats + " (Name LONGTEXT, Kills INT, Tode INT, Killstreak INT)");
        this.updateSync("CREATE TABLE IF NOT EXISTS " + this.table_friede + " (Name1 LONGTEXT, Name2 LONGTEXT)");
        this.updateSync("CREATE TABLE IF NOT EXISTS " + this.table_scoreboard + " (Name LONGTEXT, color VARCHAR(1))");
        String perks = "";
        for (Perk perk : Perk.values()) {
            perks = perks + ", " + perk.perkName + " INT DEFAULT 1";
        }
        this.updateSync("CREATE TABLE IF NOT EXISTS " + this.table_perks + " (Name LONGTEXT" + perks + ")");
        this.checkAndAddColumnsSync();
    }

    public void getColor(String name, Consumer<String> consumer) {
        this.queryAsync("SELECT color FROM " + this.table_scoreboard + " WHERE Name = '" + name + "'", rs -> {
            try {
                if (rs.next()) {
                    consumer.accept(rs.getString(1));
                    return;
                }
            } catch (SQLException ex) {
                Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
            }
            consumer.accept("3");
        });
    }

    public void saveColor(String name, char color) {
        this.colorOfPlayerExists(name, t -> {
            if (!t.booleanValue()) {
                this.updateAsync("INSERT INTO " + this.table_scoreboard + " (Name, color) VALUES ('" + name + "','" + color + "')");
            } else {
                this.updateAsync("UPDATE " + this.table_scoreboard + " SET color = '" + color + "' WHERE Name = '" + name + "'");
            }
        });
    }

    public void colorOfPlayerExists(String name, Consumer<Boolean> consumer) {
        this.queryAsync("SELECT color FROM " + this.table_scoreboard + " WHERE Name = '" + name + "'", rs -> {
            try {
                if (rs.next()) {
                    consumer.accept(true);
                    return;
                }
                consumer.accept(false);
            } catch (SQLException ex) {
                Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void close() {
        try {
            this.conn.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void getKills(String p, final MysqlCallbackObject callback, boolean sync) {
        if (sync) {
            ResultSet rs = this.querySync("SELECT Kills FROM " + this.table_stats + " WHERE Name='" + p + "'");
            try {
                if (rs.next()) {
                    callback.callback(rs.getInt(1));
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(0);
            return;
        }
        this.queryAsync("SELECT Kills FROM " + this.table_stats + " WHERE Name='" + p + "'", new MysqlCallback() {

            @Override
            public void callback(ResultSet rs) {
                try {
                    if (rs.next()) {
                        callback.callback(rs.getInt(1));
                        return;
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
                callback.callback(0);
            }
        });
    }

    public void getDeaths(String p, final MysqlCallbackObject callback, boolean sync) {
        if (sync) {
            ResultSet rs = this.querySync("SELECT Tode FROM " + this.table_stats + " WHERE Name='" + p + "'");
            try {
                if (rs.next()) {
                    callback.callback(rs.getInt(1));
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(0);
            return;
        }
        this.queryAsync("SELECT Tode FROM " + this.table_stats + " WHERE Name='" + p + "'", new MysqlCallback() {

            @Override
            public void callback(ResultSet rs) {
                try {
                    if (rs.next()) {
                        callback.callback(rs.getInt(1));
                        return;
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
                callback.callback(0);
            }
        });
    }

    public void getKillstreak(String p, final MysqlCallbackObject<Integer> callback, boolean sync) {
        if (sync) {
            ResultSet rs = this.querySync("SELECT Killstreak FROM " + this.table_stats + " WHERE Name='" + p + "'");
            try {
                if (rs.next()) {
                    callback.callback(rs.getInt(1));
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(0);
            return;
        }
        this.queryAsync("SELECT Killstreak FROM " + this.table_stats + " WHERE Name='" + p + "'", new MysqlCallback() {

            @Override
            public void callback(ResultSet rs) {
                try {
                    if (rs.next()) {
                        callback.callback(rs.getInt(1));
                        return;
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
                callback.callback(0);
            }
        });
    }

    public void containsPlayer(Player p, String table, MysqlCallbackObject<Boolean> callback) {
        this.queryAsync("SELECT * FROM " + table + " WHERE Name='" + p.getName() + "'", rs -> {
            try {
                if (rs.next()) {
                    callback.callback(true);
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(false);
        });
    }

    public void containsPlayer(String p, String table, MysqlCallbackObject<Boolean> callback) {
        this.queryAsync("SELECT * FROM " + table + " WHERE Name='" + p + "'", rs -> {
            try {
                if (rs.next()) {
                    callback.callback(true);
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(false);
        });
    }

    public void increaseKills(Player p) {
        this.containsPlayer(p, this.table_stats, b -> {
            if (!b.booleanValue()) {
                this.updateSync("INSERT INTO " + this.table_stats + " (Name, Kills, Tode, Killstreak) VALUES ('" + p.getName() + "', 1, 0, 0)");
            } else {
                this.getKills(p.getName(), kills -> this.updateSync("UPDATE " + this.table_stats + " SET Kills=" + (kills + 1) + " WHERE Name='" + p.getName() + "'"), true);
            }
        });
    }

    public void resetStats(String p, Player player) {
        this.containsPlayer(p, this.table_stats, b -> {
            if (!b.booleanValue()) {
                player.sendMessage("\u00a7cDer Spieler " + p + " ist nicht in der Datenbank eingetragen.");
            } else {
                this.updateSync("UPDATE " + this.table_stats + " SET Kills=0 WHERE Name='" + p + "'");
                this.updateSync("UPDATE " + this.table_stats + " SET Tode=0 WHERE Name='" + p + "'");
                this.updateSync("UPDATE " + this.table_stats + " SET Killstreak=0 WHERE Name='" + p + "'");
                player.sendMessage("\u00a7aDie Stats von " + p + " wurden zur\u00fcckgesetzt!");
            }
        });
    }

    public void increaseDeaths(Player p) {
        this.containsPlayer(p, this.table_stats, b -> {
            if (!b.booleanValue()) {
                this.updateSync("INSERT INTO " + this.table_stats + " (Name, Kills, Tode, Killstreak) VALUES ('" + p.getName() + "', 0, 1, 0)");
            } else {
                this.getDeaths(p.getName(), tode -> this.updateSync("UPDATE " + this.table_stats + " SET Tode=" + (tode + 1) + " WHERE Name='" + p.getName() + "'"), true);
            }
        });
    }

    public void updateKillstreak(Player p, int newKillstreak) {
        this.containsPlayer(p, this.table_stats, b -> {
            if (!b.booleanValue()) {
                this.updateSync("INSERT INTO " + this.table_stats + " (Name, Kills, Tode, Killstreak) VALUES ('" + p.getName() + "', 0, 0, " + newKillstreak + ")");
            } else {
                this.updateSync("UPDATE " + this.table_stats + " SET Killstreak=" + newKillstreak + " WHERE Name='" + p.getName() + "'");
            }
        });
    }

    public void checkAndAddColumnsSync() {
        ResultSet rs = this.querySync("SHOW COLUMNS FROM " + this.table_perks);
        ArrayList<String> beforePerks = new ArrayList<String>();
        try {
            while (rs.next()) {
                if (rs.getString(1).equals("Name")) continue;
                beforePerks.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Perk perk : Perk.values()) {
            if (beforePerks.contains(perk.perkName)) continue;
            this.updateSync("ALTER TABLE " + this.table_perks + " ADD " + perk.perkName + " INT DEFAULT 1");
            System.out.println("Perk " + perk.perkName + " added to database");
        }
    }

    public void isPerkActive(Player p, Perk perk, MysqlCallbackObject<Boolean> callback, boolean sync) {
        if (sync) {
            ResultSet rs2 = this.querySync("SELECT " + perk.perkName + " FROM " + this.table_perks + " WHERE Name='" + p.getName() + "'");
            try {
                if (rs2.next()) {
                    callback.callback(rs2.getInt(1) == 1);
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(true);
            return;
        }
        this.queryAsync("SELECT " + perk.perkName + " FROM " + this.table_perks + " WHERE Name='" + p.getName() + "'", rs -> {
            try {
                if (rs.next()) {
                    callback.callback(rs.getInt(1) == 1);
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(true);
        });
    }

    public void updatePerk(Player p, Perk perk, boolean active) {
        this.containsPlayer(p, this.table_perks, b -> {
            int bool = active ? 1 : 0;
            int n = bool;
            if (!b.booleanValue()) {
                this.updateSync("INSERT INTO " + this.table_perks + " (Name, " + perk.perkName + ") VALUES ('" + p.getName() + "', " + bool + ")");
            } else {
                this.updateSync("UPDATE " + this.table_perks + " SET " + perk.perkName + "=" + bool + " WHERE Name='" + p.getName() + "'");
            }
            PerkInventoryManager.queryAllPerksAndUpdateInv(p);
        });
    }

    public void getRang(String p, MysqlCallbackObject<Integer> callback, boolean sync) {
        if (sync) {
            ResultSet rs2 = this.querySync("SELECT Name FROM " + this.table_stats + " ORDER BY Kills DESC");
            int nr = 0;
            try {
                while (rs2.next()) {
                    ++nr;
                    if (!rs2.getString(1).equalsIgnoreCase(p)) continue;
                    callback.callback(nr);
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(nr + 1);
            return;
        }
        this.queryAsync("SELECT Name FROM " + this.table_stats + " ORDER BY Kills DESC", rs -> {
            int nr = 0;
            try {
                while (rs.next()) {
                    ++nr;
                    if (!rs.getString(1).equalsIgnoreCase(p)) continue;
                    callback.callback(nr);
                    return;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(nr + 1);
        });
    }

    public void getTop10(MysqlCallbackObject<HashMap<String, Integer>> callback) {
        this.queryAsync("SELECT Name, Kills FROM " + this.table_stats + " ORDER BY Kills DESC LIMIT 10", rs -> {
            LinkedHashMap<String, Integer> top10 = new LinkedHashMap<String, Integer>();
            try {
                while (rs.next()) {
                    top10.put(rs.getString(1), rs.getInt(2));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(top10);
        });
    }

    public void getTop(MysqlCallbackObject<HashMap<String, Integer>> callback, int min, int max) {
        this.queryAsync("SELECT Name, Kills FROM " + this.table_stats + " ORDER BY Kills DESC LIMIT " + max, rs -> {
            LinkedHashMap<String, Integer> top10 = new LinkedHashMap<String, Integer>();
            try {
                int i = 0;
                while (rs.next()) {
                    if (++i <= min) continue;
                    top10.put(rs.getString(1), rs.getInt(2));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(top10);
        });
    }

    public void getFriedeListOfPlayer(String p, MysqlCallbackObject<List<String>> callback) {
        this.queryAsync("SELECT Name1, Name2 FROM " + this.table_friede + " WHERE Name1='" + p + "' OR Name2='" + p + "'", rs -> {
            ArrayList<String> list = new ArrayList<String>();
            try {
                while (rs.next()) {
                    if (rs.getString(1).equalsIgnoreCase(p)) {
                        list.add(rs.getString(2));
                        continue;
                    }
                    list.add(rs.getString(1));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            callback.callback(list);
        });
    }

    public void setFriede(String p1, String p2) {
        this.getFriedeListOfPlayer(p1, list -> {
            if (!list.contains(p2)) {
                this.updateSync("INSERT INTO " + this.table_friede + " (Name1, Name2) VALUES ('" + p1 + "', '" + p2 + "')");
            }
        });
    }

    public void quitFriede(String p1, String p2) {
        this.getFriedeListOfPlayer(p1, list -> {
            if (list.contains(p2)) {
                this.updateSync("DELETE FROM " + this.table_friede + " WHERE Name1='" + p1 + "' AND Name2='" + p2 + "'");
                this.updateSync("DELETE FROM " + this.table_friede + " WHERE Name1='" + p2 + "' AND Name2='" + p1 + "'");
            }
        });
    }

    public void createPlayer(String name) {
        this.updateAsync("INSERT INTO " + this.table_stats + "(Name,Kills,Tode,Killstreak) VALUES ('" + name + "','0','0','0')");
    }

    public static interface MysqlCallback {
        public void callback(ResultSet var1);
    }

    public static interface MysqlCallbackObject<T> {
        public void callback(T var1);
    }

}


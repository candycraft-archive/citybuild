/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package net.twerion.twicesystem.skypvp.impl;

import net.twerion.twicesystem.skypvp.TwiceSystem;
import net.twerion.twicesystem.skypvp.manager.PerkInventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Perk {
    NO_FIRE_DAMAGE("nofiredamage", Material.LAVA_BUCKET, (short) 0, "", "\u00a77Du bekommst nie wieder Feuerschaden"),
    NO_WATER_DAMAGE("nowaterdamage", Material.WATER_BUCKET, (short) 0, "", "\u00a77Du kannst nie wieder ertrinken"),
    DOUBLE_JUMP("doublejump", Material.FEATHER, (short) 0, "", "\u00a77Du kannst in der Luft", "\u00a77einen weiteren Sprung ausf\u00fchren"),
    POTION_CLEAR("potionclear", Material.POTION, (short) 0, "", "\u00a77Du kannst mit Shift-Rechtsklick auf einen Spieler, ", "\u00a77ihm alle 15 Minuten alle Potion-Effekte entfernen"),
    KEEP_XP("keepxp", Material.EXP_BOTTLE, (short) 0, "", "\u00a77Du verlierst beim Sterben keine XP mehr"),
    RUNNER("runner", Material.DIAMOND_BOOTS, (short) 0, "", "\u00a77Du hast dauerhaft Speed II"),
    DOUBLE_XP("doublexp", Material.EXP_BOTTLE, (short) 0, "", "\u00a77Du bekommst immer doppelt so viel XP"),
    NO_HUNGER("nohunger", Material.COOKED_BEEF, (short) 0, "", "\u00a77Du bekommst keinen Hunger"),
    ITEM_NAME("itemname", Material.NAME_TAG, (short) 0, "", "\u00a77Alle Items, die du aufsammelst,", "\u00a77werden nach dir benannt"),
    DROPPER("dropper", Material.DROPPER, (short) 0, "", "\u00a77Du bekommst die Items deines Gegners, ", "\u00a77den du get\u00f6tet hast, in dein Inventar"),
    ARROW_POTION("arrowpotion", Material.ARROW, (short) 0, "", "\u00a77Wenn du jemanden mit einem Pfeil triffst, ", "\u00a77bekommt dieser f\u00fcr 10 Sekunden Langsamkeit"),
    NIGHT_VISION("nightvision", Material.EYE_OF_ENDER, (short) 0, "", "\u00a77Du hast dauerthaft Nachtsicht"),
    ANTI_POISON("antipoison", Material.SPIDER_EYE, (short) 0, "", "\u00a77Du bekommst nie wieder Gift-Schaden"),
    ANTI_FALL_DAMAGE("antifalldamage", Material.DIAMOND_LEGGINGS, (short) 0, "", "\u00a77Du bekommst nie wieder Fallschaden"),
    FAST_BREAK("fastbreak", Material.DIAMOND_PICKAXE, (short) 0, "", "\u00a77Du kannst schneller Bl\u00f6cke abbauen"),
    MOB_SPAWNER("mobspawner", Material.MOB_SPAWNER, (short) 0, "", "\u00a77Wenn du einen Mob-Spawner abbaust,", "\u00a77bekommst du ihn ins Inventar");

    public String perkName;
    public Material material;
    public short data;
    public List<String> lore = new ArrayList<String>();

    private /* varargs */ Perk(String perkName, Material material, short data, String... lore) {
        this.perkName = perkName;
        this.material = material;
        this.data = data;
        this.lore.addAll(Arrays.asList(lore));
    }

    public boolean hasPermission(Player p) {
        if (this == ITEM_NAME && !TwiceSystem.isItemNamePerk()) {
            return false;
        }
        return p.hasPermission(TwiceSystem.getPerkPermission(this));
    }

    public boolean canBeUsed(Player p) {
        return this.hasPermission(p) && PerkInventoryManager.getPerkStateLocal(p, this);
    }

    public boolean isDeactivated() {
        return this == ITEM_NAME && !TwiceSystem.isItemNamePerk();
    }

    public String getPerkName() {
        return this.perkName;
    }

    public Material getMaterial() {
        return this.material;
    }

    public short getData() {
        return this.data;
    }

    public List<String> getLore() {
        return this.lore;
    }
}


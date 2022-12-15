/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 */
package net.twertion.trade.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.twertion.trade.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class User {
    private final Player player;

    public void openTradeUp() {
        Inventory inventory = Bukkit.createInventory(null, (InventoryType)InventoryType.CHEST, (String)"Alchemist");
        List<Integer> blackList = Arrays.asList(3, 5, 13);
        int i = 0;
        while (i != 27) {
            if (!blackList.contains(i)) {
                inventory.setItem(i, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName("\u00a78*\u00a7fKlick\u00a78*").setLore("\u00a77(Die Sachen werden umgewandelt)").build());
            }
            ++i;
        }
        inventory.setItem(13, new ItemStackBuilder(Material.GOLD_NUGGET).setDisplayName("\u00a78*\u00a7fInformation\u00a78*").setLore("\u00a77Man bekommt das umgewandelte Buch automatisch,", "\u00a77wenn man auf eine Glassscheibe klickt.").build());
        inventory.setItem(22, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 14).setDisplayName("\u00a7b\u00a7lTHE SERVER ALCHEMIST").setLore("\u00a7d\u00a7lDieser NPC tauscht...", " ", "\u00a7b\u00a7l\u00d7 \u00a7f2X Enchantment B\u00fccher", "\u00a77(vom gleichen Typ und Level)", "\u00a7d\u00a7l= \u00a7f1X Enchantment B\u00fccher", "\u00a77(vom h\u00f6herem)", " ", "\u00a7b\u00a7l\u00d7 \u00a7f2X Magic Dust", "\u00a77(von der gleichen Seltenheit)", "\u00a7d\u00a7l= \u00a7f1X Magic Dust", "\u00a77(von h\u00f6herer Seltenheit)").build());
        this.player.openInventory(inventory);
    }

    public ItemStack getTradeItem(ItemStack arg0, ItemStack arg1) {
        Integer level;
        if (arg0 == null || arg1 == null) {
            return new ItemStackBuilder(Material.BARRIER).setDisplayName("\u00a7cDer \u00a7eAlchemist \u00a7cfunktioniert nicht so.").build();
        }
        if (arg0.getType() != Material.ENCHANTED_BOOK || arg1.getType() != Material.ENCHANTED_BOOK) {
            return new ItemStackBuilder(Material.BARRIER).setDisplayName("\u00a7cDer \u00a7eAlchemist \u00a7cfunktioniert nicht so.").build();
        }
        EnchantmentStorageMeta arg0Meta = (EnchantmentStorageMeta)arg0.getItemMeta();
        EnchantmentStorageMeta arg1Meta = (EnchantmentStorageMeta)arg1.getItemMeta();
        Map enchantments = arg0Meta.getStoredEnchants();
        HashMap<Enchantment, Integer> newEnchantments = new HashMap<Enchantment, Integer>();
        for (Enchantment enchantment : enchantments.keySet()) {
            level = (Integer)enchantments.get((Object)enchantment);
            if (arg1Meta.hasStoredEnchant(enchantment)) {
                if (level.intValue() == arg1Meta.getStoredEnchantLevel(enchantment) && level.intValue() != enchantment.getMaxLevel()) {
                    level = level + 1;
                } else if (level <= arg1Meta.getStoredEnchantLevel(enchantment)) {
                    level = arg1Meta.getStoredEnchantLevel(enchantment);
                }
            }
            newEnchantments.put(enchantment, level);
        }
        for (Enchantment enchantment : arg1Meta.getStoredEnchants().keySet()) {
            level = (Integer)arg1Meta.getStoredEnchants().get((Object)enchantment);
            if (newEnchantments.containsKey((Object)enchantment)) continue;
            newEnchantments.put(enchantment, level);
        }
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)itemStack.getItemMeta();
        for (Enchantment enchantment : newEnchantments.keySet()) {
            bookMeta.addStoredEnchant(enchantment, ((Integer)newEnchantments.get((Object)enchantment)).intValue(), true);
        }
        itemStack.setItemMeta((ItemMeta)bookMeta);
        return itemStack;
    }

    public Player getPlayer() {
        return this.player;
    }

    public User(Player player) {
        this.player = player;
    }
}


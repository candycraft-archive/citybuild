/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package net.twerion.twicesystem.skypvp.impl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemStackBuilder {
    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemStackBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemStackBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, 1, (short) 0);
        this.itemMeta = this.itemStack.getItemMeta();
        this.setAmount(amount);
    }

    public ItemStackBuilder(Material material, short itemData) {
        this.itemStack = new ItemStack(material, 1, itemData);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemStackBuilder(Material material, int amount, short itemData) {
        this.itemStack = new ItemStack(material, 1, itemData);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemStackBuilder setDisplayName(String displayName) {
        this.itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemStackBuilder setAmount(Integer amount) {
        this.itemStack.setAmount(amount.intValue());
        return this;
    }

    public /* varargs */ ItemStackBuilder setLore(String... lore) {
        this.itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemStackBuilder setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemStackBuilder setEnchantment(Enchantment enchantment, Integer level) {
        this.itemMeta.addEnchant(enchantment, level.intValue(), true);
        return this;
    }

    public ItemStackBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        this.itemStack.addUnsafeEnchantments(enchantments);
        return this;
    }

    public ItemStackBuilder setItemFlag(ItemFlag itemFlag) {
        this.itemMeta.addItemFlags(new ItemFlag[]{itemFlag});
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public ItemMeta getItemMeta() {
        return this.itemMeta;
    }
}


// src/main/java/com/yourmod/enchantments/ShaluguanghuanEnchantment.java
package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WaJueJiEnchantment extends Enchantment {

    public WaJueJiEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{ EquipmentSlot.FEET});
    }
    @Override
    public boolean isTreasureOnly() {
        return false; // 允许在附魔台获得该附魔
    }
    @Override
    public boolean isDiscoverable() {
        return true; // 允许该附魔在附魔表中出现
    }

    @Override
    public int getMaxLevel() {
        return 1; // 可以根据需求调整等级
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        // 定义与其他附魔的兼容性
        return super.checkCompatibility(enchantment);
    }
}

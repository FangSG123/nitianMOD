// src/main/java/com/yourmod/enchantments/ShaluguanghuanEnchantment.java
package com.ntsw.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;

public class ShaluguanghuanEnchantment extends Enchantment {

    public ShaluguanghuanEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public boolean isTreasureOnly() {
        return false; // 允许在附魔台获得该附魔
    }

    @Override
    public int getMaxLevel() {
        return 999; // 可以根据需求调整等级
    }
    @Override
    public boolean isDiscoverable() {
        return true; // 允许该附魔在附魔表中出现
    }
    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        // 定义与其他附魔的兼容性
        return super.checkCompatibility(enchantment);
    }
}

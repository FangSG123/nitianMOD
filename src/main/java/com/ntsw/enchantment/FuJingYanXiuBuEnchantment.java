// src/main/java/com/yourmod/enchantments/ShaluguanghuanEnchantment.java
package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FuJingYanXiuBuEnchantment extends Enchantment {

    public FuJingYanXiuBuEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
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
}

package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MeihuoEnchantment extends Enchantment {

    public MeihuoEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3; // 最大等级为3
    }

    @Override
    public boolean isTreasureOnly() {
        return false; // 可以通过附魔台获得
    }

    @Override
    public boolean isTradeable() {
        return true; // 可以通过交易获得
    }

    @Override
    public boolean isDiscoverable() {
        return true; // 可以通过附魔台获得
    }
}

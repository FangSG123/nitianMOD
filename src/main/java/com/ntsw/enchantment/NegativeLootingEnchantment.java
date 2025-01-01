package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class NegativeLootingEnchantment extends Enchantment {
    public NegativeLootingEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMinCost(int level) {
        return 10; // 附魔所需最低经验值
    }

    @Override
    public int getMaxCost(int level) {
        return 30; // 附魔所需最高经验值
    }

    @Override
    public boolean isTreasureOnly() {
        return false; // 是否仅通过宝藏获取
    }

    @Override
    public boolean isTradeable() {
        return true; // 是否可通过村民交易获取
    }

    @Override
    public boolean isDiscoverable() {
        return true; // 是否可通过附魔台发现
    }
}

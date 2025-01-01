package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;


public class FuFengLiEnchantment extends Enchantment {

    public FuFengLiEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3; // 附魔最大等级为3
    }

    @Override
    public int getMinLevel() {
        return 1;
    }
}

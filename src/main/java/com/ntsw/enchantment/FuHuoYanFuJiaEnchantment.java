package com.ntsw.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;


public class FuHuoYanFuJiaEnchantment extends Enchantment {

    public FuHuoYanFuJiaEnchantment() {
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

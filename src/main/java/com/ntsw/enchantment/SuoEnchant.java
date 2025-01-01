package com.ntsw.enchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;

public class    SuoEnchant extends Enchantment {

    public SuoEnchant() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1; // 如果想要多个级别，可以调整这里
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return  super.canApplyAtEnchantingTable(stack);
    }
}

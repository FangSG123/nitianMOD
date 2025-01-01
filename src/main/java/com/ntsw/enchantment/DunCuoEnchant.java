package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DunCuoEnchant extends Enchantment {

    public DunCuoEnchant() {
        super(Rarity.COMMON, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.OFFHAND});
    }
    @Override
    public boolean isDiscoverable() {
        return true; // 允许该附魔在附魔表中出现
    }
    @Override
    public int getMaxLevel() {
        return 1;  // 附魔仅需一级
    }
    @Override
    public boolean isTreasureOnly() {
        return false; // 允许在附魔台获得该附魔
    }


    @Override
    public boolean canEnchant(ItemStack stack) {
        // 仅允许附魔在盾牌上
        return stack.getItem() instanceof ShieldItem;
    }
}

package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.ItemStack;

public class LuodishuiEnchantment extends Enchantment {

    public LuodishuiEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 仅允许附魔在水桶上
        return stack.is(Items.WATER_BUCKET);
    }

    @Override
    public int getMaxLevel() {
        return 1; // 最大等级为 1
    }

    @Override
    public boolean isTreasureOnly() {
        return false; // 可以通过常规方式获得
    }
}

package com.ntsw.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;

public class WodishengaoEnchantment extends Enchantment {

    public WodishengaoEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 仅允许附魔在镐子上
        return stack.getItem() instanceof PickaxeItem;
    }

    @Override
    public boolean isTreasureOnly() {
        return true; // 设置为宝藏附魔，只能通过特定方式获得
    }

    @Override
    public int getMaxLevel() {
        return 1; // 设定最大等级为 1
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        // 与其他挖掘类附魔互斥
        return super.checkCompatibility(other)  && other != Enchantments.BLOCK_FORTUNE && other != Enchantments.SILK_TOUCH;
    }
}

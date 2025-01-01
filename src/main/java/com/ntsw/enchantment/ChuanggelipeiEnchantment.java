package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;

public class ChuanggelipeiEnchantment extends Enchantment {

    public ChuanggelipeiEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[] { EquipmentSlot.CHEST });
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 仅允许附魔在胸甲上
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.CHEST;
    }

    @Override
    public int getMaxLevel() {
        return 1; // 最大等级为 1
    }

    @Override
    public boolean isTreasureOnly() {
        return true; // 设置为宝藏附魔，只能通过特定方式获得
    }
}

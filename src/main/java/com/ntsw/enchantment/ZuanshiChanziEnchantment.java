
package com.ntsw.enchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ZuanshiChanziEnchantment extends Enchantment {

    public ZuanshiChanziEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinLevel() {
        return 1; // 最小等级
    }

    @Override
    public int getMaxLevel() {
        return 1; // 最大等级
    }

    @Override
    public boolean canApplyAtEnchantingTable(net.minecraft.world.item.ItemStack stack) {
        // 附魔适用于所有工具和武器
        return true;
    }

    @Override
    public boolean canEnchant(net.minecraft.world.item.ItemStack stack) {
        // 附魔适用于所有工具和武器
        return true;
    }
}

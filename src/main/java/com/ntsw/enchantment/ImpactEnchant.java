package com.ntsw.enchantment;

import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ImpactEnchant extends Enchantment {

    public ImpactEnchant() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.CHEST});;  // 注册事件总线
    }

    @Override
    public int getMaxLevel() {
        return 2; // 附魔有两个等级
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 允许附魔在盔甲上
        return stack.getItem() instanceof ArmorItem;
    }
    @Override
    public boolean isCurse() {
        return true; // 将附魔标记为诅咒
    }
    @Override
    public boolean isTreasureOnly() {
        return false; // 允许在附魔台获得该附魔
    }

    // 当玩家受到伤害时施加向上的力
    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 检查玩家的盔甲是否有“冲击”附魔，取最高等级的“冲击”附魔
            int maxImpactLevel = 0;
            for (ItemStack armor : player.getArmorSlots()) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.IMPACT_ENCHANT.get(), armor);
                if (level > maxImpactLevel) {
                    maxImpactLevel = level;
                }
            }

            // 如果有“冲击”附魔，根据等级施加不同的向上力
            if (maxImpactLevel > 0) {
                double upwardForce = 1 * maxImpactLevel;  // 等级1时向上1，等级2时向上2
                player.setDeltaMovement(player.getDeltaMovement().add(0, upwardForce, 0));  // 根据附魔等级增加Y轴速度
            }
        }
    }
}

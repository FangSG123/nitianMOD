package com.ntsw.enchantment;

import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EfficiencyEnchant extends Enchantment {

    public EfficiencyEnchant() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR, new EquipmentSlot[]{ EquipmentSlot.LEGS});
        MinecraftForge.EVENT_BUS.register(this);  // 注册事件总线
    }
    @Override
    public boolean isCurse() {
        return true; // 将附魔标记为诅咒
    }
    @Override
    public boolean isDiscoverable() {
        return true; // 允许该附魔在附魔表中出现
    }
    @Override
    public boolean isTreasureOnly() {
        return false; // 允许在附魔台获得该附魔
    }

    @Override
    public int getMaxLevel() {
        return 5; // 附魔总共有5级
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 仅允许给盔甲进行附魔
        return stack.getItem() instanceof ArmorItem;
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        // 检查是否是玩家
        if (event.getEntity() instanceof Player player) {
            // 获取玩家身上每个盔甲部位的附魔等级
            int efficiencyLevel = getMaxEfficiencyLevel(player);

            if (efficiencyLevel > 0) {
                // 计算无敌帧时长：每级减少20%，五级时无敌帧为0
                int defaultInvulnerabilityTicks = player.invulnerableTime; // 原始无敌帧时长
                float reductionFactor = 1.0f - (efficiencyLevel * 0.20f); // 每级减少20%
                int newInvulnerabilityTicks = Math.max(0, Math.round(defaultInvulnerabilityTicks * reductionFactor));

                player.invulnerableTime = newInvulnerabilityTicks; // 设置减少后的无敌帧时长
            }
        }
    }

    // 获取玩家身上所有装备的最大效率附魔等级
    private int getMaxEfficiencyLevel(Player player) {
        int maxLevel = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.EFFICIENCY_ENCHANT.get(), armor);
            if (level > maxLevel) {
                maxLevel = level;
            }
        }
        return maxLevel;
    }
}

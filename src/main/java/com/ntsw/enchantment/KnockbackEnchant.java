package com.ntsw.enchantment;

import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KnockbackEnchant extends Enchantment {

    public KnockbackEnchant() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
        MinecraftForge.EVENT_BUS.register(this); // 注册事件总线
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
        return 1; // 附魔只需要一个等级
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 仅允许给头盔附魔
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.HEAD;
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            //System.out.println("玩家受伤事件触发");  // 调试输出
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

            // 检查头盔是否带有击退附魔
            if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.KNOCKBACK_ENCHANT.get(), helmet) > 0) {
                System.out.println("头盔带有击退附魔");  // 调试输出
                applyKnockback(player, event.getSource());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            //System.out.println("玩家受伤（包括环境伤害）事件触发");  // 调试输出
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

            // 检查头盔是否带有击退附魔
            if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.KNOCKBACK_ENCHANT.get(), helmet) > 0) {
                //System.out.println("头盔带有击退附魔");  // 调试输出
                applyKnockback(player, event.getSource());
            }
        }
    }

    private void applyKnockback(Player player, DamageSource source) {
        if (source.getEntity() != null) {
            double knockbackStrength = 10.0; // 击退的强度
            //System.out.println("击退逻辑执行");  // 调试输出
            player.knockback(knockbackStrength, source.getEntity().getX() - player.getX(), source.getEntity().getZ() - player.getZ());
        } else {
            // 如果伤害来源为空，例如环境伤害，使用默认击退方向
            player.knockback(5.0, -1, 0);  // 默认向左击退
            //System.out.println("没有伤害来源，使用默认击退");
        }
    }
}

package com.ntsw.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class JiuItem extends Item {
    public JiuItem(Properties properties) {
        super(properties.food(new FoodProperties.Builder()
                .nutrition(4) // 食物恢复量
                .saturationMod(0.3f) // 饱和度
                .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0f) // 反胃效果，10秒 (200 ticks)
                .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && !level.isClientSide) {
            // 给玩家一个双倍伤害的标记
            player.getPersistentData().putBoolean("hasDoubleDamage", true);
            // 设置双倍伤害效果的持续时间
            player.getPersistentData().putLong("doubleDamageEndTime", level.getGameTime() + 200); // 200 ticks = 10 seconds
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
}

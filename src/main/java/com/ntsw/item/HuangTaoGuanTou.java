package com.ntsw.ntsw.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

public class HuangTaoGuanTou extends Item {

    public HuangTaoGuanTou(Properties properties) {
        super(properties.food(new FoodProperties.Builder()
                .nutrition(10) // 食物值
                .saturationMod(0F) // 饱和度
                .alwaysEat() // 总是可以食用
                .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide) {
            // 遍历所有药水效果，排除饱和效果
            for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
                if (effect != MobEffects.SATURATION) { // 跳过饱和效果
                    // 为每个药水效果应用等级为10的效果，持续时间为600 ticks（30秒）
                    entity.addEffect(new MobEffectInstance(effect, 100, 10));
                }
            }

            // 给玩家添加饥饿效果，等级为20，持续时间为600 ticks（30秒）
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 20));
        }

        return super.finishUsingItem(stack, world, entity);
    }
}

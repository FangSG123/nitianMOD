package com.ntsw.item;

import com.ntsw.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DaBianItem extends Item {
    public DaBianItem() {
        super(new Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(1) // 回复一格饱食度
                        .saturationMod(0.1F) // 饱和度，设为较低值
                        .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 600), 1.0F) // 30秒反胃
                        .effect(() -> new MobEffectInstance(MobEffects.POISON, 600), 1.0F)    // 30秒中毒
                        .effect(() -> new MobEffectInstance(ModEffects.REPEL.get(), 600), 1.0F) // 30秒repel效果
                        .alwaysEat() // 即使饱食度满了也可以食用
                        .build()));

    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.literal("好像不能吃.."));
    }
}
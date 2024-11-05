package com.ntsw.item;

import com.ntsw.ModEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;

public class PddItem extends Item {
    public PddItem() {
        super(new Properties()
                .stacksTo(1)
                .durability(1000)); // 设置耐久度（可选）
    }
}
package com.ntsw.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DamageImmunityEffect extends MobEffect {

    public DamageImmunityEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFFFFFF); // 白色效果
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // 无需每刻更新
    }
}

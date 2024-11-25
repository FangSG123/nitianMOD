package com.ntsw.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MingDingZhiSiEffect  extends MobEffect {

    public MingDingZhiSiEffect () {
        super(MobEffectCategory.NEUTRAL, 0xFF0000); // 红色效果
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // 无需每刻更新
    }
}

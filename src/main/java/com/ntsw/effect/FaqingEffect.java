package com.ntsw.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class FaqingEffect extends MobEffect {

    public FaqingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF69B4); // 使用粉红色表示
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每个游戏刻都会触发效果
    }
}

package com.ntsw.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MeiliaoEffect extends MobEffect {

    public MeiliaoEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFF69B4); // 设置药水类别为中性，颜色为粉色
    }

    @Override
    public boolean isBeneficial() {
        return false; // 设置为负面效果
    }
}

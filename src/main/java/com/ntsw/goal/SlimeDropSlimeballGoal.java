package com.ntsw.goal;

import com.ntsw.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class SlimeDropSlimeballGoal extends Goal {
    private final Slime slime;
    private int timer;

    public SlimeDropSlimeballGoal(Slime slime) {
        this.slime = slime;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // 只有在史莱姆拥有 "meiliao" 效果时，才执行该 Goal
        MobEffectInstance effect = slime.getEffect(ModEffects.MEILIAO.get());
        return effect != null && effect.getDuration() > 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.timer = 20; // 初始化计时器为20 tick（1秒）
    }

    @Override
    public void tick() {
        if (timer > 0) {
            timer--;
        } else {
            if (!slime.level().isClientSide) {
                // 生成一个粘液球
                ItemStack slimeball = new ItemStack(Items.SLIME_BALL);
                slime.spawnAtLocation(slimeball);
            }
            // 重置计时器
            timer = 10; // 20 tick，即1秒
        }
    }

    @Override
    public void stop() {
        // 停止生成粘液球
        this.timer = 0;
    }
}

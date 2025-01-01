package com.ntsw.goal;

import com.ntsw.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.warden.Warden;

import java.util.EnumSet;

public class FollowPlayerGoal extends Goal {
    private final Mob mob;
    private final Player targetPlayer;
    private final double speedModifier;

    public FollowPlayerGoal(Mob mob, Player targetPlayer, double speedModifier) {
        this.mob = mob;
        this.targetPlayer = targetPlayer;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // 只有在目标生物仍然拥有 "meiliao" 效果时，才会执行该 Goal
        MobEffectInstance effect = mob.getEffect(ModEffects.MEILIAO.get());
        return effect != null && mob.isAlive() && targetPlayer.isAlive();
    }

    @Override
    public void tick() {
        // 每个 tick 更新生物的移动目标，但不生成仇恨
        if (mob.distanceToSqr(targetPlayer) > 1.0D) { // 距离太近时不移动
            mob.getNavigation().moveTo(targetPlayer, speedModifier);
        }

        // 清除 Warden 的愤怒值
        if (mob instanceof Warden warden) {
            warden.clearAnger(targetPlayer);
        }

        // 清除普通敌对生物的目标，避免生成仇恨
        mob.setLastHurtByMob(null);
        mob.setTarget(null);
        if (mob instanceof Piglin piglin) {
            piglin.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET); // 清除攻击目标
        }
    }

    @Override
    public boolean canContinueToUse() {
        // 当 "meiliao" 效果还在时，继续执行该 Goal
        return this.canUse();
    }

    @Override
    public void stop() {
        // 停止导航
        mob.getNavigation().stop();
        // 确保清除目标，防止产生仇恨
        mob.setLastHurtByMob(null);
        mob.setTarget(null);

        // 停止 Warden 的愤怒行为
        if (mob instanceof Warden warden) {
            warden.clearAnger(targetPlayer);
        }
    }
}

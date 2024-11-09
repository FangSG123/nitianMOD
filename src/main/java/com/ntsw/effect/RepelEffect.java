package com.ntsw.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects; // 引入速度效果类
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class RepelEffect extends MobEffect {

    public RepelEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8B4513); // 使用棕色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            ServerLevel level = (ServerLevel) entity.level();

            // 检测玩家附近的所有生物并触发远离效果
            if (entity instanceof Player player) {
                level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10.0)).stream()
                        .filter(mob -> mob instanceof Mob && !(mob instanceof Player))
                        .forEach(mob -> {
                            // 让生物远离玩家
                            double dx = mob.getX() - player.getX();
                            double dz = mob.getZ() - player.getZ();
                            double distance = Math.sqrt(dx * dx + dz * dz);
                            if (distance > 0) { // 防止除零错误
                                double escapeSpeed = 0.5 + 0.05 * amplifier; // 随着药水等级增加移动速度
                                mob.setDeltaMovement(dx / distance * escapeSpeed, mob.getDeltaMovement().y, dz / distance * escapeSpeed);
                                mob.hurtMarked = true; // 标记实体以更新移动状态
                            }

                            // 移除对玩家的仇恨状态
                            if (mob instanceof Mob hostileMob) {
                                hostileMob.setTarget(null); // 清除目标
                                hostileMob.setLastHurtByMob(null); // 清除仇恨对象
                            }
                        });
            }

            // 检查生物是否带有 RepelEffect，并在 5 格范围内给予玩家速度效果
            if (entity instanceof Mob) {
                level.getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(5.0)).stream()
                        .filter(player -> !player.hasEffect(MobEffects.MOVEMENT_SPEED)) // 过滤掉已有速度效果的玩家
                        .forEach(player -> {
                            // 给玩家添加5秒速度2效果
                            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1)); // 5秒(100 ticks)的速度2效果
                        });
            }
        }
        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每个 tick 都触发效果
    }
}

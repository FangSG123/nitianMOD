package com.ntsw.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class RepelEffect extends MobEffect {

    public RepelEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8B4513); // 使用棕色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !entity.level().isClientSide()) {
            ServerLevel level = (ServerLevel) entity.level();

            // 检测玩家附近的所有生物
            level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10.0)).stream()
                    .filter(mob -> mob instanceof Mob && !(mob instanceof Player)) // 排除玩家自身
                    .forEach(mob -> {
                        // 让生物远离玩家
                        double dx = mob.getX() - player.getX();
                        double dz = mob.getZ() - player.getZ();
                        double distance = Math.sqrt(dx * dx + dz * dz);
                        double escapeSpeed = 0.5 + 1 * amplifier; // 随着药水等级增加移动速度

                        mob.setDeltaMovement(dx / distance * escapeSpeed, mob.getDeltaMovement().y, dz / distance * escapeSpeed);

                        // 移除对玩家的仇恨状态
                        if (mob instanceof Mob hostileMob) {
                            hostileMob.setTarget(null); // 清除目标
                        }
                    });
        }
        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0; // 每秒触发一次
    }
}

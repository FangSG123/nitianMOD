package com.ntsw.effect;

import com.ntsw.ModItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class MeiliaoEffect extends MobEffect {

    public MeiliaoEffect() {
        // 第一个参数为药水类别(正面/负面/中性)，第二个参数为状态显示颜色(16进制)
        super(MobEffectCategory.NEUTRAL, 0xFF69B4);
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }

    /** 每一刻 (tick) 都执行 */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return; // 只在服务端执行
        }

        Level level = entity.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 1. 搜索范围（可自行调大/调小）
        double range = 16.0;
        BlockPos pos = entity.blockPosition();
        AABB box = new AABB(pos).inflate(range);

        // 2. 获取范围内 所有“主手/副手持有 mhapp”的玩家
        List<Player> players = level.getEntitiesOfClass(Player.class, box, this::hasCmappInHand);
        if (players.isEmpty()) {
            return;
        }

        // 3. 找最近那个玩家
        players.sort(Comparator.comparingDouble(entity::distanceToSqr));
        Player closestPlayer = players.get(0);

        // 4. 分别处理：若 entity 是 Mob，用导航；若 entity 是 Player，用推力/传送
        if (entity instanceof Mob mob) {
            // 生物使用AI
            mob.getNavigation().moveTo(closestPlayer, 1.0D);
        }
        else if (entity instanceof Player victim) {
            // 简单演示：对“玩家实体”施加一个拉扯，让其接近 closestPlayer
            // （可改为 teleportTo() 瞬移，或做更多安全判断）
            this.pullPlayerToward(victim, closestPlayer, 0.3D);
        }
    }

    /**
     * 判断玩家是否在主手或副手拿着 mhapp 物品
     */
    private boolean hasCmappInHand(Player player) {
        return player.getMainHandItem().getItem() == ModItems.MHAPP.get()
                || player.getOffhandItem().getItem() == ModItems.MHAPP.get();
    }

    /**
     * 用"推力"的方式将 victim 玩家 拉向 targetPlayer
     */
    private void pullPlayerToward(Player victim, Player targetPlayer, double force) {
        // 如果 victim == targetPlayer，可以选择跳过，避免自我拉扯
        if (victim == targetPlayer) {
            return;
        }

        // 算出从 victim -> target 的方向向量
        Vec3 start = victim.position();
        Vec3 end = targetPlayer.position();
        Vec3 dir = end.subtract(start);

        // 若距离过近，避免一直抖动
        double distance = dir.length();
        if (distance < 0.5) {
            return;
        }

        // 归一化，并乘以 force
        dir = dir.normalize().scale(force);

        // 设定玩家的DeltaMovement (速度)
        // 也可以先取原速度 + dir，叠加一下
        Vec3 finalMotion = victim.getDeltaMovement().add(dir.x, dir.y, dir.z);

        victim.setDeltaMovement(finalMotion);
        victim.hurtMarked = true; // 通知客户端同步
    }
}

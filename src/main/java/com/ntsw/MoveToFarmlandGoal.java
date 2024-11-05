package com.ntsw;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;

public class MoveToFarmlandGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;

    public MoveToFarmlandGoal(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        return findNearbyFarmland() != null;
    }

    @Override
    public void tick() {
        BlockPos targetPos = findNearbyFarmland();
        if (targetPos != null) {
            this.mob.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed);
            // 在耕地上种植作物
            plantCrops(targetPos);
        }
    }

    private BlockPos findNearbyFarmland() {
        Level level = mob.level();
        BlockPos mobPos = mob.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(mobPos.offset(-10, -1, -10), mobPos.offset(10, 1, 10))) {  // 扩大范围至10x10
            if (level.getBlockState(pos).is(Blocks.FARMLAND) && level.isEmptyBlock(pos.above())) {
                return pos.above();
            }
        }
        return null;
    }

    private void plantCrops(BlockPos farmlandPos) {
        Level level = mob.level();
        if (level.isEmptyBlock(farmlandPos) && mob.getRandom().nextInt(100) < 10) { // 10%的概率种植
            level.setBlock(farmlandPos, Blocks.WHEAT.defaultBlockState(), 3);
        }
    }
}

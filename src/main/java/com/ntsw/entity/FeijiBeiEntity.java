package com.ntsw.entity;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FeijiBeiEntity extends Animal {

    public FeijiBeiEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        // 检测是否有骑乘者，并处理飞行逻辑
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            this.handleFlying(player);
        }
    }
    @Nullable
    public Entity getFirstControllingPassenger() {
        return this.getFirstPassenger(); // 返回第一个骑乘者
    }

    // 实现飞行逻辑
    private void handleFlying(Player player) {
        Vec3 motion = player.getDeltaMovement();
        double upwardSpeed = 0.1; // 向上的速度

        if (player.isShiftKeyDown()) { // 玩家按下 Shift 键时下降
            this.setDeltaMovement(motion.x, -upwardSpeed, motion.z);
        } else if (this.level().isClientSide && player instanceof LocalPlayer localPlayer && localPlayer.input.jumping) {
            this.setDeltaMovement(motion.x, upwardSpeed, motion.z);
        } else { // 正常情况下保持平飞
            this.setDeltaMovement(motion.x, 0, motion.z);
        }
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos pos) {
        // 禁用坠落伤害
    }

    @Override
    public boolean isPushable() {
        return false; // 禁用推拉
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isVehicle() && this.getControllingPassenger() instanceof Player; // 允许玩家控制
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty(); // 只允许一个乘客
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && !player.isPassenger()) {
            player.startRiding(this); // 玩家骑乘实体
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // 暂无食物逻辑
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }
}

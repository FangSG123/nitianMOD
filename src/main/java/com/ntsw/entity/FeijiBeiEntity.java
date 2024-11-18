package com.ntsw.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
    public void travel(Vec3 travelVector) {
        // 骑乘时玩家控制
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            Minecraft mc = Minecraft.getInstance();

            // 获取玩家输入
            float forward = player.zza; // 前进/后退（W/S）
            float strafe = player.xxa;  // 左右移动（A/D）

            // 设置朝向
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();

            // 方向计算：按玩家当前方向前进
            Vec3 forwardMotion = Vec3.directionFromRotation(player.getXRot(), player.getYRot()).scale(forward * 0.4);
            Vec3 motion = new Vec3(forwardMotion.x, this.getDeltaMovement().y, forwardMotion.z);

            // 检测鼠标按键：左键上升，右键下降
            if (mc.options.keyAttack.isDown()) { // 鼠标左键
                motion = motion.add(0, 0.3, 0);
            } else if (mc.options.keyUse.isDown()) { // 鼠标右键
                motion = motion.add(0, -0.3, 0);
            }else {
                // 重力逻辑：当未按键时，实体受到重力影响
                motion = motion.add(0, -0.08, 0); // -0.08 是默认重力加速度
            }

            // 限制最大下落速度，避免太快
            if (motion.y < -1.0) {
                motion = new Vec3(motion.x, -1.0, motion.z);
            }

            this.setDeltaMovement(motion); // 设置新的移动速度
            this.move(MoverType.SELF, this.getDeltaMovement()); // 移动实体
        } else {
            super.travel(travelVector); // 默认移动逻辑
        }
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isVehicle() && this.getControllingPassenger() instanceof Player;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity; // 返回 LivingEntity
            }
        }
        return null;
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty(); // 只允许一个乘客
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && !player.isPassenger()) {
            player.startRiding(this); // 玩家开始骑乘
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos pos) {
        // 禁用坠落伤害
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // 不支持食物逻辑
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null; // 不支持繁殖逻辑
    }
}

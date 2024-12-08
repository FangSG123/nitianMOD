package com.ntsw.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FeijiBeiEntity extends Animal {

    private static final int MAX_FEED_TIME = 6000; // 最大时间限制
    private static final int FEED_INCREMENT = 600; // 每次喂食增加时间
    public static final int MAX_PROGRESS = 5000; // 进度条最大值
    private static final EntityDataAccessor<Integer> REMAINING_MOVE_TIME = SynchedEntityData.defineId(FeijiBeiEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PROGRESS = SynchedEntityData.defineId(FeijiBeiEntity.class, EntityDataSerializers.INT); // 进度条

    public FeijiBeiEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(REMAINING_MOVE_TIME, 0); // 初始化同步数据
        this.entityData.define(PROGRESS, 0); // 初始化进度条
    }

    public void setRemainingMoveTime(int time) {
        this.entityData.set(REMAINING_MOVE_TIME, time);
    }

    public int getRemainingMoveTime() {
        return this.entityData.get(REMAINING_MOVE_TIME);
    }

    public void setProgress(int progress) {
        this.entityData.set(PROGRESS, progress);
    }

    public int getProgress() {
        return this.entityData.get(PROGRESS);
    }

    @Override
    public void travel(Vec3 travelVector) {
        Minecraft mc = Minecraft.getInstance();
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            if (getRemainingMoveTime() > 0) {
                setRemainingMoveTime(getRemainingMoveTime() - 1); // 减少剩余时间

                // 获取玩家输入
                float forward = player.zza; // 前进/后退（W/S）
                float strafe = player.xxa;  // 左右移动（A/D）
                this.setYRot(player.getYRot());
                this.yRotO = this.getYRot();

                Vec3 forwardMotion = Vec3.directionFromRotation(player.getXRot(), player.getYRot()).scale(forward * 0.4);
                Vec3 motion = new Vec3(forwardMotion.x, this.getDeltaMovement().y, forwardMotion.z);

                // 鼠标左键上升，右键下降
                if (mc.options.keyAttack.isDown()) { // 鼠标左键
                    motion = motion.add(0, 0.20, 0);
                    int currentProgress = getProgress();
                    setProgress(currentProgress + 10);
                } else if (mc.options.keyUse.isDown()) { // 鼠标右键
                    motion = motion.add(0, -0.20, 0);
                } else {
                    // 重力逻辑
                    motion = motion.add(0, -0.08, 0); // -0.08 是默认重力加速度
                }

                // 限制最大下落速度，避免太快
                if (motion.y < -1.0) {
                    motion = new Vec3(motion.x, -1.0, motion.z);
                }

                this.setDeltaMovement(motion);
                this.move(MoverType.SELF, this.getDeltaMovement());

                // 增加进度条
                int currentProgress = getProgress();
                if (currentProgress < MAX_PROGRESS) {
                    setProgress(currentProgress + 1); // 每次增加进度
                }
                // 如果在地狱，实体和骑乘的玩家都着火
                if (this.level().dimension() == Level.NETHER) {
                    // 给骑乘的玩家也设置着火效果
                    if (player != null && !player.isOnFire()) {
                        this.setSecondsOnFire(1); // 设置实体着火，持续 5 秒
                        player.setSecondsOnFire(1); // 设置玩家着火，持续 5 秒
                    }
                }

            } else {
                // 没有剩余时间，停止移动
                this.setDeltaMovement(Vec3.ZERO);
            }
        } else {
            super.travel(travelVector); // 默认移动逻辑

            // 未骑乘时减少进度条
            int currentProgress = getProgress();
            if (currentProgress > 0) {
                setProgress(currentProgress - 1); // 每次减少进度
            }
        }

        // 检查进度条是否满了
        if (getProgress() >= MAX_PROGRESS) {
            triggerExplosion();
            setProgress(0); // 重置进度条
        }
        // 新增：检查进度条是否到达 80%，如果是则开始着火
        if (getProgress() >= MAX_PROGRESS * 0.93) { // 80%进度
            if (!this.isOnFire()) {
                this.setSecondsOnFire(1); // 设置实体着火，持续 5 秒
            }

        }
    }


    private void triggerExplosion() {
        if (!this.level().isClientSide) {
            // 生成爆炸
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3.0F, false, Level.ExplosionInteraction.NONE);
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
        ItemStack itemStack = player.getItemInHand(hand);

        if (!this.level().isClientSide) {
            // 喂食逻辑
            if (itemStack.getItem() == Items.END_ROD) {
                if (getRemainingMoveTime() < MAX_FEED_TIME) {
                    // 增加移动时间
                    int increment = Math.min(FEED_INCREMENT, MAX_FEED_TIME - getRemainingMoveTime());
                    setRemainingMoveTime(getRemainingMoveTime() + increment);

                    // 消耗物品
                    if (!player.getAbilities().instabuild) { // 非创造模式消耗物品
                        itemStack.shrink(1);
                    }

                    // 提示玩家剩余时间
                    player.displayClientMessage(
                            Component.literal("喂食成功！剩余运行时间: ")
                                    .append(String.valueOf(getRemainingMoveTime() / 20))
                                    .append(" 秒"),
                            true
                    );
                } else {
                    player.displayClientMessage(
                            Component.literal("运行时间已达上限！"),
                            true
                    );
                }
                return InteractionResult.SUCCESS;
            }

            // 玩家尝试骑乘
            if (!player.isPassenger()) {
                player.startRiding(this); // 玩家开始骑乘
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos pos) {
        // 禁用坠落伤害
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.END_ROD; // 仅末地杆可以喂食
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null; // 不支持繁殖逻辑
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 1)
                .add(Attributes.FLYING_SPEED, 1)
                .add(Attributes.ARMOR, 20.0);
    }
}

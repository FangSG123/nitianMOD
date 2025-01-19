package com.ntsw.entity;

import com.google.common.collect.ImmutableList;
import com.ntsw.ModItems;
import com.ntsw.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class ETHEntity extends Monster implements PowerableMob, RangedAttackMob {

    private static final EntityDataAccessor<Integer> DATA_TARGET_A = SynchedEntityData.defineId(ETHEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_B = SynchedEntityData.defineId(ETHEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_C = SynchedEntityData.defineId(ETHEntity.class, EntityDataSerializers.INT);
    private static final List<EntityDataAccessor<Integer>> DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);
    private static final EntityDataAccessor<Integer> DATA_ID_INV = SynchedEntityData.defineId(ETHEntity.class, EntityDataSerializers.INT);

    private static final int INVULNERABLE_TICKS = 220;

    private final float[] xRotHeads = new float[2];
    private final float[] yRotHeads = new float[2];
    private final float[] xRotOHeads = new float[2];
    private final float[] yRotOHeads = new float[2];

    private final int[] nextHeadUpdate = new int[2];
    private final int[] idleHeadUpdates = new int[2];

    private int destroyBlocksTick;

    private final ServerBossEvent bossEvent;

    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (entity) -> entity.getMobType() != MobType.UNDEAD && entity.attackable();

    private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0).selector(LIVING_ENTITY_SELECTOR);

    public ETHEntity(EntityType<? extends ETHEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.bossEvent = new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.PROGRESS);
        this.bossEvent.setDarkenScreen(true);
        this.setHealth(this.getMaxHealth());
        this.xpReward = 2333;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TARGET_A, 0);
        this.entityData.define(DATA_TARGET_B, 0);
        this.entityData.define(DATA_TARGET_C, 0);
        this.entityData.define(DATA_ID_INV, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Invul", this.getInvulnerableTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setInvulnerableTicks(tag.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        // 检查玩家是否在8格范围内
        if (this.distanceTo(player) <= 8.0) {
            super.startSeenByPlayer(player);
            this.bossEvent.addPlayer(player);
        }
    }
    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new WitherDoNothingGoal());
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    @Override
    public void aiStep() {

        super.aiStep();







        // Fire particle effects instead of wither particles
        for (int i = 0; i < 3; ++i) {
            double headX = this.getHeadX(i);
            double headY = this.getHeadY(i);
            double headZ = this.getHeadZ(i);
            this.level().addParticle(ParticleTypes.FLAME, headX + this.random.nextGaussian() * 0.3, headY + this.random.nextGaussian() * 0.3, headZ + this.random.nextGaussian() * 0.3, 0.0, 0.0, 0.0);
        }

        if (this.getInvulnerableTicks() > 0) {
            for (int i = 0; i < 3; ++i) {
                this.level().addParticle(ParticleTypes.FLAME, this.getX() + this.random.nextGaussian(), this.getY() + this.random.nextFloat() * 3.3F, this.getZ() + this.random.nextGaussian(), 0.7, 0.7, 0.9);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        if (this.getInvulnerableTicks() > 0) {
            int invulTicks = this.getInvulnerableTicks() - 1;
            this.bossEvent.setProgress(1.0F - (float) invulTicks / INVULNERABLE_TICKS);
            if (invulTicks <= 0) {
                this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, Level.ExplosionInteraction.MOB);
                if (!this.isSilent()) {
                    this.level().globalLevelEvent(1023, this.blockPosition(), 0);
                }
            }
            this.setInvulnerableTicks(invulTicks);
            if (this.tickCount % 10 == 0) {
                this.heal(10.0F);
            }
        } else {
            super.customServerAiStep();

            for (int j = 1; j < 3; ++j) {
                if (this.tickCount >= this.nextHeadUpdate[j - 1]) {
                    this.nextHeadUpdate[j - 1] = this.tickCount + 10 + this.random.nextInt(10);
                    if (this.level().getDifficulty() == Difficulty.NORMAL || this.level().getDifficulty() == Difficulty.HARD) {
                        int index = j - 1;
                        this.idleHeadUpdates[index]++;
                        if (this.idleHeadUpdates[index] > 15) {
                            float spread = 10.0F;
                            float verticalSpread = 5.0F;
                            double targetX = Mth.nextDouble(this.random, this.getX() - spread, this.getX() + spread);
                            double targetY = Mth.nextDouble(this.random, this.getY() - verticalSpread, this.getY() + verticalSpread);
                            double targetZ = Mth.nextDouble(this.random, this.getZ() - spread, this.getZ() + spread);
                            this.performRangedAttack(j + 1, targetX, targetY, targetZ, true);
                            this.idleHeadUpdates[index] = 0;
                        }
                    }

                    int targetId = this.getAlternativeTarget(j);
                    if (targetId > 0) {
                        LivingEntity targetEntity = (LivingEntity) this.level().getEntity(targetId);
                        if (targetEntity != null && this.canAttack(targetEntity) && !(this.distanceToSqr(targetEntity) > 900.0) && this.hasLineOfSight(targetEntity)) {
                            this.performRangedAttack(j + 1, targetEntity);
                            this.nextHeadUpdate[j - 1] = this.tickCount + 10 + this.random.nextInt(5);
                            this.idleHeadUpdates[j - 1] = 0;
                        } else {
                            this.setAlternativeTarget(j, 0);
                        }
                    } else {
                        List<LivingEntity> entities = this.level().getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0, 8.0, 20.0));
                        if (!entities.isEmpty()) {
                            LivingEntity randomEntity = entities.get(this.random.nextInt(entities.size()));
                            this.setAlternativeTarget(j, randomEntity.getId());
                        }
                    }
                }
            }

            if (this.getTarget() != null) {
                this.setAlternativeTarget(0, this.getTarget().getId());
            } else {
                this.setAlternativeTarget(0, 0);
            }

            if (this.destroyBlocksTick > 0) {
                --this.destroyBlocksTick;
                if (this.destroyBlocksTick == 0 && ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                    int y = Mth.floor(this.getY());
                    int x = Mth.floor(this.getX());
                    int z = Mth.floor(this.getZ());
                    boolean destroyed = false;

                    for (int j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            for (int l = 0; l <= 3; ++l) {
                                BlockPos blockPos = new BlockPos(x + j, y + l, z + k);
                                BlockState blockState = this.level().getBlockState(blockPos);
                                if (canDestroy(blockState) && ForgeEventFactory.onEntityDestroyBlock(this, blockPos, blockState)) {
                                    destroyed |= this.level().destroyBlock(blockPos, true, this);
                                }
                            }
                        }
                    }

                    if (destroyed) {
                        this.level().levelEvent(null, 1022, this.blockPosition(), 0);
                    }
                }
            }

            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.performRangedAttack(0, target);
    }

    private void performRangedAttack(int headIndex, LivingEntity target) {
        this.performRangedAttack(headIndex, target.getX(), target.getEyeY() - 0.1, target.getZ(), headIndex == 0 && this.random.nextFloat() < 0.001F);
    }

    // Change to fire small fireballs instead of Wither Skulls
    private void performRangedAttack(int headIndex, double x, double y, double z, boolean dangerous) {
        if (!this.isSilent()) {
            //this.playSound(ModSounds.NAILONG_AMBIENT2.get(), 1.0F, 1.0F); //攻击音效
        }

        double headX = this.getHeadX(headIndex);
        double headY = this.getHeadY(headIndex);
        double headZ = this.getHeadZ(headIndex);
        double dx = x - headX;
        double dy = y - headY;
        double dz = z - headZ;

        SmallFireball fireball = new SmallFireball(this.level(), this, dx, dy, dz);
        fireball.setPosRaw(headX, headY, headZ);
        this.level().addFreshEntity(fireball);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!source.is(DamageTypeTags.WITHER_IMMUNE_TO) && !(source.getEntity() instanceof ETHEntity)) {
            if (this.getInvulnerableTicks() > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            } else {
                Entity attacker = source.getEntity();
                if (this.isPowered()) {
                    if (attacker instanceof SmallFireball) {
                        return false;
                    }
                }

                if (attacker != null && !(attacker instanceof Player) && attacker instanceof LivingEntity && ((LivingEntity) attacker).getMobType() == this.getMobType()) {
                    return false;
                } else {
                    if (this.destroyBlocksTick <= 0) {
                        this.destroyBlocksTick = 20;
                    }

                    for (int i = 0; i < this.idleHeadUpdates.length; ++i) {
                        this.idleHeadUpdates[i] += 3;
                    }

                    return super.hurt(source, amount);
                }
            }
        } else {
            return false;
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        for(int i = 0;i <= 14 ; i++)
        {
            double offsetX = this.random.nextDouble() * 4 - 1; // [-1, 1] 范围内
            double offsetY = 0; // 让虞美人生成在地面上
            double offsetZ = this.random.nextDouble() * 4 - 1; // [-1, 1] 范围内
            ItemStack itemStack = new ItemStack(Items.POPPY);
            itemStack.setHoverName(Component.literal("血玫瑰"));  // 设置自定义名字
            this.spawnAtLocation(itemStack);
            this.level().addFreshEntity(new ItemEntity(this.level(), this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, itemStack));
        }
    }

    @Override
    public boolean isPowered() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(effect);
    }

    @Override
    public boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
        return false;
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motion) {
        // No-op to prevent being stuck in blocks
    }

    private double getHeadX(int headIndex) {
        if (headIndex <= 0) {
            return this.getX();
        } else {
            float rotation = (this.yBodyRot + (180 * (headIndex - 1))) * 0.017453292F;
            return this.getX() + Mth.cos(rotation) * 1.3;
        }
    }

    private double getHeadY(int headIndex) {
        return headIndex <= 0 ? this.getY() + 3.0 : this.getY() + 2.2;
    }

    private double getHeadZ(int headIndex) {
        if (headIndex <= 0) {
            return this.getZ();
        } else {
            float rotation = (this.yBodyRot + (180 * (headIndex - 1))) * 0.017453292F;
            return this.getZ() + Mth.sin(rotation) * 1.3;
        }
    }

    private float rotlerp(float current, float target, float maxChange) {
        float delta = Mth.wrapDegrees(target - current);
        if (delta > maxChange) {
            delta = maxChange;
        }
        if (delta < -maxChange) {
            delta = -maxChange;
        }
        return current + delta;
    }

    public static boolean canDestroy(BlockState state) {
        return !state.isAir() && !state.is(BlockTags.WITHER_IMMUNE);
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(INVULNERABLE_TICKS);
        this.bossEvent.setProgress(0.0F);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public int getInvulnerableTicks() {
        return this.entityData.get(DATA_ID_INV);
    }

    public void setInvulnerableTicks(int ticks) {
        this.entityData.set(DATA_ID_INV, ticks);
    }

    public int getAlternativeTarget(int index) {
        return this.entityData.get(DATA_TARGETS.get(index));
    }

    public void setAlternativeTarget(int index, int targetId) {
        this.entityData.set(DATA_TARGETS.get(index), targetId);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.bossEvent.removeAllPlayers();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.DROWN) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ARMOR, 3.0);
    }

    class WitherDoNothingGoal extends Goal {
        public WitherDoNothingGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return ETHEntity.this.getInvulnerableTicks() > 0;
        }
    }
} 
package com.nailong.nailong;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class NaiLongEntity extends Monster implements PowerableMob, RangedAttackMob {

    // Data Accessors for targets and invulnerability
    private static final EntityDataAccessor<Integer> DATA_TARGET_A = SynchedEntityData.defineId(NaiLongEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_B = SynchedEntityData.defineId(NaiLongEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_C = SynchedEntityData.defineId(NaiLongEntity.class, EntityDataSerializers.INT);
    private static final List<EntityDataAccessor<Integer>> DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);
    private static final EntityDataAccessor<Integer> DATA_ID_INV = SynchedEntityData.defineId(NaiLongEntity.class, EntityDataSerializers.INT);

    private static final int INVULNERABLE_TICKS = 220;

    // Head rotation arrays
    private final float[] xRotHeads = new float[2];
    private final float[] yRotHeads = new float[2];
    private final float[] xRotOHeads = new float[2];
    private final float[] yRotOHeads = new float[2];

    // Head update timers
    private final int[] nextHeadUpdate = new int[2];
    private final int[] idleHeadUpdates = new int[2];

    // Block destruction timer
    private int destroyBlocksTick;

    // Boss event for the boss bar
    private final ServerBossEvent bossEvent;

    // Selector for valid living entities (excluding undead)
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (entity) ->
            entity.getMobType() != MobType.UNDEAD && entity.attackable();

    // Targeting conditions similar to the vanilla Wither
    private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat()
            .range(20.0)
            .selector(LIVING_ENTITY_SELECTOR);

    public NaiLongEntity(EntityType<? extends NaiLongEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.bossEvent = new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.PROGRESS);
        this.bossEvent.setDarkenScreen(true);
        this.setHealth(this.getMaxHealth());
        this.xpReward = 50;
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
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void registerGoals() {
        // Goals similar to vanilla Wither
        this.goalSelector.addGoal(0, new WitherDoNothingGoal());
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Targeting players and other living entities
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    @Override
    public void aiStep() {
        Vec3 movement = this.getDeltaMovement().multiply(1.0, 0.6, 1.0);
        if (!this.level().isClientSide && this.getAlternativeTarget(0) > 0) {
            Entity targetEntity = this.level().getEntity(this.getAlternativeTarget(0));
            if (targetEntity != null) {
                double yMotion = movement.y;
                if (this.getY() < targetEntity.getY() || (!this.isPowered() && this.getY() < targetEntity.getY() + 5.0)) {
                    yMotion = Math.max(0.0, yMotion) + 0.3 - yMotion * 0.6;
                }
                movement = new Vec3(movement.x, yMotion, movement.z);

                Vec3 horizontalDirection = new Vec3(targetEntity.getX() - this.getX(), 0.0, targetEntity.getZ() - this.getZ());
                if (horizontalDirection.horizontalDistanceSqr() > 9.0) {
                    Vec3 normalized = horizontalDirection.normalize();
                    movement = movement.add(normalized.x * 0.3 - movement.x * 0.6, 0.0, normalized.z * 0.3 - movement.z * 0.6);
                }
            }
        }

        this.setDeltaMovement(movement);
        if (movement.horizontalDistanceSqr() > 0.05) {
            this.setYRot((float) Mth.atan2(movement.z, movement.x) * 57.295776F - 90.0F);
        }

        super.aiStep();

        // Update head rotations
        for (int i = 0; i < 2; ++i) {
            this.yRotOHeads[i] = this.yRotHeads[i];
            this.xRotOHeads[i] = this.xRotHeads[i];
        }

        for (int j = 0; j < 2; ++j) {
            int targetId = this.getAlternativeTarget(j + 1);
            Entity targetEntity = null;
            if (targetId > 0) {
                targetEntity = this.level().getEntity(targetId);
            }

            if (targetEntity != null) {
                double headX = this.getHeadX(j + 1);
                double headY = this.getHeadY(j + 1);
                double headZ = this.getHeadZ(j + 1);
                double dx = targetEntity.getX() - headX;
                double dy = targetEntity.getEyeY() - headY;
                double dz = targetEntity.getZ() - headZ;
                double horizontalDist = Math.sqrt(dx * dx + dz * dz);
                float targetYaw = (float) (Mth.atan2(dz, dx) * 57.2957763671875) - 90.0F;
                float targetPitch = (float) (-(Mth.atan2(dy, horizontalDist) * 57.2957763671875));
                this.xRotHeads[j] = this.rotlerp(this.xRotHeads[j], targetPitch, 40.0F);
                this.yRotHeads[j] = this.rotlerp(this.yRotHeads[j], targetYaw, 10.0F);
            } else {
                this.yRotHeads[j] = this.rotlerp(this.yRotHeads[j], this.yBodyRot, 10.0F);
            }
        }

        boolean isPowered = this.isPowered();

        // Particle effects for heads
        for (int i = 0; i < 3; ++i) {
            double headX = this.getHeadX(i);
            double headY = this.getHeadY(i);
            double headZ = this.getHeadZ(i);
            this.level().addParticle(ParticleTypes.SMOKE, headX + this.random.nextGaussian() * 0.3, headY + this.random.nextGaussian() * 0.3, headZ + this.random.nextGaussian() * 0.3, 0.0, 0.0, 0.0);
            if (isPowered && this.level().random.nextInt(4) == 0) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT, headX + this.random.nextGaussian() * 0.3, headY + this.random.nextGaussian() * 0.3, headZ + this.random.nextGaussian() * 0.3, 0.7, 0.7, 0.5);
            }
        }

        // Additional particle effects during invulnerability
        if (this.getInvulnerableTicks() > 0) {
            for (int i = 0; i < 3; ++i) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + this.random.nextFloat() * 3.3F, this.getZ() + this.random.nextGaussian(), 0.7, 0.7, 0.9);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        if (this.getInvulnerableTicks() > 0) {
            // Handle invulnerability phase
            int invulTicks = this.getInvulnerableTicks() - 1;
            this.bossEvent.setProgress(1.0F - (float) invulTicks / INVULNERABLE_TICKS);
            if (invulTicks <= 0) {
                // Explosion when invulnerability ends
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
            // Normal AI steps
            super.customServerAiStep();

            for (int j = 1; j < 3; ++j) {
                if (this.tickCount >= this.nextHeadUpdate[j - 1]) {
                    this.nextHeadUpdate[j - 1] = this.tickCount + 10 + this.random.nextInt(10);
                    if (this.level().getDifficulty() == Difficulty.NORMAL || this.level().getDifficulty() == Difficulty.HARD) {
                        int index = j - 1;
                        this.idleHeadUpdates[index]++;
                        if (this.idleHeadUpdates[index] > 15) {
                            // Perform ranged attack
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
                            this.nextHeadUpdate[j - 1] = this.tickCount + 40 + this.random.nextInt(20);
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

            // Set primary target
            if (this.getTarget() != null) {
                this.setAlternativeTarget(0, this.getTarget().getId());
            } else {
                this.setAlternativeTarget(0, 0);
            }

            // Handle block destruction
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

            // Heal periodically
            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            // Update boss bar progress
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.performRangedAttack(0, target);
    }

    // Overloaded method to handle specific head attacks
    private void performRangedAttack(int headIndex, LivingEntity target) {
        this.performRangedAttack(headIndex, target.getX(), target.getEyeY() - 0.1, target.getZ(), headIndex == 0 && this.random.nextFloat() < 0.001F);
    }

    // Method to spawn and fire WitherSkulls
    private void performRangedAttack(int headIndex, double x, double y, double z, boolean dangerous) {
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1024, this.blockPosition(), 0);
        }

        double headX = this.getHeadX(headIndex);
        double headY = this.getHeadY(headIndex);
        double headZ = this.getHeadZ(headIndex);
        double dx = x - headX;
        double dy = y - headY;
        double dz = z - headZ;

        WitherSkull skull = new WitherSkull(this.level(), this, dx, dy, dz);
        skull.setOwner(this);
        if (dangerous) {
            skull.setDangerous(true);
        }
        skull.setPosRaw(headX, headY, headZ);
        this.level().addFreshEntity(skull);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!source.is(DamageTypeTags.WITHER_IMMUNE_TO) && !(source.getEntity() instanceof NaiLongEntity)) {
            if (this.getInvulnerableTicks() > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            } else {
                Entity attacker = source.getEntity();
                if (this.isPowered()) {
                    if (attacker instanceof WitherSkull) {
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
        this.spawnAtLocation(net.minecraft.world.item.Items.NETHER_STAR).setExtendedLifetime();
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

    // Helper methods for head positions
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

    // Linear interpolation for rotation
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

    // Static method to determine if a block can be destroyed
    public static boolean canDestroy(BlockState state) {
        return !state.isAir() && !state.is(BlockTags.WITHER_IMMUNE);
    }

    // Make the entity invulnerable for a certain period
    public void makeInvulnerable() {
        this.setInvulnerableTicks(INVULNERABLE_TICKS);
        this.bossEvent.setProgress(0.0F);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    // Getters and setters for invulnerable ticks and alternative targets
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

    // Boss bar progress update
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    // Synchronize boss bar on entity death
    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.bossEvent.removeAllPlayers();
    }

    // Make entity immune to drowning
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.DROWN) || super.isInvulnerableTo(source);
    }

    // Ensure the entity has no gravity
    @Override
    public boolean isNoGravity() {
        return true;
    }

    // Define entity attributes similar to the vanilla Wither
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ARMOR, 4.0);
    }

    // Inner class to handle the Wither's invulnerability goal
    class WitherDoNothingGoal extends Goal {
        public WitherDoNothingGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return NaiLongEntity.this.getInvulnerableTicks() > 0;
        }
    }
}

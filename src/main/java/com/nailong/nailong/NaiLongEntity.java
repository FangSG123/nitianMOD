package com.nailong.nailong;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.bossevents.CustomBossEvent;

import java.util.EnumSet;

public class NaiLongEntity extends Monster implements RangedAttackMob {

    private final CustomBossEvent bossInfo;

    public NaiLongEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.bossInfo = new CustomBossEvent(new ResourceLocation("nailong", "nai_long_entity"), this.getDisplayName());
        this.bossInfo.setColor(CustomBossEvent.BossBarColor.PURPLE);
        this.bossInfo.setOverlay(CustomBossEvent.BossBarOverlay.PROGRESS);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public ClientboundAddEntityPacket getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.FOLLOW_RANGE, 50.0)
                .add(Attributes.FLYING_SPEED, 0.6); // 添加飞行速度属性
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new HoverGoal(this));
        this.goalSelector.addGoal(4, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!this.level().isClientSide) {
            WitherSkull skull = new WitherSkull(this.level(), this, target.getX() - this.getX(), target.getEyeY() - this.getEyeY(), target.getZ() - this.getZ());
            skull.setPos(this.getX(), this.getEyeY() + 0.5D, this.getZ());
            this.level().addFreshEntity(skull);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.bossInfo.removeAllPlayers();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.DROWN) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isNoGravity() {
        return true; // 确保该实体无重力
    }

    private static class HoverGoal extends Goal {
        private final Monster mob;
        private long lastMoveTime = 0;

        public HoverGoal(Monster mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            // 每隔一定时间允许重新触发移动
            return System.currentTimeMillis() - lastMoveTime > 1500;
        }

        @Override
        public boolean canContinueToUse() {
            // 持续执行，直到目标地点到达
            return !this.mob.getNavigation().isDone();
        }

        @Override
        public void start() {
            int x = this.mob.getRandom().nextInt(10) - 5;
            int z = this.mob.getRandom().nextInt(10) - 5;
            int y = this.mob.getRandom().nextInt(6) - 3; // 增加y轴移动范围
            this.mob.getNavigation().moveTo(this.mob.getX() + x, this.mob.getY() + y, this.mob.getZ() + z, 1.0);
            lastMoveTime = System.currentTimeMillis(); // 更新移动时间
        }

        @Override
        public void tick() {
            if (this.mob.getNavigation().isDone()) {
                this.start();
            }
        }
    }
}

package com.ntsw.entity;

import com.ntsw.item.PddItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ChuanJianGuoEntity extends PathfinderMob {
    private static final double BASE_REGEN_RATE = 0.1;  // 每 tick 恢复的基础生命值
    private static final double BASE_DAMAGE = 5;
    private static final double VILLAGER_DAMAGE_BONUS = 2.0;  // 每个村民增加的伤害
    private static final double VILLAGER_REGEN_BONUS = 0.1;   // 每个村民增加的回血速度
    private static final int PDD_HIT_THRESHOLD = 27;
    public boolean isTransformed = false; // 是否已经变异
    private boolean isHurtCooldown = false; // 是否在当前tick中已处理过hurt

    private int pddHitCount = 0;
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            Component.literal("米国总统"), // Boss名字
            BossEvent.BossBarColor.RED,   // Boss血条颜色
            BossEvent.BossBarOverlay.PROGRESS // 血条样式
    ).setCreateWorldFog(true);

    public ChuanJianGuoEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        // 添加铁傀儡的类似AI行为
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // 攻击任何伤害村民的生物
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                entity -> entity instanceof Player && hasAttackedVillager((Player) entity)));
    }

    @Override
    public void tick() {
        super.tick();
        isHurtCooldown = false;

        // 降低计算频率，每隔20 tick 执行一次
        if (this.tickCount % 20 == 0) {
            if (this.getHealth() < this.getMaxHealth()) {
                double regenAmount = calculateRegenRate();
                this.heal((float) regenAmount);
            }

            if (!isTransformed) {
                double addDamage = calculateEnhancedDamage();
                Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(addDamage + BASE_DAMAGE);
            }
        }

        if (isTransformed) {
            updateBoundingBox();
        }

        bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    private void updateBoundingBox() {
        if (!this.isTransformed) return; // 只有变异后才更新碰撞箱

        AABB currentBox = this.getBoundingBox();
        AABB newBox = new AABB(
                currentBox.minX - currentBox.getXsize(),
                currentBox.minY,
                currentBox.minZ - currentBox.getZsize(),
                currentBox.maxX + currentBox.getXsize(),
                currentBox.maxY + currentBox.getYsize() * 2,
                currentBox.maxZ + currentBox.getZsize()
        );
        this.setBoundingBox(newBox);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (isHurtCooldown) {
            return false; // 如果已在当前tick中处理过hurt，直接返回
        }
        isHurtCooldown = true;

        if (source.getDirectEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            if (weapon.getItem() instanceof PddItem) {
                pddHitCount++;
                if (pddHitCount >= PDD_HIT_THRESHOLD && !isTransformed) {
                    transformToMeiGuoZongTong();
                }
            }
        }

        // 减少雷击效果生成频率，每隔100个tick生成一次
        if (this.tickCount % 100 == 0) {
            Entity attacker = source.getDirectEntity();
            if (attacker != null ) {
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level());
                if (lightning != null) {
                    lightning.moveTo(attacker.getX(), attacker.getY(), attacker.getZ());
                    level().addFreshEntity(lightning);
                }
            }
        }

        boolean hurtResult = super.hurt(source, amount);
        if (hurtResult) {
            bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }

        return hurtResult;
    }

    private boolean hasAttackedVillager(Player player) {
        return player.getLastHurtMob() instanceof Villager;
    }

    private double calculateEnhancedDamage() {
        int nearbyVillagerCount = countNearbyVillagers();
        return nearbyVillagerCount * VILLAGER_DAMAGE_BONUS;
    }

    private double calculateRegenRate() {
        int nearbyVillagerCount = countNearbyVillagers();
        return BASE_REGEN_RATE + (nearbyVillagerCount * VILLAGER_REGEN_BONUS);
    }

    private int countNearbyVillagers() {
        // 每隔40个tick 执行一次检测
        if (this.tickCount % 40 != 0) {
            return 0;
        }
        List<Villager> villagers = this.level().getEntitiesOfClass(Villager.class, this.getBoundingBox().inflate(5.0D));
        return villagers.size();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 8.0);
    }

    private void transformToMeiGuoZongTong() {
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).getBaseValue() * 2);
        this.setHealth(this.getMaxHealth());
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).getBaseValue() * 2);
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).getBaseValue() * 1.2);

        isTransformed = true;

        this.setCustomName(Component.literal("米国总统"));
        this.setCustomNameVisible(true);

        pddHitCount = 0;
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void knockback(double strength, double xRatio, double zRatio) {
        // 不调用父类的knockback方法，就不会产生击退效果
    }
}

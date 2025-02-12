package com.ntsw.entity;

import com.ntsw.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HeiManBaEntity extends IronGolem {
    public HeiManBaEntity(EntityType<? extends IronGolem> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 添加对敌对生物的攻击行为
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));

        // 设定攻击目标为所有敌对生物（Monster类的实例），而不是玩家或友好实体
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true));

        // 添加看向玩家的行为，显得更友好
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // 添加随机散步行为，使其显得更加自然
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));
    }

    @Override
    public boolean isAlliedTo(net.minecraft.world.entity.Entity entity) {
        // 设置为与玩家为盟友，即不会攻击玩家
        return entity instanceof Player;
    }

    @Override
    public void die(DamageSource cause) {
        // 播放死亡音效
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.HeiManBa_DEATH.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        super.die(cause);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ATTACK_DAMAGE, 8.0);
    }
}

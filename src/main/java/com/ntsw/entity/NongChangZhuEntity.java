package com.ntsw.entity;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class NongChangZhuEntity extends PathfinderMob {

    public NongChangZhuEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        // 设置基础移动速度和其他属性
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0D);

        // 设置手持物品为栓绳
        this.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(Items.LEAD));
    }

    @Override
    protected void registerGoals() {
        // 添加基础的村民行为
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));

        // 添加攻击目标，设为最近的 LaoHei 实体
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LaoHeiEntity.class, true));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.ARMOR, 1.0);
    }
}

package com.ntsw.entity;

import com.ntsw.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class ZiMinEntity extends PathfinderMob {

    private Player targetPlayer;

    public ZiMinEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        ItemStack enchantedPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
        enchantedPickaxe.enchant(Enchantments.SHARPNESS, 1);
        enchantedPickaxe.enchant(Enchantments.UNBREAKING, 3);
        this.setItemSlot(EquipmentSlot.MAINHAND, enchantedPickaxe);
        this.playSpawnSound();
    }

    public void playSpawnSound() {
        this.targetPlayer = this.level().getNearestPlayer(this, 100);
        this.level().playSound(targetPlayer, this.getX(), this.getY(), this.getZ(),
                ModSounds.ZiMin_Summon.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.addGoal(0, new ChasePlayerUntilDeathGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (targetPlayer != null && !targetPlayer.isAlive()) {
            this.discard();
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean success = super.doHurtTarget(target);
        if (success) {
            this.swing(InteractionHand.MAIN_HAND); // 使用默认的 swing 方法
            System.out.println("Swing animation triggered");
        } else {
            System.out.println("Attack failed");
        }
        return success;
    }

    // 移除 swing 方法的重写
    // @Override
    // public void swing(InteractionHand hand, boolean updateSelf) {
    //     super.swing(hand, updateSelf);
    //     if (!this.level().isClientSide) {
    //         int animationDuration = 6;
    //         this.swingTime = animationDuration;
    //         this.swinging = true;
    //     }
    // }

    private static class ChasePlayerUntilDeathGoal extends Goal {
        private final ZiMinEntity mob;
        private Player targetPlayer;

        public ChasePlayerUntilDeathGoal(ZiMinEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            this.targetPlayer = this.mob.level().getNearestPlayer(this.mob, 100);
            if (this.targetPlayer != null && this.targetPlayer.isAlive()) {
                mob.targetPlayer = targetPlayer;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            mob.getNavigation().moveTo(targetPlayer, 1.2);
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
        }

        @Override
        public boolean canContinueToUse() {
            return targetPlayer != null && targetPlayer.isAlive() && mob.distanceTo(targetPlayer) < 100;
        }

        @Override
        public void tick() {
            if (targetPlayer != null && targetPlayer.isAlive()) {
                mob.getNavigation().moveTo(targetPlayer, 1.2);
                mob.lookAt(targetPlayer, 30.0F, 30.0F);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 1.0);
    }
}

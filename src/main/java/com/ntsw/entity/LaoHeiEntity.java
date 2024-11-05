package com.ntsw.entity;

import com.ntsw.MoveToFarmlandGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class LaoHeiEntity extends Villager {
    private static final double INITIAL_SPEED = 0.1D;
    private static final double SPEED_INCREMENT = 0.1D;
    private static final int RESET_TICKS = 2000;
    private int tickCounter = 0;

    public LaoHeiEntity(EntityType<? extends Villager> type, Level world) {
        super(type, world);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(INITIAL_SPEED);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new MoveToFarmlandGoal(this, 1.0D));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 检查攻击来源是否为栓绳
        if (source.getDirectEntity() instanceof Player player) {
            if (player.getMainHandItem().is(Items.LEAD)) {
                System.out.println("加速！");
                double newSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() + SPEED_INCREMENT;
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newSpeed);
                return super.hurt(source, amount);
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        // 计时器增加
        tickCounter++;
        if (tickCounter >= RESET_TICKS) {
            // 每 2000 个 tick 重置速度
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(INITIAL_SPEED);
            tickCounter = 0;
        }
    }
}

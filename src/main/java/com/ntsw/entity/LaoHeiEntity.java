package com.ntsw.entity;

import com.ntsw.MoveToFarmlandGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public class LaoHeiEntity extends Villager {
    private boolean isSpeedBoosted = false;

    public LaoHeiEntity(EntityType<? extends Villager> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 添加靠近耕地并种植作物的自定义目标
        this.goalSelector.addGoal(5, new MoveToFarmlandGoal(this, 1.0D));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).is(Items.LEAD)) {
            // 增加速度和种田速度
            isSpeedBoosted = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);  // 设定为更高的移动速度
            // 可选：增加种田的速度（实现种田速度加成逻辑）
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public boolean isSpeedBoosted() {
        return isSpeedBoosted;
    }
}

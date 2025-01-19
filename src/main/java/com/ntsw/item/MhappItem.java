package com.ntsw.item;

import com.ntsw.ModEffects;
import com.ntsw.goal.FollowPlayerGoal;
import com.ntsw.goal.SlimeDropSlimeballGoal;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.Random;

public class MhappItem extends Item {

    public MhappItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){        // 只有在服务端执行逻辑
            // 判断目标是否已有 FAQING 与 MEILIAO 效果
            boolean hasFaqing = target.hasEffect(ModEffects.FAQING.get());
            boolean hasMeiliao = target.hasEffect(ModEffects.MEILIAO.get());

            if (hasFaqing && hasMeiliao) {
                // 如果已有，则清除效果
                target.removeEffect(ModEffects.FAQING.get());
                target.removeEffect(ModEffects.MEILIAO.get());
                target.getPersistentData().putBoolean("cuiming", false);
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("清除催眠！"),
                        true
                );
            } else {
                // 否则添加无限时长 (此处示例用很大数值模拟无限)
                int duration = 999999999;  // 很长的时间，约 ~ 1157 天
                target.getPersistentData().putBoolean("cuiming", true);
                target.addEffect(new MobEffectInstance(ModEffects.MEILIAO.get(), duration, 0));
                target.addEffect(new MobEffectInstance(ModEffects.FAQING.get(), duration, 0));

                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        ModEffects.MEILIAO.get(),
                        duration, 0,
                        true,
                        true
                ));

                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("催眠！"),
                        true
                );
            }

            if (target instanceof Mob mobTarget) {
                mobTarget.goalSelector.addGoal(1, new FollowPlayerGoal(mobTarget, player, 1D));

            }
            // 如果目标是史莱姆，添加粘液球生成的 Goal
            if (target instanceof Slime slime) {
                slime.goalSelector.addGoal(0, new SlimeDropSlimeballGoal(slime));
            }



        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }


}

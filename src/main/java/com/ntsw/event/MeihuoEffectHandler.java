package com.ntsw.event;


import com.ntsw.ModEffects;
import com.ntsw.ModEnchantments;
import com.ntsw.goal.FollowPlayerGoal;
import com.ntsw.goal.SlimeDropSlimeballGoal;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Method;
import java.util.Random;

@Mod.EventBusSubscriber
public class MeihuoEffectHandler {

    private static final Random random = new Random();

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        Player player = event.getEntity();
        ItemStack weapon = player.getMainHandItem();

        // 检查武器是否带有 "meihuo" 附魔
        int meihuoLevel = weapon.getEnchantmentLevel(ModEnchantments.MEIHUO.get());
        if (meihuoLevel > 0) {
            // 根据附魔等级给目标施加 "meiliao" 药水效果
            int duration = 100 * meihuoLevel; // 效果持续时间，等级越高，时间越长
            target.addEffect(new MobEffectInstance(ModEffects.MEILIAO.get(), duration, 0));

            // 如果目标是 Mob 类型，则添加跟随玩家的目标
            if (target instanceof Mob mobTarget) {
                mobTarget.goalSelector.addGoal(1, new FollowPlayerGoal(mobTarget, player, 1D));
                //System.out.println("添加跟随");
            }
            // 如果目标是史莱姆，添加粘液球生成的 Goal
            if (target instanceof Slime slime) {
                slime.goalSelector.addGoal(0, new SlimeDropSlimeballGoal(slime));
            }

        }

        // 新功能：如果敌对生物带有 "meiliao" 效果，有概率掉落
        if (target instanceof Monster && target.hasEffect(ModEffects.MEILIAO.get())) {
            if ((target instanceof Piglin piglin)) {
                dropGold(piglin);
                target.setHealth(target.getHealth() * 0.9f);
            }
            else if(target instanceof Pillager pillager)
            {
                dropEmerald(target);
                target.setHealth(target.getHealth() * 0.6f);
            }
            else {
                float dropChance = 0.4f + (meihuoLevel * 0.05f);
                if (random.nextFloat() < dropChance) {
                    dropLoot(target);
                }
            }
        }

        // 新增功能：如果目标是村民且带有 "meiliao" 效果，每次攻击掉落一个绿宝石
        if (target instanceof Villager && target.hasEffect(ModEffects.MEILIAO.get())) {
            dropEmerald(target);
            target.setHealth(target.getHealth() * 0.6f);
        }
    }

    private static void dropLoot(LivingEntity entity) {
        if (entity.level() != null && !entity.level().isClientSide) {
            // 定义掉落的经验数量，这里以随机数为例
            int experience = 3 + random.nextInt(6); // 生成5到10个经验
            ExperienceOrb orb = new ExperienceOrb(entity.level(), entity.getX(), entity.getY(), entity.getZ(), experience);
            entity.level().addFreshEntity(orb);
            //System.out.println("Dropped " + experience + " experience orb(s) for " + entity.getName().getString());
        }
    }

    // 村民掉落绿宝石方法
    private static void dropEmerald(LivingEntity entity) {
        if (entity.level() != null) {
            // 掉落一个绿宝石
            ItemStack emerald = new ItemStack(Items.EMERALD);
            entity.spawnAtLocation(emerald);
        }
    }
    private static void dropGold(LivingEntity entity) {
        if (entity.level() != null) {
            // 掉落一个绿宝石
            ItemStack gold = new ItemStack(Items.GOLD_NUGGET);
            entity.spawnAtLocation(gold);
        }
    }
}

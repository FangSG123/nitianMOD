package com.ntsw.event;


import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SuoArrowHandler {

    @SubscribeEvent
    public static void onProjectileLaunch(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;

        // 检查箭矢是否由带有suo附魔的弓发射
        if (arrow.getOwner() instanceof LivingEntity shooter) {
            ItemStack bow = shooter.getOffhandItem();
            if (bow.getItem() instanceof BowItem && bow.getEnchantmentLevel(ModEnchantments.SUO.get()) > 0) {
                // 添加自定义追踪逻辑
                arrow.addTag("suo_tracking");
            }
        }
    }

    @SubscribeEvent
    public static void onArrowUpdate(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide()) return;

        ServerLevel level = (ServerLevel) event.level;
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof AbstractArrow arrow && arrow.getTags().contains("suo_tracking") && arrow.getOwner() instanceof LivingEntity shooter) {
                // 寻找符合条件的最近生物
                double range = 20.0D;
                AABB searchBox = arrow.getBoundingBox().inflate(range);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != shooter);

                LivingEntity nearest = null;
                double closestDistance = Double.MAX_VALUE;
                for (LivingEntity target : targets) {
                    // 过滤Y值低于玩家两格以上的生物
                    if (target.getY() < shooter.getY() - 2) continue;

                    double distance = arrow.distanceToSqr(target);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        nearest = target;
                    }
                }

                if (nearest != null) {
                    // 计算实时方向
                    double dx = nearest.getX() - arrow.getX();
                    double dy = nearest.getY() + (nearest.getBbHeight() / 2) - arrow.getY(); // 追踪生物中心
                    double dz = nearest.getZ() - arrow.getZ();
                    double magnitude = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (magnitude > 0.0D) {
                        dx /= magnitude;
                        dy /= magnitude;
                        dz /= magnitude;
                        double speed = 1D; // 调整追踪速度
                        arrow.setDeltaMovement(dx * speed, dy * speed, dz * speed);
                        arrow.hasImpulse = true; // 确保箭矢能调整速度
                    }
                }
            }
        }
    }
}

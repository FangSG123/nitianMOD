package com.ntsw.event;


import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TridentLockOnHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        // 检查是否是三叉戟
        if (!(entity instanceof ThrownTrident trident)) {
            return;
        }

        // 检查三叉戟的所有者是否是玩家
        if (!(trident.getOwner() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        int jianrenpianzuoLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.JIANRENPIANZUO.get(), player);

        if (jianrenpianzuoLevel > 0) {
            // 计算玩家左前方的位置
            double offset = 2.0 * jianrenpianzuoLevel;
            double yaw = Math.toRadians(player.getYRot());
            double xOffset = -offset * Math.sin(yaw);
            double zOffset = offset * Math.cos(yaw);

            double targetX = player.getX() + xOffset;
            double targetZ = player.getZ() + zOffset;

            // 获取玩家的视线方向向量（水平分量）
            Vec3 lookVec = player.getLookAngle().normalize();
            Vec3 leftVec = new Vec3(-lookVec.z, 0, lookVec.x); // 左方向向量

            LivingEntity closestEntity = null;
            double closestDistanceSq = Double.MAX_VALUE;

            for (Entity e : player.level().getEntities(player, player.getBoundingBox().inflate(50.0))) { // 搜索10格范围内
                if (e instanceof LivingEntity living && living != player) {
                    // 计算生物到玩家的向量
                    Vec3 toEntity = new Vec3(living.getX() - player.getX(), 0, living.getZ() - player.getZ()).normalize();

                    // 计算生物在玩家视线方向的点积（cosine of angle）
                    double angleCosine = lookVec.dot(toEntity);

                    // 计算生物在玩家左侧的方向（叉积的 Y 分量）
                    double cross = lookVec.x * toEntity.z - lookVec.z * toEntity.x;

                    // 定义前方 45 度的范围，并且在左侧
                    if (angleCosine > Math.cos(Math.toRadians(45)) && cross < 0) {
                        // 计算生物身体中心的 Y 坐标
                        double targetY = living.getY() + living.getBbHeight() / 2.0;
                        double distanceSq = living.distanceToSqr(targetX, targetY, targetZ);
                        if (distanceSq < closestDistanceSq) {
                            closestDistanceSq = distanceSq;
                            closestEntity = living;
                        }
                    }
                }
            }

            if (closestEntity != null) {
                // 计算方向向量，指向生物的身体中心
                Vec3 targetPos = new Vec3(
                        closestEntity.getX(),
                        (closestEntity.getY() + closestEntity.getBbHeight() / 2.0)+0.5F,
                        closestEntity.getZ()
                );
                Vec3 direction = targetPos.subtract(trident.position()).normalize();
                double speed = 1.0 + 0.5 * jianrenpianzuoLevel; // 根据附魔等级增加速度

                // 设置三叉戟的速度向量，使其朝向目标生物飞行
                trident.setDeltaMovement(direction.scale(speed));
                trident.setNoGravity(false); // 确保重力作用
                System.out.println("Trident locked on to: " + closestEntity);
            }
        }
    }
}

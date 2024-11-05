// KillCountEventHandler.java
package com.ntsw;
import com.ntsw.entity.ZiMinEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class KillCountEventHandler {

    private static final Map<Player, Integer> killCounts = new HashMap<>();

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player && event.getEntity() instanceof Animal) {
            killCounts.put(player, killCounts.getOrDefault(player, 0) + 1);
            int kills = killCounts.get(player);

            // 提示玩家
            if (kills == 3) {
                player.sendSystemMessage(Component.literal("你感觉被某人盯上了"));
            } else if (kills == 4) {
                player.sendSystemMessage(Component.literal("你感觉你不该继续破坏大籽然"));
            } else if (kills == 5) {
                player.sendSystemMessage(Component.literal("真的还要继续吗?"));
            } else if (kills == 6) {
                player.sendSystemMessage(Component.literal("这样做真好吗"));
            } else if (kills == 7) {
                player.sendSystemMessage(Component.literal("住手吧.... "));
            } else if (kills >= 8) {
                player.sendSystemMessage(Component.literal("你已经没救了"));
                spawnCustomMob(player);
                killCounts.put(player, 0); // 重置计数
            }
        }
    }

    private static void spawnCustomMob(Player player) {
        Level world = player.level();
        if (!world.isClientSide) {
            // 获取玩家的朝向角度（弧度表示）
            float yaw = player.getYRot();

            // 将角度转换为玩家后方的相对坐标（1.0F 代表在1格距离后方生成）
            double offsetX = -Math.sin(Math.toRadians(yaw)) * 1.0F;
            double offsetZ = Math.cos(Math.toRadians(yaw)) * 1.0F;

            // 创建 ZiMinEntity 实例
            ZiMinEntity ziminEntity = new ZiMinEntity(ModEntitys.ZiMin_Entity.get(), world);
            // 设置实体生成的位置为玩家背后的位置
            ziminEntity.setPos(player.getX() + offsetX, player.getY(), player.getZ() + offsetZ);

            // 添加实体到世界
            world.addFreshEntity(ziminEntity);
        }
    }
}

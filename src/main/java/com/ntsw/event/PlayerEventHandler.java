package com.ntsw.event;

import com.ntsw.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerEventHandler {
    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent event) {
        // 检查是否是玩家受伤
        if (event.getEntity() instanceof Player player) {
            // 遍历背包
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                // 如果找到 Shan 物品
                if (stack.getItem() == ModItems.SHAN.get()) {
                    // 消耗一个 Shan
                    stack.shrink(1);
                    // 取消此次伤害
                    event.setCanceled(true);
                    break;
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            long currentTime = player.level().getGameTime();
            long doubleDamageEndTime = player.getPersistentData().getLong("doubleDamageEndTime");

            // 检查玩家是否在双倍伤害的时间内
            if (player.getPersistentData().getBoolean("hasDoubleDamage") && currentTime <= doubleDamageEndTime) {
                event.setAmount(event.getAmount() * 2); // 双倍伤害
                player.getPersistentData().putBoolean("hasDoubleDamage", false); // 移除双倍伤害状态
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 检查背包中是否有 "jiu" 物品
            boolean hasJiu = false;
            ItemStack jiuStack = ItemStack.EMPTY;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() == ModItems.JIU.get()) {
                    hasJiu = true;
                    jiuStack = stack;
                    break;
                }
            }

            // 如果玩家有 "jiu" 并且受到的伤害大于当前生命值
            if (hasJiu && event.getAmount() >= player.getHealth()) {
                // 消耗一瓶 "jiu"
                jiuStack.shrink(1);
                // 将玩家生命值设为2
                player.setHealth(2.0F);
                // 取消此次伤害
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        // 判断是否为玩家死亡
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 读取之前打上的标记
            boolean flag = serverPlayer.getPersistentData().getBoolean("NonRespawn");
            if (flag) {
                // 直接改为观察者模式
                serverPlayer.setGameMode(GameType.SPECTATOR);

                // 如果只想让玩家在死亡后一次性进入观察者模式，而下次再进游戏正常复活，
                // 那么可以在这里重置标志（防止玩家一直保持观察者模式无法正常游玩）
                // serverPlayer.getPersistentData().putBoolean("NonRespawn", false);
            }
        }
    }
}

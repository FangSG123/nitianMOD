// DaikuangtutengHandler.java

package com.ntsw.event;

import com.ntsw.DaikuangtutengData;
import com.ntsw.ModItems;
import com.ntsw.network.ModMessages;
import com.ntsw.network.PacketDaikuangtutengEffect;
import com.ntsw.network.PacketUpdateAccumulatedDamage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "nitian")
public class DaikuangtutengHandler {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // 如果当前正在应用伤害，不取消
        if (DaikuangtutengData.isApplyingDamage(player)) {
            // 重置标志位
            DaikuangtutengData.setApplyingDamage(player, false);
            return;
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() != ModItems.DAIKUANGTUTENG.get()) return;

        // 阻止伤害生效
        event.setCanceled(true);

        // 获取并累加伤害
        double damageAmount = event.getAmount();
        DaikuangtutengData.addAccumulatedDamage(player, damageAmount);

        double currentDamage = DaikuangtutengData.getAccumulatedDamage(player);

        // 发送更新的累计伤害到客户端
        if (player instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToClient(new PacketUpdateAccumulatedDamage(currentDamage), serverPlayer);
        }

        // 检查是否超过1000
        if (currentDamage > 1000) {
            applyAccumulatedDamage(player, currentDamage);
        }
        // 检查是否超过100
        else if (currentDamage > 100) {
            if (RANDOM.nextDouble() < 0.05) { // 5%概率
                applyAccumulatedDamage(player, currentDamage);
            }
        }

    }

    private static void applyAccumulatedDamage(Player player, double totalDamage) {
        // 设置标志位，允许伤害通过
        DaikuangtutengData.setApplyingDamage(player, true);

        // 造成所有累计伤害
        player.hurt(player.damageSources().generic(), (float) totalDamage);

        // 清空累计伤害
        DaikuangtutengData.resetAccumulatedDamage(player);

        // 发送更新的累计伤害到客户端
        if (player instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToClient(new PacketUpdateAccumulatedDamage(0), serverPlayer);
        }

        // 触发客户端动画
        if (player instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToClient(new PacketDaikuangtutengEffect(), serverPlayer);
        }
    }

    public static void triggerEffect(Player player) {
        double accumulatedDamage = DaikuangtutengData.getAccumulatedDamage(player);
        if (accumulatedDamage > 0) {
            // 设置标志位，允许伤害通过
            DaikuangtutengData.setApplyingDamage(player, true);

            // 造成5点伤害
            player.hurt(player.damageSources().generic(), 5.0F);

            // 减少5点累计伤害
            DaikuangtutengData.subtractAccumulatedDamage(player, 5.0);

            // 获取当前累计伤害
            double newAccumulatedDamage = DaikuangtutengData.getAccumulatedDamage(player);

            // 发送更新的累计伤害到客户端
            if (player instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToClient(new PacketUpdateAccumulatedDamage(newAccumulatedDamage), serverPlayer);
            }

            // 触发客户端动画
            if (player instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToClient(new PacketDaikuangtutengEffect(), serverPlayer);
            }
        }
    }
}

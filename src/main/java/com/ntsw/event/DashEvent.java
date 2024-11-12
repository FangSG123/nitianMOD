// 文件路径：com.hollow.event.ClientEvents.java

package com.ntsw.event;

import com.ntsw.Main;
import com.ntsw.item.KeyBindings;
import com.ntsw.item.PifengItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class DashEvent {

    private static long lastDashTime = 0;
    private static final long COOLDOWN = 600; // 0.6 秒冷却时间

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.DASH_KEY.consumeClick()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.isAlive()) {
                ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
                if (chestItem.getItem() instanceof PifengItem) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastDashTime >= COOLDOWN) {
                        dashForward(player);
                        lastDashTime = currentTime;
                    }
                }
            }
        }
    }

    private static void dashForward(Player player) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 dashVec = new Vec3(lookVec.x, 0, lookVec.z).normalize().scale(2.0); // 冲刺距离可调整

        player.push(dashVec.x, 0, dashVec.z);
        player.hurtMarked = true; // 确保客户端更新玩家位置

        for (int i = 0; i < 20; i++) {  // 粒子数量，可调整
            double x = player.getX() - dashVec.x * (i * 0.1); // 粒子位置沿路径分布
            double y = player.getY() + 0.1;                   // 粒子高度略高于地面
            double z = player.getZ() - dashVec.z * (i * 0.1);
            player.level().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }
}

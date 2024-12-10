package com.ntsw.client;

import com.ntsw.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "nitian", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {

    private static int tickCounter = 0; // 计时器变量

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.ClientTickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player != null && player.hasEffect(ModEffects.FAQING.get())) {
                // 每 5 个 tick 生成一个粒子
                if (tickCounter % 5 == 0) {
                    double x = player.getX();
                    double y = player.getY() + player.getEyeHeight() + 0.5; // 调整高度以适应视觉效果
                    double z = player.getZ();
                    mc.level.addParticle(ParticleTypes.HEART,
                            x + (Math.random() - 0.5) * 0.5,
                            y + Math.random() * 0.5,
                            z + (Math.random() - 0.5) * 0.5,
                            0, 0.1, 0); // 生成一个爱心粒子
                }

                tickCounter++; // 每次 tick 增加计数器

                if (tickCounter >= Integer.MAX_VALUE) {
                    tickCounter = 0; // 防止计数器溢出
                }
            }
        }
    }
}

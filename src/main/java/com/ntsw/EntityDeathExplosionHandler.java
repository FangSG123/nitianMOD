package com.ntsw;

import com.ntsw.entity.ChuanJianGuoEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class EntityDeathExplosionHandler {

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        // 获取死亡的实体
        LivingEntity entity = event.getEntity();

        // 检查是否在有效的世界中
        if (entity.level() != null && entity instanceof ChuanJianGuoEntity) {
            // 生成爆炸效果
            entity.level().explode(null, entity.getX(), entity.getY(), entity.getZ(), 100F, Level.ExplosionInteraction.BLOCK);
        }
    }
}

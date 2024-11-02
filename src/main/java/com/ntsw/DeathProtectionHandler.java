package com.ntsw;

import com.ntsw.item.FeiJiPiao;
import com.ntsw.network.ModMessages;
import com.ntsw.network.PacketFeiJiPiaoTotemEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;

@Mod.EventBusSubscriber
public class DeathProtectionHandler {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            Level level = player.level();
            ItemStack offhandItem = player.getOffhandItem();

            if (offhandItem.getItem() instanceof FeiJiPiao) {
                event.setCanceled(true);
                player.setHealth(10.0F);

                if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    // 发送自定义包到客户端，显示`FeiJiPiao`的图腾动画
                    ModMessages.sendToClient(new PacketFeiJiPiaoTotemEffect(), serverPlayer);

                    // 播放音效
                    player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);

                    // 添加恢复和吸收效果
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));

                    ((FeiJiPiao) offhandItem.getItem()).summonEntities(level, player);
                    offhandItem.shrink(1);
                }
            }
        }
    }
}

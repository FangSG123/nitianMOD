package com.ntsw.item;

import com.ntsw.Main;
import com.ntsw.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class ArmorEventHandler {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 获取玩家头盔装备
        ItemStack helmet = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);

        // 检查是否装备了 Baguajing 头盔
        if (helmet.getItem() == ModItems.BAGUAJING_HELMET.get()) {
            // 50% 概率不掉血
            if (shouldAvoidDamage()) {
                event.setCanceled(true);
                player.displayClientMessage(Component.translatable("判定成功"), true);

                // 可选：添加粒子和声音效果
                player.level().addParticle(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 0.0, 0.0, 0.0);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    /**
     * 判断是否应该避免伤害
     *
     * @return 如果应避免，返回 true；否则返回 false
     */
    private static boolean shouldAvoidDamage() {
        return RANDOM.nextFloat() < 0.5f;
    }
}

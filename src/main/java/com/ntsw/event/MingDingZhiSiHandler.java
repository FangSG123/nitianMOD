package com.ntsw.event;

import com.ntsw.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "nitian")
public class MingDingZhiSiHandler {

    private static final String MING_DING_ZHI_SI_TAG = "mingdingzhisi";

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        // 获取生物实体
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return; // 忽略客户端执行

        // 排除玩家
        if (entity instanceof Player) return;

        // 检查附近的掉落物
        for (Entity nearbyEntity : entity.level().getEntities(entity, entity.getBoundingBox().inflate(0.5))) {
            if (nearbyEntity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getItem();

                // 检查是否为必死图腾的掉落物
                if (stack.getItem() == ModItems.MUST_DIE_TOTEM.get()) {

                    // 移除掉落物
                    if (!entity.level().isClientSide() && entity.level() instanceof ServerLevel) {
                        itemEntity.discard(); // 删除掉落物实体
                    }
                }
            }
        }
    }
}

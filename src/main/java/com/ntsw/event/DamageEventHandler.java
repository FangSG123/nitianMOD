package com.ntsw.event;

import com.ntsw.Main;
import com.ntsw.ModEffects;
import com.ntsw.item.PddItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class DamageEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        // 检查实体是否有免疫效果
        if (entity.hasEffect(ModEffects.DAMAGE_IMMUNITY.get())) {
            // 检查伤害来源是否来自 Pdd 武器
            boolean isFromPdd = false;
            if (event.getSource().getEntity() instanceof Player player) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof PddItem) {
                    isFromPdd = true;
                }
            }

            if (!isFromPdd) {
                // 取消伤害
                event.setCanceled(true);
            }
        }
    }
}

package com.ntsw;

import com.ntsw.item.PddItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class PddEventHandler {

    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof PddItem) {
            LivingEntity target = (LivingEntity) event.getTarget();

            if (target.getHealth() < 5.0F) {
                if (!player.level().isClientSide) {
                    // 给予玩家一个积分
                    ItemStack zuanshi = new ItemStack(ModItems.ZUANSHI.get());
                    player.getInventory().add(zuanshi);
                }
                event.setCanceled(true); // 取消默认攻击，避免造成伤害
            } else {
                // 造成目标当前生命值的90%伤害
                float damage = target.getHealth() * 0.9F;
                DamageSource damageSource = player.damageSources().playerAttack(player);
                target.hurt(damageSource, damage);

                // 施加免疫效果，持续120秒（2400 ticks）
                target.addEffect(new MobEffectInstance(ModEffects.DAMAGE_IMMUNITY.get(), 2400, 0));

                // 减少物品耐久度
                heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
        }
    }
}

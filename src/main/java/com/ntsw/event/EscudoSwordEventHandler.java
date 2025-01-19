package com.ntsw.event;

import com.ntsw.Main;
import com.ntsw.item.EscudoSwordItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent; // 新增的事件
import net.minecraftforge.event.entity.EntityJoinLevelEvent; // 1.20.x 生成事件
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 事件处理器：处理与埃斯库多之剑相关的事件
 */
@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EscudoSwordEventHandler {

    /**
     * 当实体被击杀时触发
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof Player player)) {
            return;
        }
        // 判断玩家是否是拿着我们的自定义武器
        ItemStack held = player.getMainHandItem();
        if (held.getItem() instanceof EscudoSwordItem escudoSword) {
            LivingEntity deadEntity = event.getEntity();
            String entityKey = deadEntity.getType().toString();

            // 增加击杀数量
            escudoSword.incrementKillCount(held, entityKey);
            int count = escudoSword.getKillCount(held, entityKey);

            // 如果击杀数恰好到达 10，说明要增加伤害 & 将该生物类型加入自动击杀
            if (count == 10) {
                int oldDamage = escudoSword.getCurrentDamage(held);
                escudoSword.setCurrentDamage(held, oldDamage + 5);
                escudoSword.addEntityToAutoKillList(held, entityKey);
            }
        }
    }

    /**
     * 自定义伤害逻辑：我们不继承 SwordItem，需要在事件里手动处理
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof Player player)) {
            return;
        }
        ItemStack held = player.getMainHandItem();
        if (held.getItem() instanceof EscudoSwordItem escudoSword) {
            // 如果伤害达到了 50，直接秒杀
            if (escudoSword.isMaxDamageReached(held)) {
                if (!player.isCreative()) { // 仅非创造模式玩家使用原有逻辑
                    event.setAmount(99999.0F); // 给予一个很高的伤害，模拟直接 Kill
                }
                // 对于创造模式，另行处理
            } else {
                // 否则就按当前 NBT 中的伤害来
                float customDamage = escudoSword.getCurrentDamage(held);
                event.setAmount(customDamage);
            }
        }
    }

    /**
     * 监听实体被攻击事件：用于处理创造模式玩家的攻击
     */
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack held = player.getMainHandItem();

        if (held.getItem() instanceof EscudoSwordItem escudoSword) {
            // 检查武器是否达到最大伤害
            if (escudoSword.isMaxDamageReached(held)) {
                Entity target = event.getTarget();

                // 强制杀死目标实体
                if (target instanceof LivingEntity living) {
                    living.kill(); // 或者 living.setHealth(0.0F);
                } else {
                    target.discard(); // 如果是非 LivingEntity 类型，也可以直接移除
                }

                // 取消后续的伤害事件，防止多次处理
                event.setCanceled(true);
            }
        }
    }

    /**
     * 监听实体生成事件：若其类型在 auto_kill_list 中，则立即杀死
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        // 仅处理服务端逻辑，避免客户端无意义运算
        if (entity.level().isClientSide) {
            return;
        }

        // 检查所有玩家的武器
        for (Player player : event.getLevel().players()) {
            ItemStack held = player.getMainHandItem();

            // 检查是否为埃斯库多之剑
            if (held.getItem() instanceof EscudoSwordItem escudoSword) {
                String entityKey = entity.getType().toString();

                // 检查实体类型是否在自动击杀列表中
                if (escudoSword.isInAutoKillList(held, entityKey)) {
                    // 阻止实体生成
                    event.setCanceled(true);
                    return; // 避免继续遍历，提升性能
                }
            }
        }
    }
}

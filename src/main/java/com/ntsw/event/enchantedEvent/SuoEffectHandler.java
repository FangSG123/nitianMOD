package com.ntsw.event.enchantedEvent;



import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SuoEffectHandler {
    private static final UUID SUO_SPEED_BOOST_ID = UUID.fromString("12345678-1234-1234-1234-1234567890ab");
    private static final AttributeModifier SUO_SPEED_BOOST = new AttributeModifier(SUO_SPEED_BOOST_ID, "Suo speed boost", 1D, AttributeModifier.Operation.ADDITION);

    // 当玩家开始拉弓时设置标记
    @SubscribeEvent
    public static void onUsingBowStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack itemStack = event.getItem();
            if (itemStack.getItem() instanceof BowItem && itemStack.getEnchantmentLevel(ModEnchantments.SUO.get()) > 0) {
                applySpeedBoost(player);
            }
        }
    }

    // 在每个玩家的tick中检查
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.isUsingItem()) {
            ItemStack itemStack = player.getUseItem();
            if (itemStack.getItem() instanceof BowItem && itemStack.getEnchantmentLevel(ModEnchantments.SUO.get()) > 0) {
                // 持续应用速度增益
                applySpeedBoost(player);
                return;
            }
        }
        // 如果玩家不在使用附魔弓，移除速度增益
        removeSpeedBoost(player);
    }

    // 当玩家释放弓或拉弓结束时移除速度增益
    @SubscribeEvent
    public static void onFinishUsingBow(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player) {
            removeSpeedBoost(player);
        }
    }

    @SubscribeEvent
    public static void onFinishUsingBow(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            removeSpeedBoost(player);
        }
    }

    // 控制 FOV 视角变形
    @SubscribeEvent
    public static void onFOVUpdate(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.isUsingItem()) {
            ItemStack itemStack = player.getUseItem();
            // 如果玩家正在使用带suo附魔的弓，则锁定FOV为默认值
            if (itemStack.getItem() instanceof BowItem && itemStack.getEnchantmentLevel(ModEnchantments.SUO.get()) > 0) {
                event.setNewFovModifier(1.0F); // 将视角固定为默认值，不受速度影响
            }
        }
    }

    // 应用速度增益
    private static void applySpeedBoost(Player player) {
        if (!player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(SUO_SPEED_BOOST)) {
            player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(SUO_SPEED_BOOST); // 使用临时修饰符
        }
    }

    // 移除速度增益
    private static void removeSpeedBoost(Player player) {
        if (player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(SUO_SPEED_BOOST)) {
            player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SUO_SPEED_BOOST_ID);
        }
    }
}

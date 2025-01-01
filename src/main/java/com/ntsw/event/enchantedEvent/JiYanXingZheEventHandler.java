package com.ntsw.event.enchantedEvent;


import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JiYanXingZheEventHandler {

    private static final double MIN_Y_COORDINATE = -67.0;  // 玩家最低允许的 Y 坐标

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 确保只在服务端执行，以避免客户端视觉错误
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        // 检查玩家是否穿着附有 JiYanXingZhe 附魔的鞋子
        if (boots.getEnchantmentLevel(ModEnchantments.JIYANXINGZHE.get()) > 0) {
            // 检查玩家的 Y 坐标是否低于指定的最小值
            if (player.getY() < MIN_Y_COORDINATE - 1) {
                // 将玩家的 Y 坐标设置为 MIN_Y_COORDINATE
                System.out.println("cs");
                player.teleportTo(player.getX(), MIN_Y_COORDINATE, player.getZ());
                player.setNoGravity(true);
                player.fallDistance = 0.0F;  // 重置玩家的坠落距离，以避免坠落伤害
            }
        }
        if (player.getY() > -65)
        {
            player.setNoGravity(false);
        }
    }
}

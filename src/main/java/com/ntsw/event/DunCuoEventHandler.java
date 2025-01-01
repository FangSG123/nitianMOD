package com.ntsw.event;

import com.ntsw.Main;
import com.ntsw.network.ModNetworkHandler;
import com.ntsw.network.ShieldAttackPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class DunCuoEventHandler {

    @SubscribeEvent
    public static void onMouseButtonInput(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        // 检查鼠标左键按下事件
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && event.getAction() == GLFW.GLFW_PRESS) {
           // System.out.println("MouseButton.Pre event detected: Left button pressed.");

            if (player != null && player.isUsingItem() && player.getUsedItemHand() == InteractionHand.OFF_HAND) {
               // System.out.println("Player is using item in off-hand.");

                ItemStack offHandItem = player.getOffhandItem();
                if (offHandItem.getItem() instanceof ShieldItem && offHandItem.isEnchanted()) {
                  //  System.out.println("Off-hand item is an enchanted shield.");

                    // 发送网络包到服务端
                    ModNetworkHandler.INSTANCE.sendToServer(new ShieldAttackPacket());

                    // 取消事件，防止默认行为
                    event.setCanceled(true);

                    // 在客户端触发主手攻击动画
                    player.swing(InteractionHand.MAIN_HAND);
                } else {
                   // System.out.println("Off-hand item is not an enchanted shield.");
                }
            } else {
              //  System.out.println("Player is not using item in off-hand.");
            }
        }
    }
}

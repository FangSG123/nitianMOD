package com.ntsw.client;

import com.ntsw.ModItems;
import com.ntsw.network.ClientAccumulatedDamageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "nitian", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GuiOverlayHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        // 可选：根据需要检查特定的 Overlay 类型
        // 由于 RenderGuiOverlayEvent 没有 ElementType.TEXT，可以选择移除检查或使用正确的 NamedGuiOverlay 常量
        // 例如，如果你只想在所有 Overlay 渲染后绘制，可以跳过类型检查

        // 获取 Minecraft 实例和玩家
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        // 检查副手是否持有 daikuangtuteng
        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() != ModItems.DAIKUANGTUTENG.get()) return;
        // 获取累计伤害
        double accumulatedDamage = ClientAccumulatedDamageManager.getAccumulatedDamage();
        double probability = accumulatedDamage/10;
        if(accumulatedDamage < 40)
        {
            probability = 0;
        }
        // 绘制文本
        String text = "累计伤害: " + (int) accumulatedDamage + "   " + "强制还款概率: " + (int)probability + "%";
        float x = 10.0F; // 左上角X坐标
        float y = 10.0F; // 左上角Y坐标
        int color = 0xFFFFFF; // 白色

        // 使用 GuiGraphics 绘制带阴影的字符串
        event.getGuiGraphics().drawString(mc.font, text, x, y, color, true);
    }
}

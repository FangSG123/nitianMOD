package com.ntsw.event;


import com.mojang.blaze3d.vertex.PoseStack;
import com.ntsw.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class MeiliaoRenderHandler {

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<LivingEntity, ?> event) {
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        // 动态检查生物是否仍然拥有“meiliao”效果
        MobEffectInstance meiliaoEffect = entity.getEffect(ModEffects.MEILIAO.get());
        if (meiliaoEffect != null && meiliaoEffect.getDuration() > 0) {
            // 设置要显示的文本
            String text = "魅了";

            // 确定文字位置
            poseStack.pushPose();
            poseStack.translate(0, entity.getBbHeight() + 0.5, 0); // 移动到生物头顶
            poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation()); // 保持面向摄像机
            poseStack.scale(-0.025F, -0.025F, 0.025F); // 缩放文本
            // 获取文本宽度，居中对齐
            float textWidth = font.width(text) / 2.0F;

            // 渲染文本
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            font.drawInBatch(text, -textWidth, 0, 0xFF69B4, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            bufferSource.endBatch();

            poseStack.popPose();

            // 在生物周围随机生成爱心粒子效果
            if (minecraft.level != null && minecraft.level.getRandom().nextFloat() < 0.05) { // 控制粒子生成的频率
                double radius = 0.5 + minecraft.level.getRandom().nextDouble() * 0.5; // 半径范围0.5到1.0
                double angle = minecraft.level.getRandom().nextDouble() * Math.PI * 2; // 随机角度
                double x = entity.getX() + radius * Math.cos(angle); // 水平方向偏移
                double y = entity.getY() + entity.getBbHeight() * 0.5 + minecraft.level.getRandom().nextDouble() * 0.5; // 高度偏移
                double z = entity.getZ() + radius * Math.sin(angle); // 水平方向偏移
                minecraft.level.addParticle(ParticleTypes.HEART, x, y, z, 0, 0, 0);
            }
        }
    }


}

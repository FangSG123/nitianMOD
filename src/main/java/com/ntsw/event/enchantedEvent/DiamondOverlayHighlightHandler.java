package com.ntsw.event.enchantedEvent;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class DiamondOverlayHighlightHandler {
    private static final int SCAN_RADIUS = 10; // 扫描范围

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        // 确保在渲染叠加层时执行（AFTER_TRIPWIRE为一个深度检测后的适合阶段）
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;

        // 检查玩家是否有 TOUSHI 附魔且等级为 3
        ItemStack heldItem = minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
        int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.TOUSHI.get(), heldItem);

        if (enchantmentLevel < 3) return;

        // 获取玩家位置
        BlockPos playerPos = minecraft.player.blockPosition();

        // 遍历指定范围内的方块
        for (int x = -SCAN_RADIUS; x <= SCAN_RADIUS; x++) {
            for (int y = -SCAN_RADIUS; y <= SCAN_RADIUS; y++) {
                for (int z = -SCAN_RADIUS; z <= SCAN_RADIUS; z++) {
                    BlockPos blockPos = playerPos.offset(x, y, z);
                    BlockState blockState = minecraft.level.getBlockState(blockPos);

                    // 检查方块是否为钻石矿
                    if (blockState.is(Blocks.DIAMOND_ORE) || blockState.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {
                        // 绘制在叠加层上的高亮
                        renderOverlayHighlight(event.getPoseStack(), blockPos);
                    }
                }
            }
        }
    }

    // 使用叠加层渲染的高亮
    private static void renderOverlayHighlight(PoseStack poseStack, BlockPos blockPos) {
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();
        double x = blockPos.getX() - cameraPos.x;
        double y = blockPos.getY() - cameraPos.y;
        double z = blockPos.getZ() - cameraPos.z;

        AABB boundingBox = new AABB(x, y, z, x + 1, y + 1, z + 1);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest(); // 禁用深度测试以确保不被遮挡

        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, vertexConsumer, boundingBox.minX, boundingBox.minY, boundingBox.minZ,
                boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ,
                0.0F, 1.0F, 0.0F, 0.8F); // 半透明绿色

        bufferSource.endBatch();

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}

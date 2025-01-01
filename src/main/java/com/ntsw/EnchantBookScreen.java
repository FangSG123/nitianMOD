    package com.ntsw;

    import com.mojang.blaze3d.systems.RenderSystem;
    import net.minecraft.client.gui.GuiGraphics;
    import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
    import net.minecraft.client.renderer.GameRenderer;
    import net.minecraft.network.chat.Component;
    import net.minecraft.resources.ResourceLocation;
    import net.minecraft.world.entity.player.Inventory;

    public class EnchantBookScreen extends AbstractContainerScreen<EnchantBookContainer> {

        private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

        public EnchantBookScreen(EnchantBookContainer container, Inventory inv, Component title) {
            super(container, inv, title);
            this.imageWidth = 176;  // 宽度
            this.imageHeight = 166; // 高度
        }

        @Override
        protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
            // 设置渲染的着色器
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            // 绑定纹理
            RenderSystem.setShaderTexture(0, TEXTURE);
            // 绘制背景图
            guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            super.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

package com.ntsw.entityrenderer;

import com.ntsw.Main;
import com.ntsw.entity.ShikuaiEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;

public class ShiKuaiRenderer extends ThrownItemRenderer<ShikuaiEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/projectile/shikuai.png");

    public ShiKuaiRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0F, true); // 可以根据需求设置缩放和渲染是否旋转
    }
}

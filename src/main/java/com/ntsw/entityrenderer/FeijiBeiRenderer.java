package com.ntsw.entityrenderer;

import com.ntsw.entity.FeijiBeiEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FeijiBeiRenderer extends MobRenderer<FeijiBeiEntity, ChickenModel<FeijiBeiEntity>> {

    // 使用原版鸡的贴图
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/chicken.png");

    public FeijiBeiRenderer(EntityRendererProvider.Context context) {
        // 使用原版鸡模型层定义
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(FeijiBeiEntity entity) {
        return TEXTURE; // 返回原版鸡的贴图路径
    }
}

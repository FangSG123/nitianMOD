package com.ntsw.entityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ntsw.Main;
import com.ntsw.entity.ChuanJianGuoEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChuanJianGuoEntityRenderer extends MobRenderer<ChuanJianGuoEntity, PlayerModel<ChuanJianGuoEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/chuanjianguo_entity.png");

    public ChuanJianGuoEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ChuanJianGuoEntity entity) {
        return TEXTURE;
    }
    @Override
    protected void scale(ChuanJianGuoEntity entity, PoseStack poseStack, float partialTickTime) {
        if (entity.isTransformed) {
            poseStack.scale(3.0F, 3.0F, 3.0F); // 将模型放大三倍
        }
        super.scale(entity, poseStack, partialTickTime);
    }
}

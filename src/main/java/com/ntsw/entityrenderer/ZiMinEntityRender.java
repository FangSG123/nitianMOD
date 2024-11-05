package com.ntsw.entityrenderer;

import com.ntsw.Main;
import com.ntsw.entity.ZiMinEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ZiMinEntityRender extends HumanoidMobRenderer<ZiMinEntity, PlayerModel<ZiMinEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/zimin_entity.png");

    public ZiMinEntityRender(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(ZiMinEntity entity) {
        return TEXTURE;
    }

    // 移除 render 方法的重写，让基类处理动画
    // @Override
    // public void render(ZiMinEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
    //     if (entity.swinging) {
    //         this.getModel().attackTime = entity.getAttackAnim(partialTicks);
    //     } else {
    //         this.getModel().attackTime = 0.0F;
    //     }
    //     super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    // }
}

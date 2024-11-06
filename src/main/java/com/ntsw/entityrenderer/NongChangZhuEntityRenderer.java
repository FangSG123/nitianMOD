package com.ntsw.entityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ntsw.Main;
import com.ntsw.entity.NongChangZhuEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class NongChangZhuEntityRenderer extends MobRenderer<NongChangZhuEntity, PlayerModel<NongChangZhuEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/nongchangzhu_entity.png");

    public NongChangZhuEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }
    @Override
    public ResourceLocation getTextureLocation(NongChangZhuEntity entity) {
        return TEXTURE;
    }
}

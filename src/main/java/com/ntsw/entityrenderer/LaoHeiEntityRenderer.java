package com.ntsw.entityrenderer;

import com.ntsw.Main;
import com.ntsw.entity.HeiManBaEntity;
import com.ntsw.entity.LaoHeiEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LaoHeiEntityRenderer extends MobRenderer<LaoHeiEntity, PlayerModel<LaoHeiEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/laohei_entity.png");

    public LaoHeiEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LaoHeiEntity entity) {
        return TEXTURE;
    }
}

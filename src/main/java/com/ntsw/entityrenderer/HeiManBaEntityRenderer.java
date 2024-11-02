package com.ntsw.entityrenderer;

import com.ntsw.Main;
import com.ntsw.ntsw.entity.HeiManBaEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HeiManBaEntityRenderer extends MobRenderer<HeiManBaEntity, PlayerModel<HeiManBaEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/heimanba_entity.png");

    public HeiManBaEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(HeiManBaEntity entity) {
        return TEXTURE;
    }
}

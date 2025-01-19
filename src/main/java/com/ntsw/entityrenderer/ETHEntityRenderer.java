package com.ntsw.entityrenderer;

import com.ntsw.Main;
import com.ntsw.entity.ETHEntity;
import com.ntsw.entity.NaiLongEntity;
import com.ntsw.model.ETHModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ETHEntityRenderer extends MobRenderer<ETHEntity, ETHModel> {

    public ETHEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ETHModel(context.bakeLayer(ETHModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ETHEntity entity) {
        return new ResourceLocation(Main.MODID, "textures/entity/eth_entity.png");
    }
}

package com.ntsw.entityrenderer;

import com.ntsw.Main;
import com.ntsw.model.NaiLongModel;
import com.ntsw.entity.NaiLongEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NaiLongEntityRenderer extends MobRenderer<NaiLongEntity, NaiLongModel> {

    public NaiLongEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new NaiLongModel(context.bakeLayer(NaiLongModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(NaiLongEntity entity) {
        return new ResourceLocation(Main.MODID, "textures/entity/nailong_entity.png");
    }
}

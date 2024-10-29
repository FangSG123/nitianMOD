package com.nailong.nailong;

import com.nailong.nailong.Main;
import com.nailong.nailong.NaiLongModel;
import com.nailong.nailong.NaiLongEntity;
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

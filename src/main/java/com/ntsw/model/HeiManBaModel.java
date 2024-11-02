package com.ntsw.model;

import com.ntsw.Main;
import com.ntsw.ModEntity;
import com.ntsw.entityrenderer.HeiManBaEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HeiManBaModel {
    @SubscribeEvent
    public static void onEntityRendererRegistration(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntity.HeiManBa_Entity.get(), HeiManBaEntityRenderer::new);
    }
}

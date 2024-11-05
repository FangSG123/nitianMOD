package com.ntsw.model;

import com.ntsw.Main;
import com.ntsw.ModEntitys;
import com.ntsw.entityrenderer.HeiManBaEntityRenderer;
import com.ntsw.entityrenderer.ZiMinEntityRender;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ZiMinModel {
    @SubscribeEvent
    public static void onEntityRendererRegistration(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntitys.HeiManBa_Entity.get(), HeiManBaEntityRenderer::new);
    }
}

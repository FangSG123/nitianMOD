package com.ntsw;

import com.mojang.logging.LogUtils;
import com.ntsw.ntsw.entity.HeiManBaEntity;
import com.ntsw.model.NaiLongModel;
import com.ntsw.network.ModMessages;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "nitian";
    private static final Logger LOGGER = LogUtils.getLogger();


    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册生命周期事件
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::addEntityAttributes);
        modEventBus.addListener(Main::registerLayerDefinitions);
        MinecraftForge.EVENT_BUS.register(new DeathProtectionHandler()); //自定义监听器

        MinecraftForge.EVENT_BUS.addListener(this::addCreativeTab);
        MinecraftForge.EVENT_BUS.register(this);


        ModItems.ITEMS.register(modEventBus);
        ModMessages.register();
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModEntity.ENTITY_TYPES.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }
    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.HuangTaoGuangTou);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.FeiJiPiao);
        }
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntity.NAILONG_ENTITY.get(), NaiLongEntity.createAttributes().build());
        event.put(ModEntity.HeiManBa_Entity.get(), HeiManBaEntity.createAttributes().build());
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntity.NAILONG_ENTITY.get(), NaiLongEntityRenderer::new);
        EntityRenderers.register(ModEntity.HeiManBa_Entity.get(), HeiManBaEntityRenderer::new);
    }

    // 注册模型层定义
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NaiLongModel.LAYER_LOCATION, NaiLongModel::createBodyLayer);
    }

}

package com.ntsw;

import com.mojang.logging.LogUtils;
import com.ntsw.entity.*;
import com.ntsw.entityrenderer.*;
import com.ntsw.event.DeathProtectionHandler;
import com.ntsw.item.KeyBindings;
import com.ntsw.model.NaiLongModel;
import com.ntsw.network.ModMessages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Collections;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "nitian";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> NITIAN_TAB = CREATIVE_MODE_TABS.register("nitian_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(ModItems.HuangTaoGuangTou.get()))
            .title(Component.translatable("itemGroup.nitian"))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.SHIKUAI_ITEM.get());
                output.accept(ModItems.DABIAN.get());
                output.accept(ModItems.FeiJiPiao.get());
                output.accept(ModItems.HuangTaoGuangTou.get());
                output.accept(ModItems.PDD.get());
                output.accept(ModItems.PDD_ZHIDUN.get());
                output.accept(ModItems.JIFEN.get());
                output.accept(ModItems.SAOBA.get());
                output.accept(ModItems.ZHANSHISAOBA.get());
                output.accept(ModItems.ZUANSHI.get());

                output.accept(ModItems.SHAN.get());
                output.accept(ModItems.GUDINGDAO.get());
                output.accept(ModItems.JIU.get());

                output.accept(ModItems.BUSHI_GEMEN.get());
                output.accept(ModItems.TIESUO_LIANHUAN.get());
                output.accept(ModItems.HUOSHA.get());
                output.accept(ModItems.BAGUAJING_HELMET.get());
                output.accept(ModItems.MUST_DIE_TOTEM.get());


                output.accept(ModItems.ZIMIN_SPAWN_EGG.get());
                output.accept(ModItems.NONGCHANGZHU_SPAWN_EGG.get());
                output.accept(ModItems.NAILONG_SPAWN_EGG.get());
                output.accept(ModItems.LAOHEI_SPAWN_EGG.get());
                output.accept(ModItems.TRUMP_SPAWN_EGG.get());
                output.accept(ModItems.HEIMANBA_SPAWN_EGG.get());
                output.accept(ModItems.FEIJIBEI_SPAWN_EGG.get());
                output.accept(ModItems.MCTOW_SPAWN_EGG.get());
                output.accept(ModBlocks.LAUGH_OBSIDIAN_ITEM.get());
                output.accept(ModBlocks.LAUGH_PORTAL_ITEM.get());
                output.accept(ModBlocks.NTN_BLOCK_ITEM.get());

                output.accept(ModItems.DAIKUANGTUTENG.get());
                output.accept(ModItems.DAIKUANGTUTENGBIG.get());
                output.accept(ModItems.DAIKUANGTUTENGMAX.get());

            }).build());





    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册生命周期事件
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModEntitys.register(modEventBus);
        ModItems.register(modEventBus);
        ModMessages.register();
        ModEffects.MOB_EFFECTS.register(modEventBus);

        ModPotions.POTIONS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);

        ModBlocks.ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(this::addCreativeTab);
//        ModDamageTypes.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::addEntityAttributes);
        modEventBus.addListener(Main::registerLayerDefinitions);
        CREATIVE_MODE_TABS.register(modEventBus);


        MinecraftForge.EVENT_BUS.register(new DeathProtectionHandler()); //自定义监听器
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // 创建一个包含浓稠药水的 ItemStack
        ItemStack thickPotionStack = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.THICK);

        // 将 FAQING 药水效果添加到浓稠药水中
        PotionUtils.setCustomEffects(thickPotionStack, Collections.singletonList(new MobEffectInstance(ModEffects.FAQING.get(), 3600)));

        // 注册药水配方
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(thickPotionStack), // 基础药水（作为 ItemStack）
                Ingredient.of(Items.BLAZE_POWDER), // 激活物品（烈焰棒）
                thickPotionStack // 生成的药水（FAQING 药水效果）
        );
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }
    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {

    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntitys.NAILONG_ENTITY.get(), NaiLongEntity.createAttributes().build());
        event.put(ModEntitys.HeiManBa_Entity.get(), HeiManBaEntity.createAttributes().build());
        event.put(ModEntitys.CHUANGJIANGUO.get(), ChuanJianGuoEntity.createAttributes().build());
        event.put(ModEntitys.ZiMin_Entity.get(), ZiMinEntity.createAttributes().build());
        event.put(ModEntitys.LAO_HEI.get(), LaoHeiEntity.createAttributes().build());
        event.put(ModEntitys.NONGCHANGZHU.get(), NongChangZhuEntity.createAttributes().build());
        event.put(ModEntitys.FeiJiBei.get(), FeijiBeiEntity.createAttributes().build());
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntitys.NAILONG_ENTITY.get(), NaiLongEntityRenderer::new);
        EntityRenderers.register(ModEntitys.HeiManBa_Entity.get(), HeiManBaEntityRenderer::new);
        EntityRenderers.register(ModEntitys.ZiMin_Entity.get(), ZiMinEntityRender::new);
        EntityRenderers.register(ModEntitys.LAO_HEI.get(), LaoHeiEntityRenderer::new);
        EntityRenderers.register(ModEntitys.NONGCHANGZHU.get(),NongChangZhuEntityRenderer::new);
        EntityRenderers.register(ModEntitys.CHUANGJIANGUO.get(),ChuanJianGuoEntityRenderer::new);
        EntityRenderers.register(ModEntitys.SHIKUAI.get(), ShiKuaiRenderer::new);
        EntityRenderers.register(ModEntitys.FeiJiBei.get(),FeijiBeiRenderer::new);
    }


    // 注册模型层定义
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NaiLongModel.LAYER_LOCATION, NaiLongModel::createBodyLayer);
    }
}

package com.ntsw;

import com.ntsw.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntitys {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Main.MODID);

    // 定义并注册自定义实体
    public static final RegistryObject<EntityType<NaiLongEntity>> NAILONG_ENTITY = ENTITY_TYPES.register("nailong_entity",
            () -> EntityType.Builder.of(NaiLongEntity::new, MobCategory.MONSTER)
                    .sized(3f, 6f)  // 设置实体的尺寸
                    .build(new ResourceLocation(Main.MODID, "nailong_entity").toString()));
    public static final RegistryObject<EntityType<ETHEntity>> ETH_ENTITY = ENTITY_TYPES.register("eth_entity",
            () -> EntityType.Builder.of(ETHEntity::new, MobCategory.MONSTER)
                    .sized(3f, 6f)  // 设置实体的尺寸
                    .build(new ResourceLocation(Main.MODID, "eth_entity").toString()));
    public static final RegistryObject<EntityType<HeiManBaEntity>> HeiManBa_Entity = ENTITY_TYPES.register("heimanba_entity",
            () -> EntityType.Builder.of(HeiManBaEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F) // 设置实体尺寸
                    .build("heimanba_entity"));
    public static final RegistryObject<EntityType<ZiMinEntity>> ZiMin_Entity = ENTITY_TYPES.register("zimin_entity",
            () -> EntityType.Builder.of(ZiMinEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F) // 设置实体尺寸
                    .build("zimin_entity"));
    public static final RegistryObject<EntityType<LaoHeiEntity>> LAO_HEI = ENTITY_TYPES.register("laohei_entity",
            () -> EntityType.Builder.of(LaoHeiEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F) // 设置实体尺寸
                    .build("laohei_entity"));
    public static final RegistryObject<EntityType<NongChangZhuEntity>>NONGCHANGZHU = ENTITY_TYPES.register("nongchangzhu_entity",
            () -> EntityType.Builder.of(NongChangZhuEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F) // 设置实体尺寸
                    .build("nongchangzhu_entity"));
    public static final RegistryObject<EntityType<ChuanJianGuoEntity>>CHUANGJIANGUO = ENTITY_TYPES.register("chuanjianguo_entity",
            () -> EntityType.Builder.of(ChuanJianGuoEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F) // 设置实体尺寸
                    .build("chuanjianguo_entity"));

    public static final RegistryObject<EntityType<ShikuaiEntity>> SHIKUAI = ENTITY_TYPES.register("shikuai",
            () -> EntityType.Builder.<ShikuaiEntity>of(ShikuaiEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("shikuai"));
    public static final RegistryObject<EntityType<FeijiBeiEntity>> FeiJiBei = ENTITY_TYPES.register("feijibei",
            () -> EntityType.Builder.<FeijiBeiEntity>of(FeijiBeiEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("feijibei"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        System.out.println("ShikuaiEntity registered."); // 确认注册是否执行
    }
}

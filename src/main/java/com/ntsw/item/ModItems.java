package com.ntsw.item;

import com.ntsw.Main;
import com.ntsw.ModEffects;
import com.ntsw.ModEntitys;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SnowballItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // 注册物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);


    // 注册特殊食物
    public static final RegistryObject<Item> HuangTaoGuangTou = ITEMS.register("htgt",
            () -> new HuangTaoGuanTou(new Item.Properties()));

    public static final RegistryObject<Item> DABIAN = ITEMS.register("dabian", () -> new Item(new Item.Properties()
            .food(new FoodProperties.Builder()
                    .nutrition(1) // 回复一格饱食度
                    .saturationMod(0.1F) // 饱和度，设为较低值
                    .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 600), 1.0F) // 30秒反胃
                    .effect(() -> new MobEffectInstance(MobEffects.POISON, 600), 1.0F)    // 30秒中毒
                    .effect(() -> new MobEffectInstance(ModEffects.REPEL.get(), 600), 1.0F) // 30秒repel效果
                    .alwaysEat() // 即使饱食度满了也可以食用
                    .build())
    ));

    public static final RegistryObject<Item> SAOBA = ITEMS.register("saoba",
            () -> new SaobaItem(new Item.Properties()));
    public static final RegistryObject<Item> ZHANSHISAOBA = ITEMS.register("zhanshisaoba",
            () -> new ZhanshiSaobaItem(new Item.Properties()));



    public static final RegistryObject<Item> PDD = ITEMS.register("pdd", PddItem::new);

    public static final RegistryObject<Item> FeiJiPiao = ITEMS.register("feijipiao",() -> new FeiJiPiao(new Item.Properties()));

    public static final RegistryObject<Item> ZUANSHI  = ITEMS.register("zuanshi", ZuanShiItem::new);
    public static final RegistryObject<Item> JIFEN = ITEMS.register("jifen", JifenItem::new);

    public static final RegistryObject<Item> ZIMIN_SPAWN_EGG = ITEMS.register("zimin_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.ZiMin_Entity, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));
    public static final RegistryObject<Item> SHIKUAI_ITEM = ITEMS.register("shikuai",
            () -> new ShikuaiItem(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

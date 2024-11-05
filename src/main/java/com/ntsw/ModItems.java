package com.ntsw;

import com.ntsw.item.FeiJiPiao;
import com.ntsw.item.ZuanShiItem;
import com.ntsw.item.JifenItem;
import com.ntsw.item.HuangTaoGuanTou;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.ntsw.item.PddItem;

public class ModItems {
    // 注册物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);


    // 注册特殊食物
    public static final RegistryObject<Item> HuangTaoGuangTou = ITEMS.register("htgt",
            () -> new HuangTaoGuanTou(new Item.Properties()));


    public static final RegistryObject<Item> PDD = ITEMS.register("pdd", PddItem::new);

    public static final RegistryObject<Item> FeiJiPiao = ITEMS.register("feijipiao",() -> new FeiJiPiao(new Item.Properties()));

    public static final RegistryObject<Item> ZUANSHI  = ITEMS.register("zuanshi", ZuanShiItem::new);
    public static final RegistryObject<Item> JIFEN = ITEMS.register("jifen", JifenItem::new);

    public static final RegistryObject<Item> ZIMIN_SPAWN_EGG = ITEMS.register("zimin_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.ZiMin_Entity, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));

}

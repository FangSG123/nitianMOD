package com.nailong.nailong;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // 注册物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    // 注册特殊食物
    public static final RegistryObject<Item> HuangTaoGuangTou = ITEMS.register("htgt",
            () -> new HuangTaoGuanTou(new Item.Properties()));
}

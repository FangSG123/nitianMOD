package com.ntsw;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// ModContainers.java
public class ModContainers {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Main.MODID);

    public static final RegistryObject<MenuType<EnchantBookContainer>> ENCHANT_BOOK_CONTAINER = CONTAINERS.register("enchant_book_container",
            () -> IForgeMenuType.create((windowId, inv, data) -> new EnchantBookContainer(windowId, inv)));
}

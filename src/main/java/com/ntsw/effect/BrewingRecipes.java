package com.ntsw.effect;

import com.ntsw.Main;
import com.ntsw.ModPotions;
import com.ntsw.effect.FaqingBrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BrewingRecipes {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // 注册自定义的酿造配方
        BrewingRecipeRegistry.addRecipe(new FaqingBrewingRecipe());
    }
}

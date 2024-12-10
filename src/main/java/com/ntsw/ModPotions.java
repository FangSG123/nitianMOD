package com.ntsw;

import com.ntsw.Main;
import com.ntsw.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, Main.MODID);

    public static final RegistryObject<Potion> REPEL_POTION = POTIONS.register("repel_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.REPEL.get(), 3600)));
    public static final RegistryObject<Potion> FAQING_POTION = POTIONS.register("faqing_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.FAQING.get(), 3600)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}

package com.ntsw;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModDamageTypes {
    public static final DeferredRegister<DamageType> DAMAGE_TYPES =
            DeferredRegister.create(Registries.DAMAGE_TYPE, "nitian");

    public static final RegistryObject<DamageType> MUST_DIE_TOTEM_DAMAGE_TYPE = DAMAGE_TYPES.register("must_die_totem",
            () -> new DamageType(
                    "nitian.must_die_totem",
                    DamageScaling.ALWAYS,
                    0.0f
            ));

    public static void register(IEventBus eventBus) {
        DAMAGE_TYPES.register(eventBus);
        System.out.println("DamageTypes registered.");
    }
}

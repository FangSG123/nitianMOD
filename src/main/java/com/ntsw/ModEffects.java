package com.ntsw;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.ntsw.effect.DamageImmunityEffect;
import com.ntsw.effect.RepelEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Main.MODID);

    public static final RegistryObject<MobEffect> REPEL = MOB_EFFECTS.register("repel", RepelEffect::new);
    public static final RegistryObject<MobEffect> DAMAGE_IMMUNITY = MOB_EFFECTS.register("damage_immunity", DamageImmunityEffect::new);
}

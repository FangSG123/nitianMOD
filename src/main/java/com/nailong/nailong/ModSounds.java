package com.nailong.nailong;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    // 创建 DeferredRegister 实例，用于管理 SoundEvents 的注册
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "nailongmod");

    // 注册自定义音效
    public static final RegistryObject<SoundEvent> NAILONG_AMBIENT1 = SOUND_EVENTS.register("entity.nailong.ambient1",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("nailongmod", "entity.nailong.ambient1")));
    public static final RegistryObject<SoundEvent> NAILONG_AMBIENT2 = SOUND_EVENTS.register("entity.nailong.ambient2",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("nailongmod", "entity.nailong.ambient2")));

    public static final RegistryObject<SoundEvent> NAILONG_HURT = SOUND_EVENTS.register("entity.nailong.hurt",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("nailongmod", "entity.nailong.hurt")));

    public static final RegistryObject<SoundEvent> NAILONG_DEATH = SOUND_EVENTS.register("entity.nailong.death",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("nailongmod", "entity.nailong.death")));
}

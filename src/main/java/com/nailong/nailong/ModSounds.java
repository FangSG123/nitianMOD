package com.nailong.nailong;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModSounds {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);

    public static final RegistryObject<SoundEvent> NAILONG_AMBIENT1 = SOUND_EVENTS.register("entity.nailong.ambient1",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.nailong.ambient1"));
                LOGGER.info("注册音效: " + event.getLocation());
                return event;
            });
    public static final RegistryObject<SoundEvent> NAILONG_AMBIENT2 = SOUND_EVENTS.register("entity.nailong.ambient2",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.nailong.ambient2"));
                LOGGER.info("注册音效: " + event.getLocation());
                return event;
            });

    public static final RegistryObject<SoundEvent> NAILONG_HURT = SOUND_EVENTS.register("entity.nailong.hurt",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.nailong.hurt"));
                LOGGER.info("注册音效: " + event.getLocation());
                return event;
            });

    public static final RegistryObject<SoundEvent> NAILONG_DEATH = SOUND_EVENTS.register("entity.nailong.death",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.nailong.death"));
                LOGGER.info("注册音效: " + event.getLocation());
                return event;
            });
}

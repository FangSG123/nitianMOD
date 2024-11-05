package com.ntsw;

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
    public static final RegistryObject<SoundEvent> HeiManBa_DEATH = SOUND_EVENTS.register("entity.heimanba.death",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.heimanba.death"));
                LOGGER.info("注册音效: " + event.getLocation());
                return event;
            });
    public static final RegistryObject<SoundEvent> HeiManBa_Summon = SOUND_EVENTS.register("entity.heimanba.summon",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.heimanba.summon"));
                LOGGER.info("注册音效: " + event.getLocation());
                return event;
            });
    public static final RegistryObject<SoundEvent> ZiMin_Summon = SOUND_EVENTS.register("entity.zimin.summon",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.zimin.summon"));
                LOGGER.info("注册音效: " + event.getLocation());
                return event;
            });
}

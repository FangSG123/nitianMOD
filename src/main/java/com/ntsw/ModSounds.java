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
                return event;
            });

    public static final RegistryObject<SoundEvent> NAILONG_DEATH = SOUND_EVENTS.register("entity.nailong.death",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.nailong.death"));
                return event;
            });
    public static final RegistryObject<SoundEvent> HeiManBa_DEATH = SOUND_EVENTS.register("entity.heimanba.death",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.heimanba.death"));
                return event;
            });
    public static final RegistryObject<SoundEvent> HeiManBa_Summon = SOUND_EVENTS.register("entity.heimanba.summon",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.heimanba.summon"));
                return event;
            });
    public static final RegistryObject<SoundEvent> ZiMin_Summon = SOUND_EVENTS.register("entity.zimin.summon",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "entity.zimin.summon"));
                return event;
            });
    public static final RegistryObject<SoundEvent> SiRenLaugh1 = SOUND_EVENTS.register("block.laughobsidian.sirenlaugh1",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "block.laughobsidian.sirenlaugh1"));
                return event;
            });
    public static final RegistryObject<SoundEvent> SiRenLaugh2 = SOUND_EVENTS.register("block.laughobsidian.sirenlaugh2",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "block.laughobsidian.sirenlaugh2"));
                return event;
            });
    public static final RegistryObject<SoundEvent> SiRenLaugh3 = SOUND_EVENTS.register("block.laughobsidian.sirenlaugh3",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "block.laughobsidian.sirenlaugh3"));
                return event;
            });
    public static final RegistryObject<SoundEvent> SiRenLaugh4 = SOUND_EVENTS.register("block.laughobsidian.sirenlaugh4",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "block.laughobsidian.sirenlaugh4"));
                return event;
            });
    public static final RegistryObject<SoundEvent> SiRenLaugh5 = SOUND_EVENTS.register("block.laughobsidian.sirenlaugh5",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "block.laughobsidian.sirenlaugh5"));
                return event;
            });
    public static final RegistryObject<SoundEvent> SiRenLaugh6 = SOUND_EVENTS.register("block.laughobsidian.sirenlaugh6",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "block.laughobsidian.sirenlaugh6"));
                return event;
            });
    public static final RegistryObject<SoundEvent> SiRenLaugh7 = SOUND_EVENTS.register("sirenlaugh1",
            () -> {
                SoundEvent event = SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "sirenlaugh1"));
                return event;
            });

}

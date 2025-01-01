package com.ntsw.network;

import com.ntsw.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, ShieldAttackPacket.class, ShieldAttackPacket::encode, ShieldAttackPacket::decode, ShieldAttackPacket::handle);
        System.out.println("Registered ShieldAttackPacket with ID " + (id - 1));
    }
}

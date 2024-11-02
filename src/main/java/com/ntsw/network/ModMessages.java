package com.ntsw.network;

import com.ntsw.Main;
import com.ntsw.network.PacketFeiJiPiaoTotemEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkDirection;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MODID, "main"), // 使用正确的Mod ID
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.messageBuilder(PacketFeiJiPiaoTotemEffect.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PacketFeiJiPiaoTotemEffect::toBytes)
                .decoder(PacketFeiJiPiaoTotemEffect::new)
                .consumerMainThread(PacketFeiJiPiaoTotemEffect::handle)
                .add();
    }
    public static void sendToClient(Object message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}

// ModMessages.java

package com.ntsw.network;

import com.ntsw.Main;
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
            new ResourceLocation(Main.MODID, Main.MODID),
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
        INSTANCE.messageBuilder(PacketMustDieTotemEffect.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PacketMustDieTotemEffect::toBytes)
                .decoder(PacketMustDieTotemEffect::new)
                .consumerMainThread(PacketMustDieTotemEffect::handle)
                .add();
        // 注册新的网络消息
        INSTANCE.messageBuilder(PacketDaikuangtutengEffect.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PacketDaikuangtutengEffect::toBytes)
                .decoder(PacketDaikuangtutengEffect::new)
                .consumerMainThread(PacketDaikuangtutengEffect::handle)
                .add();
        INSTANCE.messageBuilder(PacketUpdateAccumulatedDamage.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PacketUpdateAccumulatedDamage::toBytes)
                .decoder(PacketUpdateAccumulatedDamage::new)
                .consumerMainThread(PacketUpdateAccumulatedDamage::handle)
                .add();
    }

    public static void sendToClient(Object message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}

// PacketUpdateAccumulatedDamage.java

package com.ntsw.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateAccumulatedDamage {
    private final double accumulatedDamage;

    public PacketUpdateAccumulatedDamage(double accumulatedDamage) {
        this.accumulatedDamage = accumulatedDamage;
    }

    public PacketUpdateAccumulatedDamage(FriendlyByteBuf buf) {
        this.accumulatedDamage = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(accumulatedDamage);
    }

    public boolean handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            // 仅在客户端处理
            if (ctx.getDirection().getReceptionSide().isClient()) {
                ClientAccumulatedDamageManager.setAccumulatedDamage(accumulatedDamage);
            }
        });
        return true;
    }

    public double getAccumulatedDamage() {
        return accumulatedDamage;
    }
}

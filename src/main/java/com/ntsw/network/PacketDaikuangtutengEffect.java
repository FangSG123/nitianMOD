// PacketDaikuangtutengEffect.java

package com.ntsw.network;

import com.ntsw.ModItems;
import com.ntsw.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDaikuangtutengEffect {
    public PacketDaikuangtutengEffect() {}

    public PacketDaikuangtutengEffect(FriendlyByteBuf buf) {
        // 无需读取任何数据
    }

    public void toBytes(FriendlyByteBuf buf) {
        // 无需写入任何数据
    }

    public boolean handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                // 使用自定义图标显示图腾动画效果
                Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(ModItems.DAIKUANGTUTENG.get()));
            }
        });
        return true;
    }
}

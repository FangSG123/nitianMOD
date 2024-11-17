package com.ntsw.network;

import com.ntsw.ModItems;
import com.ntsw.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMustDieTotemEffect {
    public PacketMustDieTotemEffect() {}

    public PacketMustDieTotemEffect(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                // 使用自定义图标显示图腾动画效果
                player.playSound(SoundEvents.TOTEM_USE, 0.8F, 1.5F);
                Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(ModItems.MUST_DIE_TOTEM.get()));
            }
        });
        return true;
    }
}

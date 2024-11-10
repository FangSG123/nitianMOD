package com.ntsw.event;

import com.ntsw.Main;
import com.ntsw.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShikuaiItemEventHandler {
    private static final int SHIFT_PRESS_COUNT = 5;

    // Listen for player tick events
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;

        if (player.level().isClientSide()) return;
        CompoundTag persistentData = player.getPersistentData();

        boolean isEating = persistentData.getBoolean("isEating");
        int shiftPressCounter = persistentData.getInt("shiftPressCounter");

        // Check if the player is holding down the Shift key
        if (player.isShiftKeyDown()) {
            shiftPressCounter++;
            if (shiftPressCounter >= SHIFT_PRESS_COUNT && isEating) {
                ItemStack shikuaiItemStack = new ItemStack(ModItems.SHIKUAI_ITEM.get());
                player.getInventory().add(shikuaiItemStack);
                shiftPressCounter = 0;
                isEating = false;
            }
        } else {
            shiftPressCounter = 0;
        }

        persistentData.putBoolean("isEating", isEating);
        persistentData.putInt("shiftPressCounter", shiftPressCounter);
    }

    @SubscribeEvent
    public static void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.level().isClientSide()) return;

        ItemStack itemStack = event.getItem();

        if (itemStack.isEdible()) {
            CompoundTag persistentData = player.getPersistentData();
            persistentData.putBoolean("isEating", true);
        }
    }
}

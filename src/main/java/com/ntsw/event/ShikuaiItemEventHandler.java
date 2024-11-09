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
    private static final int SHIFT_PRESS_COUNT = 5; // Number of times Shift needs to be pressed

    // Listen for player tick events
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return; // Only run at the end of the tick
        Player player = event.player;

        if (player.level().isClientSide()) return; // Only run on the server side

        // Retrieve per-player persistent data
        CompoundTag persistentData = player.getPersistentData();

        boolean isEating = persistentData.getBoolean("isEating");
        int shiftPressCounter = persistentData.getInt("shiftPressCounter");

        // Check if the player is holding down the Shift key
        if (player.isShiftKeyDown()) {
            shiftPressCounter++;
            if (shiftPressCounter >= SHIFT_PRESS_COUNT && isEating) {
                ItemStack shikuaiItemStack = new ItemStack(ModItems.SHIKUAI_ITEM.get());
                player.getInventory().add(shikuaiItemStack); // Add item to the player's inventory
                shiftPressCounter = 0; // Reset the counter
                isEating = false;      // Reset the eating flag
            }
        } else {
            shiftPressCounter = 0; // Reset the counter if Shift is not held down
        }

        // Save the updated data back to the player's persistent data
        persistentData.putBoolean("isEating", isEating);
        persistentData.putInt("shiftPressCounter", shiftPressCounter);
    }

    // Listen for when the player finishes using (eating) an item
    @SubscribeEvent
    public static void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.level().isClientSide()) return; // Only run on the server side

        ItemStack itemStack = event.getItem();

        // Check if the item consumed is edible
        if (itemStack.isEdible()) {
            CompoundTag persistentData = player.getPersistentData();
            persistentData.putBoolean("isEating", true); // Set the eating flag
        }
    }
}

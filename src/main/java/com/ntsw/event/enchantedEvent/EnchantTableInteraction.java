package com.ntsw.event.enchantedEvent;


import com.ntsw.EnchantBookContainer;
import com.ntsw.Main;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.SimpleMenuProvider;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantTableInteraction {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();

        // Check if the interaction is with the main hand and the player is sneaking (holding Shift)
        if (event.getHand() == InteractionHand.MAIN_HAND && player.isShiftKeyDown()) {
            BlockState blockState = level.getBlockState(event.getPos());

            // Check if the clicked block is an enchanting table
            if (blockState.getBlock() == Blocks.ENCHANTING_TABLE) {
                // On the server side, open the custom container
                if (!level.isClientSide()) {
                    player.openMenu(new SimpleMenuProvider(
                            (id, inv, p) -> new EnchantBookContainer(id, inv),
                            Component.translatable("container.enchantbook")
                    ));
                }

                // Cancel the event and set the interaction result to prevent default behavior
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}

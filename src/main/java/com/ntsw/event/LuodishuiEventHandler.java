package com.ntsw.event;


import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LuodishuiEventHandler {

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Level level = player.level();

        // Ensure this code runs only on the server side
        if (level.isClientSide()) {
            return; // Exit if running on the client side
        }

        // Debug message: Event handler is being called
        //player.sendSystemMessage(Component.literal("onLivingFall event triggered").withStyle(ChatFormatting.GREEN));

        // Check if the player is taking fall damage
        if (event.getDistance() <= 3.0F) {
            //player.sendSystemMessage(Component.literal("Fall distance too short").withStyle(ChatFormatting.YELLOW));
            return; // Falls less than or equal to 3 blocks don't cause damage
        }

        // Check if the player has an enchanted water bucket
        boolean hasEnchantedBucket = false;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.WATER_BUCKET) && stack.getEnchantmentLevel(ModEnchantments.LUODISHUI.get()) > 0) {
                hasEnchantedBucket = true;
                break;
            }
        }

        if (!hasEnchantedBucket) {
            //player.sendSystemMessage(Component.literal("No enchanted water bucket found").withStyle(ChatFormatting.RED));
            return;
        }

        // Check if water can be placed in this dimension
        if (level.dimensionType().ultraWarm()) {
            //player.sendSystemMessage(Component.literal("Cannot place water in this dimension").withStyle(ChatFormatting.RED));
            return; // Cannot place water in ultra warm dimensions like the Nether
        }

        // Get the position below the player
        BlockPos pos = player.blockPosition();

        // Check if the position can be replaced with water
        if (level.isEmptyBlock(pos) || level.getBlockState(pos).canBeReplaced()) {
            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            //player.sendSystemMessage(Component.literal("Water placed below you").withStyle(ChatFormatting.GREEN));
        } else {
            //player.sendSystemMessage(Component.literal("Cannot place water at your position").withStyle(ChatFormatting.RED));
        }
    }
}

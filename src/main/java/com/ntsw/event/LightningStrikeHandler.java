package com.ntsw.event;

import com.ntsw.ModBlocks;
import com.ntsw.ModSounds;
import net.minecraft.advancements.critereon.LightningStrikeTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LightEventListener;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LightningStrikeHandler {

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        // Check if the player is holding a feather
        if (player.getMainHandItem().getItem() == Items.FEATHER) {
            BlockPos pos = event.getPos();
            Level level = (Level) event.getLevel();
            BlockState blockState = level.getBlockState(pos);

            // Check if the broken block is Crying Obsidian
            if (blockState.is(Blocks.CRYING_OBSIDIAN)) {
                if (!level.isClientSide) { // Ensure this runs only on the server
                    ServerLevel serverLevel = (ServerLevel) level;

                    // Schedule a task to replace the block after 1 tick
                    serverLevel.getServer().execute(() -> {
                        serverLevel.setBlock(pos, ModBlocks.LAUGH_OBSIDIAN.get().defaultBlockState(), 3);
                    });

                    // Play the sound
                    level.playSound(null, pos, ModSounds.SiRenLaugh7.get(), SoundSource.MUSIC, 1.0F, 1.0F);
                }
            }
        }
    }

    // 监听闪电击中事件
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        // 检查实体是否为闪电
        if (entity instanceof LightningBolt lightningBolt) {
            BlockPos pos = new BlockPos(lightningBolt.blockPosition().getX(),lightningBolt.blockPosition().getY() - 1,lightningBolt.blockPosition().getZ());
            Level level = lightningBolt.level();
            System.out.println("pos:" + pos);

            BlockState blockState = level.getBlockState(pos);

            // 检查被劈中的块是否为哭泣黑曜石
            if (blockState.is(Blocks.CRYING_OBSIDIAN)) {
                if (!level.isClientSide()) { // 确保在服务器端执行
                    ServerLevel serverLevel = (ServerLevel) level;

                    // 替换为 LAUGH_OBSIDIAN
                    serverLevel.setBlock(pos, ModBlocks.LAUGH_OBSIDIAN.get().defaultBlockState(), 3);

                    // 播放自定义声音
                    level.playSound(null, pos, ModSounds.SiRenLaugh7.get(), SoundSource.MUSIC, 1.0F, 1.0F);
                }
            }
        }
    }

}

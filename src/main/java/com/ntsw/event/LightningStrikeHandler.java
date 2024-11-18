package com.ntsw.event;

import com.ntsw.ModBlocks;
import com.ntsw.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LightningStrikeHandler {

    @SubscribeEvent
        public static void onLightningStrike(EntityJoinLevelEvent event) {
        // 检查实体是否为闪电
        if (event.getEntity() instanceof net.minecraft.world.entity.LightningBolt lightning) {
            // 获取闪电的位置
            BlockPos pos = lightning.blockPosition();

            // 定义一个区域，检查闪电周围的方块
            int radius = 1; // 可以根据需要调整半径

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos blockPos = pos.offset(x, y, z);
                        if (event.getLevel().getBlockState(blockPos).is(Blocks.CRYING_OBSIDIAN)) {
                            // 将哭泣的黑曜石替换为 laughobsidian.json
                            event.getLevel().playSound(null, pos, ModSounds.SiRenLaugh7.get(), SoundSource.MUSIC, 1.0F, 1.0F);
                            event.getLevel().setBlockAndUpdate(blockPos,  ModBlocks.LAUGH_OBSIDIAN.get().defaultBlockState());
                        }
                    }
                }
            }
        }
    }
}

package com.ntsw.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = "nitian")
public class LaughPortalBlock extends Block {

    private static final Logger LOGGER = LoggerFactory.getLogger(LaughPortalBlock.class);

    public LaughPortalBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE)
                .strength(-1.0F, 3600000.0F) // 类似地狱门的属性
                .noCollission()
                .lightLevel(state -> 11)
                .sound(SoundType.GLASS));
    }

    // 处理实体传送逻辑
    @SubscribeEvent
    public static void onEntityInsidePortal(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();

        if (world.getBlockState(pos).getBlock() instanceof LaughPortalBlock) {
            if (!world.isClientSide() && world instanceof ServerLevel serverLevel) {
                LOGGER.info("Entity {} is being teleported by Laugh Portal at {}", entity.getName().getString(), pos);

                // 定义传送目标位置（示例：传送到世界坐标 (0, 100, 0)）
                BlockPos targetPos = new BlockPos(0, 100, 0);

                // 传送实体
                entity.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);

                LOGGER.info("Entity {} teleported to {}", entity.getName().getString(), targetPos);
            }
        }
    }
}

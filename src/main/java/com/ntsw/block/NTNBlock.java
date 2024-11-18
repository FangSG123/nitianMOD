package com.ntsw.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class NTNBlock extends Block {

        public NTNBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.TNT)
                .strength(0.0F)
                .sound(SoundType.GRASS));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 如果玩家使用打火石点燃
        if (itemStack.getItem() instanceof FlintAndSteelItem) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11); // 移除方块
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

            // 在玩家位置生成爆炸
            if (!level.isClientSide()) {
                explode(level, player.blockPosition());
            }

            // 消耗打火石耐久（非创造模式）
            if (!player.isCreative()) {
                itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }

            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    private void explode(Level level, BlockPos playerPos) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.explode(null, playerPos.getX() + 0.5, playerPos.getY() + 0.5, playerPos.getZ() + 0.5, 4.0F, Level.ExplosionInteraction.BLOCK);
        }
    }
}

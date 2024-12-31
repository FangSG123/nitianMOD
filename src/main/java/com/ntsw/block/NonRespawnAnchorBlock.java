package com.ntsw.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LogicalSidedProvider;
import org.jetbrains.annotations.NotNull;

public class NonRespawnAnchorBlock extends Block {

    public NonRespawnAnchorBlock() {// 直接使用原版重生锚的属性（硬度、抗爆等），当然你可以自行自定义
        super(BlockBehaviour.Properties.copy(Blocks.STONE));}

    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ) {
        // 只在服务端执行逻辑，客户端只负责渲染表现
        if (!level.isClientSide()) {
            // 给玩家打上一个“不可重生”的标记，可以通过玩家的持久化数据或 Capability 实现
            // 这里只演示使用 Player 自带的 persistentData NBT 来简单处理
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getPersistentData().putBoolean("NonRespawn", true);

                // 你也可以给玩家发送一条提示信息
                // serverPlayer.sendSystemMessage(Component.literal("已激活不可重生！下次死亡后您将直接进入观察者模式。"));
            }
        }
        return InteractionResult.SUCCESS;
    }
}

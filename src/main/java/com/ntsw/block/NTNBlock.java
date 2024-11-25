package com.ntsw.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NTNBlock extends Block {
    private static final Map<PrimedTnt, Player> trackingTNTMap = new HashMap<>();

    public NTNBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.TNT)
                .strength(0.0F)
                .sound(SoundType.GRASS));
        MinecraftForge.EVENT_BUS.register(this); // 注册事件
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 如果玩家使用打火石点燃
        if (itemStack.getItem() instanceof FlintAndSteelItem) {
            ignite(level, pos, player); // 点燃 TNT
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        // 检查是否收到红石信号
        if (level.hasNeighborSignal(pos)) {
            Player nearestPlayer = getNearestPlayer(level, pos); // 找到最近的玩家
            ignite(level, pos, nearestPlayer); // 点燃 TNT 并跟踪最近的玩家
        }
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        super.onBlockExploded(state, level, pos, explosion);

        // 爆炸点燃 TNT
        if (!level.isClientSide()) {
            Player nearestPlayer = getNearestPlayer(level, pos); // 找到最近的玩家
            ignite(level, pos, nearestPlayer); // 点燃 TNT 并跟踪最近的玩家
        }
    }

    private void ignite(Level level, BlockPos pos, Player player) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11); // 移除 TNT 方块
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!level.isClientSide()) {
            spawnTrackingTnt(level, pos, player);
        }
    }

    private void spawnTrackingTnt(Level level, BlockPos pos, Player player) {
        if (level instanceof ServerLevel serverLevel) {
            // 生成 TNT 实体
            PrimedTnt tnt = new PrimedTnt(serverLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
            tnt.setFuse(80); // 设置 TNT 爆炸时间（默认 80 tick）
            serverLevel.addFreshEntity(tnt);

            // 如果找到玩家，则记录 TNT 和玩家的关系
            if (player != null) {
                trackingTNTMap.put(tnt, player);
            }
        }
    }

    private Player getNearestPlayer(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null; // 仅在服务器端寻找玩家
        }

        // 在指定范围内找到最近的玩家（例如 10 格内）
        return serverLevel.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, false);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.level.isClientSide) {
            // 更新 TNT 的位置
            ServerLevel serverLevel = (ServerLevel) event.level;
            trackingTNTMap.entrySet().removeIf(entry -> {
                PrimedTnt tnt = entry.getKey();
                Player player = entry.getValue();

                // 如果 TNT 已经爆炸或消失，从列表中移除
                if (tnt.isRemoved()) {
                    return true;
                }

                // 如果玩家仍然存活，更新 TNT 的位置到玩家位置
                if (player != null && player.isAlive()) {
                    tnt.setDeltaMovement(0, 0, 0); // 停止 TNT 默认的移动
                    tnt.setPos(player.getX(), player.getY(), player.getZ()); // 跟随玩家
                }

                return false; // 保留此 TNT
            });
        }
    }
}

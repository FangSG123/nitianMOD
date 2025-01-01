package com.ntsw.event.enchantedEvent;


import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class WodishengaoEventHandler {

    // 提高挖掘速度
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        // 检查手持物品是否为镐子，并具有 wodishengao 附魔
        if (!(heldItem.getItem() instanceof PickaxeItem) || EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WODISHENGAO.get(), heldItem) <= 0) {
            return;
        }

        // 提高挖掘速度，例如提高 50%
        float newSpeed = event.getNewSpeed() * 1.5F;
        event.setNewSpeed(newSpeed);
    }

    // 修改破坏方块的逻辑
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();

        // 仅在服务器端执行
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack heldItem = player.getMainHandItem();

        // 检查玩家手持物品是否为镐子，并具有 wodishengao 附魔
        if (!(heldItem.getItem() instanceof PickaxeItem) || EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WODISHENGAO.get(), heldItem) <= 0) {
            return;
        }

        // 获取镐子的剩余耐久度
        int durability = heldItem.getMaxDamage() - heldItem.getDamageValue();

        if (durability <= 0) {
            return; // 耐久已耗尽
        }

        // 获取耐久附魔等级
        int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, heldItem);

        // 计算破坏的方块数量：耐久度 × (耐久等级 + 1)
        int blocksToBreak = durability * (unbreakingLevel + 1);

        // 消耗所有耐久度
        heldItem.hurtAndBreak((int) ((durability * (unbreakingLevel + 1.5F  ))), player, (p) -> {
            p.broadcastBreakEvent(p.getUsedItemHand());
        });

        BlockPos originPos = event.getPos();
        BlockState originState = event.getState();

        // 设置最大破坏数量（防止服务器卡顿）
        int maxBlocksToBreak = Math.min(blocksToBreak, 1000);

        // 开始破坏方块
        Set<BlockPos> brokenBlocks = new HashSet<>();
        brokenBlocks.add(originPos);

        // 使用广度优先搜索（BFS）算法，破坏相同类型的方块
        Set<BlockPos> positionsToCheck = new HashSet<>();
        positionsToCheck.add(originPos);

        while (!positionsToCheck.isEmpty() && brokenBlocks.size() < maxBlocksToBreak) {
            Set<BlockPos> nextPositions = new HashSet<>();
            for (BlockPos pos : positionsToCheck) {
                for (Direction direction : Direction.values()) {
                    BlockPos newPos = pos.relative(direction);
                    if (brokenBlocks.contains(newPos)) {
                        continue;
                    }

                    BlockState newState = level.getBlockState(newPos);
                    if (newState.getBlock() == originState.getBlock()) {
                        // 破坏方块
                        level.destroyBlock(newPos, true, player);
                        brokenBlocks.add(newPos);

                        if (brokenBlocks.size() >= maxBlocksToBreak) {
                            break;
                        }

                        nextPositions.add(newPos);
                    }
                }
                if (brokenBlocks.size() >= maxBlocksToBreak) {
                    break;
                }
            }
            positionsToCheck = nextPositions;
        }
    }
}

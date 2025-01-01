package com.ntsw.event.enchantedEvent;

import com.ntsw.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEntityTickHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemEntityTickHandler.class);

    @SubscribeEvent
    public static void onItemEntityTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Level world = event.level;
        if (world.isClientSide) return;

        // 获取所有 ItemEntity 实体，优化搜索范围
        // 假设只检测 ItemEntity 周围 30 个方块内的掉落物
        double rangeLimit = 30;
        AABB globalSearchArea = new AABB(
                event.level.getMinBuildHeight(),
                event.level.getMinBuildHeight(),
                event.level.getMinBuildHeight(),
                event.level.getMaxBuildHeight(),
                event.level.getMaxBuildHeight(),
                event.level.getMaxBuildHeight()
        ).inflate(rangeLimit);

        for (ItemEntity itemEntity : world.getEntitiesOfClass(ItemEntity.class, globalSearchArea)) {
            if (itemEntity.getPersistentData().getBoolean("hasGoujitiaoqiang")) {
                ItemStack stack = itemEntity.getItem();
                boolean isPickaxe = stack.getItem() instanceof PickaxeItem;
                boolean isSword = stack.getItem() instanceof SwordItem;

                // 获取镐子或剑的材质以决定破坏范围或伤害
                int range = getRangeFromMaterial(stack);

                BlockPos pos = itemEntity.blockPosition();
                BlockPos digPos = pos.below();
                if (stack.getItem() == Items.TNT) {
                    explodeTNT(world, itemEntity);
                    continue; // 处理完 TNT 后，跳过后续逻辑
                }
                // 处理破坏方块的逻辑（仅适用于镐子）
                if (isPickaxe) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            for (int dy = -1; dy <= 1; dy++) { // 可根据需要调整 Y 轴范围
                                BlockPos currentPos = pos.offset(dx, dy, dz);
                                BlockState state = world.getBlockState(currentPos);
                                if (state.isAir()) continue;

                                // 检查是否为仙人掌
                                if (state.getBlock() instanceof CactusBlock) {
                                    // 破坏周围方块
                                    destroySurroundingBlocks(world, currentPos, range);
                                }
                            }
                        }
                    }
                    // 破坏镐子下方的方块并掉落物品
                    BlockState state = world.getBlockState(digPos);
                    Block block = state.getBlock();
                    if (!(block == Blocks.BEDROCK)) {
                        world.destroyBlock(digPos, true); // 修改为 true
                    }
                    reduceDurability(world, itemEntity, stack);
                }
                // 处理剑的碰撞伤害逻辑
                if (isSword) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            for (int dy = -1; dy <= 1; dy++) { // 可根据需要调整 Y 轴范围
                                BlockPos currentPos = pos.offset(dx, dy, dz);
                                BlockState state = world.getBlockState(currentPos);
                                if (state.isAir()) continue;

                                // 检查是否为仙人掌
                                if (state.getBlock() instanceof CactusBlock) {
                                    if (itemEntity.getOwner() != null) {
                                        world.destroyBlock(currentPos, true); // 修改为 true
                                        itemEntity.getOwner().hurt(itemEntity.getOwner().damageSources().generic(), range);
                                    }
                                }

                            }
                        }
                    }
                    // 定义一个碰撞半径，确保检测范围不太大以优化性能
                    double collisionRadius = 1.0;
                    // 获取周围的生物实体
                    AABB entitySearchArea = itemEntity.getBoundingBox().inflate(collisionRadius);
                    for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, entitySearchArea,
                            entity -> entity != itemEntity.getOwner())) { // 排除掉落物的拥有者
                        if (target.isAlive()) {
                            // 施加伤害
                            target.hurt(target.damageSources().generic(), 4.0f); // 4.0f 是伤害量，可根据需求调整
                            LOGGER.info("Goujitiaoqiang sword hit entity: {}", target.getName().getString());
                            // 减少耐久
                            reduceDurability(world, itemEntity, stack);
                            }
                        }
                    }
                }
            }
        }

    private static int getRangeFromMaterial(ItemStack stack) {
        if (stack.getItem() instanceof PickaxeItem pickaxe ) {
            Tier tier = pickaxe.getTier();
            if (tier == Tiers.WOOD) {
                return 3; // 5x5
            } else if (tier == Tiers.STONE) {
                return 4; // 7x7
            } else if (tier == Tiers.IRON) {
                return 5; // 9x9
            } else if (tier == Tiers.DIAMOND) {
                return 8; // 15x15
            } else if (tier == Tiers.NETHERITE) {
                return 13; // 24x24
            }
        } else if (stack.getItem() instanceof SwordItem sword) {
            Tier tier = sword.getTier();
            // 可以为剑定义不同的逻辑或范围，当前我们不使用范围，直接返回一个固定值
            if (tier == Tiers.WOOD) {
                return 8; // 5x5
            } else if (tier == Tiers.STONE) {
                return 14; // 7x7
            } else if (tier == Tiers.IRON) {
                return 20; // 9x9
            } else if (tier == Tiers.DIAMOND) {
                return 30; // 15x15
            } else if (tier == Tiers.NETHERITE) {
                return 40; // 24x24
            }
        }
        return 2;
    }

    private static void destroySurroundingBlocks(Level world, BlockPos center, int range) {
        for (int dx = -(range-1); dx <= (range-1); dx++) {
            for (int dz = -(range-1); dz <= (range-1); dz++) {
                for (int dy = -range; dy <= (range+2); dy++) { // 可根据需要调整 Y 轴范围
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    System.out.println("block" +block);
                    if (!state.isAir() && !(block == Blocks.BEDROCK)) {
                        world.destroyBlock(pos, true); // 修改为 true
                    }
                }
            }
        }
    }

    private static void reduceDurability(Level world, ItemEntity itemEntity, ItemStack stack) {
        stack.hurt(1, world.getRandom(), null);
        if (isBroken(stack)) {
            itemEntity.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private static boolean isBroken(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage();
    }

    private static void explodeTNT(Level world, ItemEntity itemEntity) {
        double x = itemEntity.getX();
        double y = itemEntity.getY();
        double z = itemEntity.getZ();
        float explosionPower = 4.0f; // TNT 的默认爆炸强度
        world.explode(null, x, y, z, explosionPower, Level.ExplosionInteraction.BLOCK);
        LOGGER.info("TNT exploded at ({}, {}, {})", x, y, z);
        // 移除爆炸的 TNT 实体
        itemEntity.remove(Entity.RemovalReason.DISCARDED);
    }
}

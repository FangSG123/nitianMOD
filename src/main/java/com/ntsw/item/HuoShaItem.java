package com.ntsw.item;

import com.ntsw.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.registries.RegistryObject;

public class HuoShaItem extends SwordItem {

    private static final Logger LOGGER = LogManager.getLogger();

    public HuoShaItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Item.Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (attacker instanceof Player player && !player.level().isClientSide()) {
            // 检查目标是否为玩家且拥有扇
            if (target instanceof Player targetPlayer && hasItem(player, targetPlayer, ModItems.SHAN)) {
                // 移除目标玩家的一个 Shan（扇）
                removeItem(targetPlayer, ModItems.SHAN);

                // 发送提示消息给攻击者
                player.displayClientMessage(Component.translatable("message.nitian.huosha.remove_shan"), true);

                LOGGER.info("移除了目标玩家的扇，不造成伤害和生成岩浆。");
                return result; // 不继续后续逻辑
            }

            // 如果目标不是玩家，或玩家没有扇，则继续执行火杀的特殊效果
            if (hasItem(player, ModItems.HUOSHA)) {
                // 消耗一个火杀
                consumeItem(player, ModItems.HUOSHA);

                // 生成岩浆块
                spawnLavaAtTarget(player.level(), target.blockPosition());

                // 发送提示消息给攻击者
                player.displayClientMessage(Component.translatable("message.nitian.huosha.spawn_lava"), true);

                LOGGER.info("火杀被使用，岩浆块已生成在 " + target.blockPosition());
            }
        }

        return result;
    }

    /**
     * 检查玩家的特定物品是否存在于主物品栏或副手槽
     *
     * @param player 玩家对象
     * @param item   要检查的物品
     * @return 如果存在，返回 true；否则返回 false
     */
    private boolean hasItem(Player player, RegistryObject<Item> item) {
        // 检查主物品栏
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() == item.get()) {
                return true;
            }
        }
        // 检查副手槽
        for (ItemStack itemStack : player.getInventory().offhand) {
            if (itemStack.getItem() == item.get()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查攻击者是否有特定物品（如火杀）
     *
     * @param player 玩家对象
     * @param item   要检查的物品
     * @return 如果存在，返回 true；否则返回 false
     */
    private boolean hasItem(Player player, Player targetPlayer, RegistryObject<Item> item) {
        // 检查主物品栏
        for (ItemStack itemStack : targetPlayer.getInventory().items) {
            if (itemStack.getItem() == item.get()) {
                return true;
            }
        }
        // 检查盔甲槽
        for (ItemStack itemStack : targetPlayer.getInventory().armor) {
            if (itemStack.getItem() == item.get()) {
                return true;
            }
        }
        // 检查副手槽
        for (ItemStack itemStack : targetPlayer.getInventory().offhand) {
            if (itemStack.getItem() == item.get()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从玩家的背包或副手槽中移除一个特定物品
     *
     * @param player 玩家对象
     * @param item   要移除的物品
     */
    private void removeItem(Player player, RegistryObject<Item> item) {
        // 从主物品栏移除
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() == item.get()) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.getInventory().removeItem(itemStack);
                }
                return;
            }
        }
        // 从盔甲槽移除
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            ItemStack itemStack = player.getInventory().armor.get(i);
            if (itemStack.getItem() == item.get()) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.getInventory().armor.set(i, ItemStack.EMPTY);
                }
                return;
            }
        }
        // 从副手槽移除
        for (int i = 0; i < player.getInventory().offhand.size(); i++) {
            ItemStack itemStack = player.getInventory().offhand.get(i);
            if (itemStack.getItem() == item.get()) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.getInventory().offhand.set(i, ItemStack.EMPTY);
                }
                return;
            }
        }
    }

    /**
     * 从玩家的背包或副手槽中消耗一个特定物品
     *
     * @param player 玩家对象
     * @param item   要消耗的物品
     */
    private void consumeItem(Player player, RegistryObject<Item> item) {
        // 从主物品栏消耗
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() == item.get()) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.getInventory().removeItem(itemStack);
                }
                return;
            }
        }
        // 从副手槽消耗
        for (ItemStack itemStack : player.getInventory().offhand) {
            if (itemStack.getItem() == item.get()) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.getInventory().offhand.set(player.getInventory().offhand.indexOf(itemStack), ItemStack.EMPTY);
                }
                return;
            }
        }
    }

    /**
     * 在目标位置生成岩浆块，并添加粒子和声音效果
     *
     * @param level 游戏世界
     * @param pos   目标位置
     */
    private void spawnLavaAtTarget(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            // 在目标位置的正上方生成岩浆块
            BlockPos lavaPos = pos.above();
            if (serverLevel.getBlockState(lavaPos).isAir()) {
                serverLevel.setBlock(lavaPos, Blocks.LAVA.defaultBlockState(), 3);
                // 添加粒子效果
                serverLevel.sendParticles(ParticleTypes.LAVA, lavaPos.getX() + 0.5, lavaPos.getY(), lavaPos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.01);
                // 播放声音效果
                serverLevel.playSound(null, lavaPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                LOGGER.warn("无法在位置 " + lavaPos + " 生成岩浆块，位置已被占用。");
            }
        }
    }
}

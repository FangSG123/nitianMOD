package com.ntsw.item;

import com.ntsw.ModItems;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HuoShaItem extends SwordItem {

    private static final Logger LOGGER = LogManager.getLogger();

    public HuoShaItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Item.Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (attacker instanceof Player player && !player.level().isClientSide()) {
            if (hasHuoSha(player)) {
                // 消耗一个火杀
                consumeHuoSha(player);

                // 生成岩浆块
                spawnLavaAtTarget(player.level(), target.blockPosition());

                LOGGER.info("火杀被使用，岩浆块已生成在 " + target.blockPosition());
            }
        }

        return result;
    }

    /**
     * 检查玩家背包中是否存在火杀
     */
    private boolean hasHuoSha(Player player) {
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() == ModItems.HUOSHA.get()) {
                return true;
            }
        }
        // 也检查副手
        for (ItemStack itemStack : player.getInventory().offhand) {
            if (itemStack.getItem() == ModItems.HUOSHA.get()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 消耗玩家背包中的一个火杀
     */
    private void consumeHuoSha(Player player) {
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() == ModItems.HUOSHA.get()) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.getInventory().removeItem(itemStack);
                }
                return;
            }
        }
        // 检查副手
        for (ItemStack itemStack : player.getInventory().offhand) {
            if (itemStack.getItem() == ModItems.HUOSHA.get()) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.getInventory().offhand.set(player.getInventory().offhand.indexOf(itemStack), ItemStack.EMPTY);
                }
                return;
            }
        }
    }

    /**
     * 在目标位置生成岩浆块
     */
    private void spawnLavaAtTarget(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            // 在目标位置上方生成岩浆块
            BlockPos lavaPos = pos.above();
            serverLevel.setBlock(lavaPos, Blocks.LAVA.defaultBlockState(), 3);
        }
    }
}

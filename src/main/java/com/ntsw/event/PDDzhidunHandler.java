package com.ntsw.event;

import com.ntsw.Main;
import com.ntsw.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class PDDzhidunHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerHurtWithPDDzhidun(LivingHurtEvent event) {
        LOGGER.info("LivingHurtEvent triggered.");
        if (!(event.getEntity() instanceof Player player)) {
            LOGGER.info("Entity is not a player. Exiting.");
            return;
        }

        // 检查玩家是否正在使用 PDDzhidun
        ItemStack activeItem = player.getUseItem(); // 当前正在使用的物品
        if (activeItem.getItem() != ModItems.PDD_ZHIDUN.get()) {
            LOGGER.info("Player is not using PDDzhidun. Exiting.");
            return;
        }

        LOGGER.info("PDDzhidun 被使用，取消伤害。");

        // 取消伤害，玩家在防御状态下无敌
        event.setCanceled(true);

        // 处理耐久度损耗
        updateDurability(activeItem, player);
    }

    private static void updateDurability(ItemStack stack, Player player) {
        int currentDamage = stack.getDamageValue();
        int maxDurability = stack.getMaxDamage();
        int remainingDurability = maxDurability - currentDamage;

        // 获取或创建 NBT 标签以存储防御次数
        CompoundTag nbt = stack.getOrCreateTag();
        int defenseCount = nbt.getInt("DefenseCount");

        // 增加防御次数
        defenseCount++;
        nbt.putInt("DefenseCount", defenseCount);
        stack.setTag(nbt);

        // 根据防御次数和剩余耐久度计算耐久度损失百分比
        float damagePercentage;

        if (defenseCount <= 5) {
            damagePercentage = 0.18f; // 前5次防御，每次损失18%
        } else if (remainingDurability < 100) {
            damagePercentage = 0.03f; // 耐久低于100，每次损失3%
        } else if (remainingDurability < 500) {
            damagePercentage = 0.05f; // 耐久低于500，每次损失5%
        } else if (remainingDurability < 7000) {
            damagePercentage = 0.10f; // 耐久低于7000，每次损失10%
        } else {
            damagePercentage = 0.18f; // 其他情况，每次损失18%
        }

        // 计算本次防御需要损失的耐久度
        int damageToReduce;

        if (remainingDurability < 500) {
            damageToReduce = 50; // 耐久低于500，每次损失50点耐久
        } else {
            damageToReduce = Math.max((int) (remainingDurability * damagePercentage), 1); // 确保至少损失1点
        }

        LOGGER.info("Defense Count: " + defenseCount + ", Remaining Durability: " + remainingDurability + ", Damage to Reduce: " + damageToReduce);

        // 应用耐久度损失
        stack.hurtAndBreak(damageToReduce, player, (entity) -> {
            entity.broadcastBreakEvent(player.getUsedItemHand());
        });

        // 检查耐久是否耗尽
        if (stack.getDamageValue() >= stack.getMaxDamage()) { // Use isBroken() to check if the item is broken
            LOGGER.info("PDDzhidun耐久耗尽，生成爆炸并丢出物品。");
            // 生成爆炸
            generateExplosion(player.level(), player.blockPosition());

            // 丢出物品
            dropItem(stack, player);

            // 移除物品
            player.setItemInHand(player.getUsedItemHand(), ItemStack.EMPTY);
        }

        // 重置防御次数
        if (defenseCount >= 5 || remainingDurability < 100) {
            nbt.putInt("DefenseCount", 0);
            stack.setTag(nbt);
            LOGGER.info("Defense count reset to 0.");
        }
    }

    private static void generateExplosion(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            LOGGER.info("Generating explosion at " + pos);
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, Level.ExplosionInteraction.BLOCK);
            LOGGER.info("Explosion generated.");
        }
    }

    private static void dropItem(ItemStack stack, Player player) {
        if (!player.level().isClientSide()) {
            LOGGER.info("Dropping item: " + stack);
            ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
            player.level().addFreshEntity(itemEntity);
            LOGGER.info("Item dropped.");
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        LOGGER.info("ItemTossEvent triggered.");
        ItemStack stack = event.getEntity().getItem();
        if (stack.getItem() == ModItems.PDD_ZHIDUN.get()) {
            LOGGER.info("Dropped item is PDDzhidun.");
                generateExplosion(event.getPlayer().level(), event.getEntity().blockPosition());
                event.getEntity().remove(Entity.RemovalReason.DISCARDED);
        }
    }

}

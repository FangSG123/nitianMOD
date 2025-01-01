package com.ntsw.event.enchantedEvent;

import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;  // 导入Level类
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class EnchantmentEventHandler {
    @SubscribeEvent
    public static void onBlockBreak(BreakEvent event) {
        Player player = event.getPlayer();
        BlockState blockState = event.getState();
        Block block = blockState.getBlock();
        ItemStack tool = player.getMainHandItem();

        // 检查是否装备了附魔
        if (tool.getEnchantmentLevel(ModEnchantments.FUSHIYUN.get()) > 0) {
            // 根据挖掘的矿物替换掉落物
            Random random = new Random();

            if (block == Blocks.ANCIENT_DEBRIS) {
                // 替换掉落物为钻石
                event.setExpToDrop(20); // 可以取消经验掉落
                event.setCanceled(true); // 阻止原本的掉落
                Level level = (Level) event.getLevel();  // 转换为Level类型
                Block.popResource(level, event.getPos(), new ItemStack(Items.DIAMOND));
                level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 3); // 销毁方块
            } else if (block == Blocks.DIAMOND_ORE) {
                // 替换掉落物为黄金
                event.setExpToDrop(15);
                event.setCanceled(true);
                Level level = (Level) event.getLevel();
                Block.popResource(level, event.getPos(), new ItemStack(Items.GOLD_INGOT));
                level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 3); // 销毁方块
            } else if (block == Blocks.GOLD_ORE) {
                // 替换掉落物为铁矿
                event.setExpToDrop(10);
                event.setCanceled(true);
                Level level = (Level) event.getLevel();
                Block.popResource(level, event.getPos(), new ItemStack(Items.IRON_ORE));
                level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 3); // 销毁方块
            } else if (block == Blocks.IRON_ORE) {
                // 替换掉落物为煤炭
                event.setExpToDrop(5);
                event.setCanceled(true);
                Level level = (Level) event.getLevel();
                Block.popResource(level, event.getPos(), new ItemStack(Items.COAL));
                level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 3); // 销毁方块
            }
        }
    }

    @SubscribeEvent
    public static void onExperienceGain(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        // 检查主手是否装备了 FuJingYanXiuBu 附魔
        if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.FUJINGYANXIUBU.get(), player) > 0) {
            // 获取该物品的耐久
            int durability = heldItem.getMaxDamage() - heldItem.getDamageValue();

            if (durability > 0) {
                // 如果物品有耐久，扣除物品的耐久
                heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                // 移除经验球
                event.getOrb().remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
                // 打印调试信息
                event.setCanceled(true);
                System.out.println("Experience collected, durability decreased on item, XP orb removed.");
            }
        }
    }
}

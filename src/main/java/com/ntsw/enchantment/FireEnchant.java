package com.ntsw.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FireEnchant extends Enchantment {

    private final Map<BlockPos, Integer> fireTimers = new HashMap<>();  // 追踪每个火焰的存在时间
    private final Map<UUID, Integer> fireGenerationTimers = new HashMap<>();  // 控制每 10 个 tick 生成一次火焰

    public FireEnchant() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        MinecraftForge.EVENT_BUS.register(this);  // 注册事件总线
    }
    @Override
    public boolean isTreasureOnly() {
        return false; // 允许在附魔台获得该附魔
    }
    @Override
    public boolean isDiscoverable() {
        return true; // 允许该附魔在附魔表中出现
    }
    @Override
    public int getMaxLevel() {
        return 1; // 附魔只需要一个等级
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 允许附魔在头盔、护腿和鞋子上
        return stack.getItem() instanceof ArmorItem && (((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.HEAD ||
                ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.LEGS || ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.FEET);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();

        // 控制火焰生成频率，每 10 个 tick 生成一个火焰
        fireGenerationTimers.putIfAbsent(player.getUUID(), 0);
        int generationTimer = fireGenerationTimers.get(player.getUUID());

        // 检查玩家是否佩戴了带有火焰附加附魔的头盔、护腿或鞋子
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        boolean isWearingFireEnchantedHelmet = helmet.isEnchanted() && helmet.getEnchantmentTags().toString().contains("fire_enchant");
        boolean isWearingFireEnchantedLeggings = leggings.isEnchanted() && leggings.getEnchantmentTags().toString().contains("fire_enchant");
        boolean isWearingFireEnchantedBoots = boots.isEnchanted() && boots.getEnchantmentTags().toString().contains("fire_enchant");

        // 如果护腿有火焰附魔，点燃玩家
        if (isWearingFireEnchantedLeggings) {
            player.setSecondsOnFire(5);  // 玩家佩戴时着火
        }

        // 每 10 个 tick 执行一次火焰生成逻辑（针对头盔和鞋子）
        if (generationTimer >= 10) {
            if (isWearingFireEnchantedHelmet) {
                BlockPos posAboveHead = player.blockPosition().above(2);
                BlockState blockAboveHead = level.getBlockState(posAboveHead);

                // 如果头顶的方块是空气，则生成火焰
                if (blockAboveHead.isAir()) {
                    level.setBlockAndUpdate(posAboveHead, Blocks.FIRE.defaultBlockState());
                    fireTimers.put(posAboveHead, 0);  // 追踪该火焰，并将其计时器设为 0
                }

                // 如果头顶的方块是 TNT，则点燃 TNT
                else if (blockAboveHead.is(Blocks.TNT)) {
                    if (!level.isClientSide) {
                        level.removeBlock(posAboveHead, false);  // 移除原来的 TNT 方块
                        PrimedTnt tntEntity = new PrimedTnt(level, posAboveHead.getX() + 0.5, posAboveHead.getY(), posAboveHead.getZ() + 0.5, player);
                        level.addFreshEntity(tntEntity);  // 生成已点燃的 TNT 实体
                    }
                }
            }

            // 针对鞋子的效果，生成脚下的火焰
            if (isWearingFireEnchantedBoots) {
                BlockPos posUnderFeet = player.blockPosition().below(0);
                BlockState blockUnderFeet = level.getBlockState(posUnderFeet);

                // 如果脚下的方块是空气，则生成火焰
                if (blockUnderFeet.isAir()) {
                    level.setBlockAndUpdate(posUnderFeet, Blocks.FIRE.defaultBlockState());
                    fireTimers.put(posUnderFeet, 0);  // 追踪该火焰，并将其计时器设为 0
                }
            }

            // 重置生成计时器
            fireGenerationTimers.put(player.getUUID(), 0);
        } else {
            // 计时器加一
            fireGenerationTimers.put(player.getUUID(), generationTimer + 1);
        }

        // 更新所有火焰的计时器
        fireTimers.entrySet().removeIf(entry -> {
            BlockPos firePos = entry.getKey();
            int fireTime = entry.getValue();
            if (fireTime >= 40) {
                // 火焰存在 40 个 tick 后移除
                if (level.getBlockState(firePos).is(Blocks.FIRE)) {
                    level.setBlockAndUpdate(firePos, Blocks.AIR.defaultBlockState());
                }
                return true;  // 移除该火焰的追踪
            } else {
                // 计时器加一
                entry.setValue(fireTime + 1);
                return false;
            }
        });
    }
}

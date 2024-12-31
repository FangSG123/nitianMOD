package com.ntsw.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义武器类：埃斯库多之剑
 */
public class EscudoSwordItem extends Item {
    // NBT 标签常量
    private static final String TAG_DAMAGE = "escudo_damage";
    private static final String TAG_AUTO_KILL_LIST = "auto_kill_list";
    private static final String TAG_KILL_COUNT = "kill_count";

    // 设定最高伤害上限
    private static final int MAX_DAMAGE = 50;

    public EscudoSwordItem(Properties properties) {
        super(properties);
    }

    /**
     * 获取当前武器的伤害值
     */
    public int getCurrentDamage(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(TAG_DAMAGE);
    }

    /**
     * 设置当前武器的伤害值
     */
    public void setCurrentDamage(ItemStack stack, int damage) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(TAG_DAMAGE, Math.min(damage, MAX_DAMAGE));
    }

    /**
     * 判断武器是否达到最大伤害
     */
    public boolean isMaxDamageReached(ItemStack stack) {
        return getCurrentDamage(stack) >= MAX_DAMAGE;
    }

    /**
     * 将某种生物添加到自动击杀列表中
     */
    public void addEntityToAutoKillList(ItemStack stack, String entityKey) {
        CompoundTag tag = stack.getOrCreateTag();
        String currentList = tag.getString(TAG_AUTO_KILL_LIST);

        // 检查是否已经存在该生物类型
        if (!Arrays.asList(currentList.split(";")).contains(entityKey)) {
            if (currentList.isEmpty()) {
                currentList = entityKey;
            } else {
                currentList = currentList + ";" + entityKey;
            }
            tag.putString(TAG_AUTO_KILL_LIST, currentList);
        }
    }

    /**
     * 判断某种生物是否在自动击杀列表中
     */
    public boolean isInAutoKillList(ItemStack stack, String entityKey) {
        CompoundTag tag = stack.getOrCreateTag();
        String currentList = tag.getString(TAG_AUTO_KILL_LIST);
        if (currentList == null || currentList.isEmpty()) {
            return false;
        }
        String[] split = currentList.split(";");
        for (String s : split) {
            if (s.equals(entityKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 增加某种生物的击杀数量
     */
    public void incrementKillCount(ItemStack stack, String entityKey) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag killCountTag = tag.getCompound(TAG_KILL_COUNT);
        int count = killCountTag.getInt(entityKey) + 1;
        killCountTag.putInt(entityKey, count);
        tag.put(TAG_KILL_COUNT, killCountTag);
    }

    /**
     * 获取某种生物的击杀数量
     */
    public int getKillCount(ItemStack stack, String entityKey) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_KILL_COUNT)) {
            return 0;
        }
        CompoundTag killCountTag = tag.getCompound(TAG_KILL_COUNT);
        return killCountTag.getInt(entityKey);
    }

    /**
     * 获取总击杀数量
     */
    public int getTotalKillCount(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_KILL_COUNT)) {
            return 0;
        }
        CompoundTag killCountTag = tag.getCompound(TAG_KILL_COUNT);
        return killCountTag.getAllKeys().stream().mapToInt(killCountTag::getInt).sum();
    }

    /**
     * 获取自动击杀的生物种类数量
     */
    public int getAutoKillSpeciesCount(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        String currentList = tag.getString(TAG_AUTO_KILL_LIST);
        if (currentList == null || currentList.isEmpty()) {
            return 0;
        }
        String[] speciesArray = currentList.split(";");
        return speciesArray.length;
    }

    /**
     * 动态修改武器的显示名称，根据自动击杀生物种类数量
     */
    @Override
    public Component getName(ItemStack stack) {
        // 保留自定义重命名（例如玩家用铁砧改名）
        if (stack.hasCustomHoverName()) {
            return stack.getHoverName();
        }

        // 获取自动击杀生物种类数量
        int speciesCount = getAutoKillSpeciesCount(stack);

        // 根据数量返回不同的名称
        if (speciesCount >= 10) {
            return Component.literal("埃斯库多之剑 - 涅槃寂静模式");
        } else if (speciesCount >= 8) {
            return Component.literal("埃斯库多之剑 - 阿摩罗模式");
        } else if (speciesCount >= 5) {
            return Component.literal("埃斯库多之剑 - 刹那模式");
        } else if (speciesCount >= 3) {
            return Component.literal("埃斯库多之剑 - 逡巡模式");
        } else if (speciesCount >= 1) {
            return Component.literal("埃斯库多之剑 - 尘模式");
        } else {
            // 如果还没有进入任何模式，就返回原名称（语言文件或直接写死）
            return super.getName(stack);
        }
    }

    /**
     * 在物品描述中添加自定义信息：当前伤害、总击杀数、自动击杀种类数
     */
    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            net.minecraft.world.item.TooltipFlag flag
    ) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 显示当前武器伤害
        int currentDmg = getCurrentDamage(stack);
        tooltip.add(Component.literal("当前伤害: " + currentDmg));

        // 显示总击杀数量
        int totalKills = getTotalKillCount(stack);
        tooltip.add(Component.literal("总击杀数: " + totalKills));

        // 显示自动击杀的生物种类数
        int speciesCount = getAutoKillSpeciesCount(stack);
        tooltip.add(Component.literal("灭绝数量: " + speciesCount));
    }
}

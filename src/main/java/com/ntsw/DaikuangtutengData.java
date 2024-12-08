// DaikuangtutengData.java

package com.ntsw;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class DaikuangtutengData {
    private static final String TAG_KEY_DAMAGE = "daikuangtuteng_damage";
    private static final String TAG_KEY_FLAG = "daikuangtuteng_applying_damage";

    // 获取玩家的累计伤害
    public static double getAccumulatedDamage(Player player) {
        CompoundTag tag = player.getPersistentData();
        return tag.getDouble(TAG_KEY_DAMAGE);
    }

    // 设置玩家的累计伤害
    public static void setAccumulatedDamage(Player player, double damage) {
        CompoundTag tag = player.getPersistentData();
        tag.putDouble(TAG_KEY_DAMAGE, damage);
    }

    // 增加玩家的累计伤害
    public static void addAccumulatedDamage(Player player, double damage) {
        double current = getAccumulatedDamage(player);
        setAccumulatedDamage(player, current + damage);
    }

    // 减少玩家的累计伤害
    public static void subtractAccumulatedDamage(Player player, double damage) {
        double current = getAccumulatedDamage(player);
        double newDamage = Math.max(current - damage, 0);
        setAccumulatedDamage(player, newDamage);
    }

    // 重置玩家的累计伤害
    public static void resetAccumulatedDamage(Player player) {
        setAccumulatedDamage(player, 0);
    }

    // 获取应用伤害的标志位
    public static boolean isApplyingDamage(Player player) {
        CompoundTag tag = player.getPersistentData();
        return tag.getBoolean(TAG_KEY_FLAG);
    }

    // 设置应用伤害的标志位
    public static void setApplyingDamage(Player player, boolean value) {
        CompoundTag tag = player.getPersistentData();
        tag.putBoolean(TAG_KEY_FLAG, value);
    }
}

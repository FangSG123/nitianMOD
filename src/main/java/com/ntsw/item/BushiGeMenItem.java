package com.ntsw.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BushiGeMenItem extends BowItem {

    public BushiGeMenItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int timeCharged) {
        if (!(shooter instanceof Player player)) return;

        // 计算蓄力时间
        int chargeTime = this.getUseDuration(stack) - timeCharged;
        float power = getPowerForTime(chargeTime);
        if (power < 0.1F) return; // 蓄力过短，不进行发射

        // 检测武器上的力量附魔等级
        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);

        // 根据附魔等级调整发射速度（每级增加 0.5 倍速度）
        float speedMultiplier = 1.0F + powerLevel * 0.5F;

        if (!level.isClientSide) {
            // 获取玩家当前的方向并施加冲击力
            Vec3 lookDirection = player.getLookAngle().normalize();
            Vec3 launchVelocity = lookDirection.scale(3.0F * power * speedMultiplier); // 根据蓄力和附魔调整速度

            // 设置玩家的运动速度
            player.setDeltaMovement(launchVelocity);
            player.hurtMarked = true; // 更新运动状态以确保同步

            // 检测火矢附魔，如果存在则点燃玩家
            int flameLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack);
            if (flameLevel > 0) {
                player.setSecondsOnFire(5); // 点燃玩家 5 秒
            }
        }

        // 播放发射音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // 损耗武器的耐久度
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand); // 开始蓄力
        return InteractionResultHolder.consume(itemstack);
    }

    public static float getPowerForTime(int chargeTime) {
        float f = (float) chargeTime / 20.0F; // 蓄力时间最大20 ticks
        f = (f * f + f * 2.0F) / 3.0F; // 模仿弓箭的蓄力公式
        if (f > 1.0F) {
            f = 1.0F; // 最大值为1.0
        }
        return f;
    }
}

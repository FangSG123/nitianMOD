package com.ntsw.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BushiGeMenItem extends BowItem {

    private boolean isLaunched = false; // 标志玩家是否被发射
    private boolean hasTNT = false;    // 标志玩家是否携带 TNT

    public BushiGeMenItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity shooter, int timeCharged) {
        if (!(shooter instanceof Player player)) return;

        // 计算蓄力时间
        int chargeTime = this.getUseDuration(stack) - timeCharged;
        float power = getPowerForTime(chargeTime);
        if (power < 0.1F) return; // 蓄力过短，不进行发射

        // 检测副手是否有 TNT
        ItemStack offhandItem = player.getOffhandItem();
        if (offhandItem.getItem() == net.minecraft.world.item.Items.TNT) {
            hasTNT = true;
            offhandItem.shrink(1); // 减少 TNT 数量
        } else {
            hasTNT = false;
        }

        // 检测武器上的力量附魔等级
        int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, player);

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
            int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, player);
            if (flameLevel > 0) {
                player.setSecondsOnFire(5); // 点燃玩家 5 秒
            }

            // 标志玩家已被发射
            isLaunched = true;
        }

        // 播放发射音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // 损耗武器的耐久度
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, net.minecraft.world.entity.@NotNull Entity entity, int slot, boolean isSelected) {
        if (!(entity instanceof Player player)) return;

        if (isLaunched && !level.isClientSide) {
            // 检测玩家是否与生物碰撞
            Vec3 position = player.position();
            AABB collisionBox = player.getBoundingBox().inflate(0.5); // 碰撞检测范围

            // 检测与生物的碰撞
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, collisionBox, e -> e instanceof Mob);
            if (!entities.isEmpty()) {
                // 对第一个碰撞到的生物造成伤害
                LivingEntity target = entities.get(0);
                double speed = player.getDeltaMovement().length(); // 玩家速度
                float damage = (float) (speed * 5.0); // 伤害值
                if(speed > 0.5D)
                {
                    target.hurt(player.damageSources().playerAttack(player), damage);

                    // 如果携带 TNT，触发爆炸
                    if (hasTNT) {
                        level.explode(null, position.x, position.y, position.z, 3.0F, Level.ExplosionInteraction.BLOCK);
                    }

                    if (player.isOnFire()) {
                        target.setSecondsOnFire(5); // 点燃生物 5 秒
                    }

                }
                // 停止玩家运动
                player.setDeltaMovement(Vec3.ZERO);
                player.hurtMarked = true;

                isLaunched = false;
                hasTNT = false;
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
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

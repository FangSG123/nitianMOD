package com.ntsw.enchantment;

import com.ntsw.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FiniteEnchant extends Enchantment {

    private static final Random random = new Random();
    private final Map<UUID, Player> trackedArrows = new HashMap<>();
    private final Map<UUID, Integer> arrowLifetimes = new HashMap<>();

    public FiniteEnchant() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        MinecraftForge.EVENT_BUS.register(this);  // 注册事件总线
    }
    @Override
    public boolean isCurse() {
        return true; // 将附魔标记为诅咒
    }
    @Override
    public boolean isTreasureOnly() {
        return false; // 允许在附魔台获得该附魔
    }

    @Override
    public int getMaxLevel() {
        return 1;  // 附魔仅需一级
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 仅允许弓附魔
        return stack.getItem() instanceof BowItem;
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        ItemStack bow = event.getBow();
        int charge = event.getCharge();

        // 检查弓是否有"有限"附魔
        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FINITE_ENCHANT.get(), bow) > 0) {
            int arrowCount = getArrowCount(player) - 1;  // 获取玩家背包中的箭矢数量

            // 检查是否有原版的“无限”附魔
            boolean hasInfinity = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0;
            boolean hasLoyalty = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LOYALTY_ENCHANT.get(), bow) > 0;
            boolean hasLightning = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LIGHTNING_ENCHANT.get(), bow) > 0;

            if (arrowCount > 0 || hasInfinity) {
                if (!hasInfinity) {
                    // 如果没有“无限”附魔，则移除背包中的箭矢
                    removeArrowsFromInventory(player, arrowCount);
                }

                // 计算射箭的速度
                float velocity = getArrowVelocity(charge);

                // 一次性射出所有箭矢
                for (int i = 0; i < arrowCount; i++) {
                    AbstractArrow arrow = shootArrow(player, level, 1.5F, velocity);

                    // 如果弓带有“引雷”附魔，则为箭矢附加“引雷”效果
                    if (hasLightning && arrow != null) {
                        arrow.getPersistentData().putBoolean("hasLightning", true);
                    }

                    // 如果弓带有“忠诚”附魔，则为箭矢添加返回效果
                    if (hasLoyalty && arrow != null) {
                        trackedArrows.put(arrow.getUUID(), player);  // 跟踪箭矢
                        arrowLifetimes.put(arrow.getUUID(), 0);  // 初始化箭矢生命周期
                    }
                }
            }
        }
    }

    // 获取箭矢的飞行速度，根据玩家的拉弓时间调整速度
    private float getArrowVelocity(int charge) {
        float velocity = charge / 20.0F;
        velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
        if (velocity > 1.0F) {
            velocity = 1.0F;
        }
        return velocity;
    }

    // 获取玩家背包中的箭矢数量
    private int getArrowCount(Player player) {
        int arrowCount = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof ArrowItem) {
                arrowCount += stack.getCount();
            }
        }
        return arrowCount;
    }

    // 从玩家背包中移除箭矢
    private void removeArrowsFromInventory(Player player, int count) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof ArrowItem) {
                int removed = Math.min(stack.getCount(), count);
                stack.shrink(removed);  // 减少箭的数量
                count -= removed;
                if (count <= 0) {
                    break;
                }
            }
        }
    }
    private AbstractArrow shootArrow(Player player, Level level, float scatterRange, float velocity) {
        if (!level.isClientSide) {
            Vec3 playerDirection = player.getLookAngle();  // 获取玩家的视线方向

            // 为散射效果生成随机角度偏移，确保不与玩家方向重叠
            double scatterX = (random.nextDouble() - 0.5) * scatterRange;
            double scatterY = (random.nextDouble() - 0.1) * scatterRange;  // 限制垂直方向的散射范围，避免过大的垂直偏移
            double scatterZ = (random.nextDouble() - 0.5) * scatterRange;

            Vec3 scatterDirection = playerDirection.add(scatterX, scatterY, scatterZ).normalize();  // 加上偏移量并归一化

            // 创建箭矢，并确保不会立即击中玩家
            AbstractArrow arrow = new AbstractArrow(EntityType.ARROW, level) {
                @Override
                protected ItemStack getPickupItem() {
                    return null;
                }
            };
            arrow.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());  // 设置箭的位置
            arrow.shoot(scatterDirection.x, scatterDirection.y, scatterDirection.z, velocity * 3.0F, 1.0F);  // 根据蓄力速度射出箭矢
            level.addFreshEntity(arrow);  // 将箭矢加入到世界中
            return arrow;
        }
        return null;
    }

    // 处理箭矢飞向玩家的逻辑
    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) event.level;

            // 更新所有箭矢的生命周期计数
            for (Map.Entry<UUID, Integer> entry : new HashMap<>(arrowLifetimes).entrySet()) {
                Entity arrowEntity = world.getEntity(entry.getKey());
                if (arrowEntity instanceof AbstractArrow arrow) {
                    int lifetime = entry.getValue() + 1;  // 增加生命周期计数
                    arrowLifetimes.put(arrow.getUUID(), lifetime);

                    // 如果箭矢存在200个Tick，开始返回玩家
                    if (lifetime >= 20) {
                        Player shooter = trackedArrows.get(arrow.getUUID());  // 获取当前跟踪的玩家
                        if (shooter != null && !shooter.isDeadOrDying()) {
                            Vec3 shooterPosition = shooter.position();
                            Vec3 arrowPosition = arrow.position();

                            // 计算朝向玩家的方向向量，并不断调整方向
                            Vec3 directionToShooter = shooterPosition.subtract(arrowPosition).normalize();

                            // 禁用箭矢重力
                            arrow.setNoGravity(true);
                            arrow.setDeltaMovement(directionToShooter.scale(1)); // 持续调整箭的速度和方向，跟踪玩家
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onArrowJoinWorld(EntityJoinLevelEvent event) {
        // 检查加入世界的实体是否是箭矢
        if (event.getEntity() instanceof AbstractArrow arrow) {
            // 获取箭矢的射手
            if (arrow.getOwner() instanceof Player player) {
                ItemStack bow = player.getMainHandItem(); // 获取玩家主手中的物品

                // 检查主手物品是否是弓并且是否带有“引雷”附魔
                if (bow.getItem() instanceof BowItem && EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LIGHTNING_ENCHANT.get(), bow) > 0) {
                    // 如果弓带有“引雷”附魔，则为箭矢添加引雷效果
                    arrow.getPersistentData().putBoolean("hasLightning", true);
                }
            }
        }
    }
}

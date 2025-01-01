package com.ntsw.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoyaltyEnchant extends Enchantment {

    private final Map<UUID, Integer> arrowLifetimes = new HashMap<>();
    private final Map<UUID, Player> trackedArrows = new HashMap<>();

    public LoyaltyEnchant() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        MinecraftForge.EVENT_BUS.register(this);  // 注册事件总线
    }

    @Override
    public int getMaxLevel() {
        return 1; // 附魔只需要一个等级
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 仅允许弓进行此附魔
        return stack.getItem() instanceof net.minecraft.world.item.BowItem;
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
    public boolean isDiscoverable() {
        return true; // 允许该附魔在附魔表中出现
    }

    @SubscribeEvent
    public void onArrowSpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow) {
            Entity owner = arrow.getOwner();  // 获取射手实体
            if (owner instanceof Player player) {
                // 检查玩家手中的弓是否带有忠诚附魔
                ItemStack bow = player.getMainHandItem();
                if (EnchantmentHelper.getItemEnchantmentLevel(this, bow) > 0) {
                    arrowLifetimes.put(arrow.getUUID(), 0); // 初始化箭矢的生命周期
                    trackedArrows.put(arrow.getUUID(), player); // 记录箭矢跟踪的玩家
                }
            }
        }        if (event.getEntity() instanceof AbstractArrow arrow) {
            Entity owner = arrow.getOwner();  // 获取射手实体
            if (owner instanceof Player player) {
                // 检查玩家手中的弓是否带有忠诚附魔
                ItemStack bow = player.getOffhandItem();
                if (EnchantmentHelper.getItemEnchantmentLevel(this, bow) > 0) {
                    arrowLifetimes.put(arrow.getUUID(), 0); // 初始化箭矢的生命周期
                    trackedArrows.put(arrow.getUUID(), player); // 记录箭矢跟踪的玩家
                }
            }
        }
    }

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

                    // 如果箭矢存在20个Tick
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
}

package com.ntsw.event.enchantedEvent;

import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;


@Mod.EventBusSubscriber(modid = Main.MODID)
public class EnchantmentEffectHandler {

//    private static final String TEMP_TEXTURE = "nitianfumo:textures/items/zuo_diamond_sword.png"; // 临时材质路径


    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack weapon = player.getMainHandItem();

        // 获取"竖劈之刃"附魔等级
        int shuPiZhiRenLevel = weapon.getEnchantmentLevel(ModEnchantments.SHUPIZHIREN.get());
        int sweepingLevel = weapon.getEnchantmentLevel(Enchantments.SWEEPING_EDGE);

        //竖劈之刃效果
        if (sweepingLevel == 0) {
            sweepingLevel = 1;
        }
        if (shuPiZhiRenLevel > 0) {
            // 计算攻击范围，附魔等级越高，攻击范围越大
            double range = 1.0 + (shuPiZhiRenLevel - 1) * 0.5; // 初始5格距离，每级增加0.5格
            double width = 1.0 + (sweepingLevel - 1) * 0.5; // 初始1格宽度，每级增加0.5格

            // 获取玩家面向方向
            Vec3 lookDirection = player.getLookAngle().normalize();
            Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);

            // 计算攻击区域的AABB
            Vec3 targetPos = playerPos.add(lookDirection.scale(range));
            AABB attackBox = new AABB(playerPos, targetPos).inflate(width / 2, 0.5, width / 2);

            // 遍历区域内的所有生物
            for (Entity entity : player.level().getEntities(player, attackBox)) {
                if (entity instanceof LivingEntity target && entity != player) {
                    target.hurt(player.damageSources().playerAttack(player), 2.0F + shuPiZhiRenLevel); // 伤害随等级增加
                }
            }
        }
    }

    //透视
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD); // 头盔槽位

        // 检查头盔是否带有"透视"附魔
        int toushiLevel = helmet.getEnchantmentLevel(ModEnchantments.TOUSHI.get());

        if (toushiLevel > 0) {
            // 计算效果范围，初始20格，每级增加10格
            double range = 15.0 + (toushiLevel - 1) * 10.0;

            // 为范围内的生物添加荧光效果
            for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(range))) {
                if (entity instanceof LivingEntity living && entity != player) {
                    // 如果生物在范围内，给予荧光效果
                    living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 0, false, false));
                }
            }
        }
    }

    @SubscribeEvent
    public static void FuQiangDuoAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack weapon = player.getMainHandItem();
        if (player.level().isClientSide) {
            return;  // 在客户端不执行任何操作
        }
        int fuqiangduoLevel = weapon.getEnchantmentLevel(ModEnchantments.FUQIANGDUO.get());

        if (fuqiangduoLevel > 0) {
            // 获取背包中的所有物品
            List<ItemStack> inventoryItems = player.getInventory().items;
            if (!inventoryItems.isEmpty()) {
                Random random = new Random();
                // 随机选择一个物品
                int randomIndex = random.nextInt(inventoryItems.size());
                ItemStack itemToDrop = inventoryItems.get(randomIndex);
                player.getInventory().setItem(randomIndex, ItemStack.EMPTY);
                // 丢弃物品
                if (!itemToDrop.isEmpty()) {
                    player.drop(itemToDrop, true);
                    System.out.println("Player dropped: " + itemToDrop);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityAttack(LivingAttackEvent event) {
        if (event.getEntity() != null && event.getSource().getEntity() instanceof Player player) {
            Entity target = event.getEntity();
            ItemStack weapon = player.getMainHandItem();

            // 获取武器上的附魔等级
            int fuHuoYanFuJiaLevel = weapon.getEnchantmentLevel(ModEnchantments.FUHUOYANFUJIA.get());

            // 如果附魔等级大于0，进行附魔效果处理
            if (fuHuoYanFuJiaLevel > 0) {
                // 如果生物正处于火焰中，移除火焰效果
                if (target.getRemainingFireTicks() > 0) {
                    target.setRemainingFireTicks(0); // 立即移除火焰效果
                }

                // 阻止火焰效果在生物身上显示
                // 将生物的火焰时间设置为负值来模拟“无火焰显示”
                target.setRemainingFireTicks(-1);  // -1 将让火焰效果不再显示

            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack2(AttackEntityEvent event) {
        Player player = event.getEntity(); // 获取玩家
        // 检查武器是否有 'fujitui' 附魔
        if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.FUJITUI.get(), player) > 0) {
            Entity target = event.getTarget(); // 获取攻击目标（生物）

            // 确保攻击的目标是一个生物
            if (target instanceof LivingEntity) {
                // 获取附魔等级
                int fujituiLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.FUJITUI.get(), player);

                // 计算吸引的力度和方向
                double pullStrength = 2 * fujituiLevel; // 吸引力度随附魔等级增加
                double xDiff = player.getX() - target.getX();
                double yDiff = player.getY() - target.getY();
                double zDiff = player.getZ() - target.getZ();
                double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

                // 确保生物不是在玩家附近（避免太短的距离）
                if (distance > 1.5) {
                    // 计算单位向量，表示吸引的方向
                    double xDirection = xDiff / distance;
                    double yDirection = yDiff / distance;
                    double zDirection = zDiff / distance;

                    // 根据吸引力度和方向调整生物的位置
                    target.setDeltaMovement(target.getDeltaMovement().add(xDirection * pullStrength, yDirection * pullStrength, zDirection * pullStrength));
                }
            }
        }
    }


    @SubscribeEvent
    public static void onEntityAttack(AttackEntityEvent event) {
        Player player = event.getEntity(); // 获取玩家
        ItemStack weapon = player.getMainHandItem(); // 获取玩家手中的武器

        // 检查武器是否有 'jianrenpianzuo' 附魔
        if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.JIANRENPIANZUO.get(), player) > 0) {
            Entity target = event.getTarget(); // 获取攻击目标（生物）
            weapon.getOrCreateTag().putInt("CustomModelData", 1);
            // 确保攻击的目标是一个生物
            if (target instanceof LivingEntity) {
                // 获取附魔等级
                int jianrenpianzuoLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.JIANRENPIANZUO.get(), player);
                // 计算目标的左边方向
                double offset = 2.0; // 计算目标左边的距离，可以根据需求调整
                double xOffset = -offset * Math.sin(player.getYHeadRot() * Math.PI / 180); // 计算目标左边的x偏移
                double zOffset = offset * Math.cos(player.getYHeadRot() * Math.PI / 180);  // 计算目标左边的z偏移

                // 在目标左边查找生物
                double targetX = target.getX() + xOffset;
                double targetZ = target.getZ() + zOffset;

                // 查找离目标左边最近的生物
                LivingEntity closestEntity = null;
                double closestDistance = Double.MAX_VALUE;

                for (Entity entity : player.level().getEntities(player, target.getBoundingBox().inflate(3.0))) { // 搜索3格范围内的生物
                    if (entity instanceof LivingEntity && entity != target) { // 排除目标本身
                        double distance = entity.distanceToSqr(targetX, entity.getY(), targetZ); // 计算与左侧生物的距离
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestEntity = (LivingEntity) entity;
                        }
                    }
                }
                int attackDamage = (int) getAttackDamage(player, weapon, EquipmentSlot.MAINHAND);
                // 如果找到了左边的生物，应用伤害
                if (closestEntity != null) {
                    closestEntity.hurt(closestEntity.damageSources().playerAttack(player), attackDamage + jianrenpianzuoLevel);
                    MobEffectInstance poisonEffect = new MobEffectInstance(MobEffects.POISON, 80, jianrenpianzuoLevel);
                    closestEntity.addEffect(poisonEffect);
                }
                event.setCanceled(true);
            }
        } else {
            if (weapon.getTag() != null) {
                weapon.getTag().remove("CustomModelData");
            }
        }
    }

    private static double getAttackDamage(Player attacker, ItemStack stack, EquipmentSlot equipmentSlot) {
        if (stack.isEmpty()) {
            return 1.0; // 无武器时的基础攻击伤害
        }

        // 获取物品的基础攻击伤害属性
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        double baseDamage = 0.0;

        if (attribute != null) {
            baseDamage = attribute.getBaseValue();
        }

        // 获取物品本身的攻击伤害（例如，剑的伤害）
        double weaponDamage = stack.getAttributeModifiers(equipmentSlot)
                .get(Attributes.ATTACK_DAMAGE)
                .stream()
                .mapToDouble(modifier -> modifier.getAmount())
                .sum();

        // 总攻击伤害 = 基础伤害 + 物品伤害
        return baseDamage + weaponDamage;
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        Entity player = event.getEntity(); // 获取玩家
        ItemStack weapon = player.getHandSlots().iterator().next(); // 获取玩家手中的武器
        int weaponEnchant = weapon.getEnchantmentLevel(ModEnchantments.JIANRENPIANZUO.get());
        // 检查武器是否有 'jianrenpianzuo' 附魔
        if (weaponEnchant > 0) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void onLivingAttack2(AttackEntityEvent event) {
        Entity player = event.getEntity(); // 获取玩家
        ItemStack weapon = player.getHandSlots().iterator().next(); // 获取玩家手中的武器
        int weaponEnchant = weapon.getEnchantmentLevel(ModEnchantments.GOUJITIAOQIANG.get());
        // 检查武器是否有 'jianrenpianzuo' 附魔
        if (weaponEnchant > 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack heldItem = event.getEntity().getMainHandItem();
        int pickaxeEnchant = heldItem.getEnchantmentLevel(ModEnchantments.GOUJITIAOQIANG.get());
        if (pickaxeEnchant > 0) {
            event.setCanceled(true);}
    }

}

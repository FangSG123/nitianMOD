package com.ntsw.item;

import com.ntsw.event.EntityConnectionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class TiesuoLianhuanItem extends Item {

    public TiesuoLianhuanItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 检查是否已经选择了第一个实体
        if (!stack.hasTag() || !stack.getTag().contains("FirstEntityID")) {

            // 检查玩家是否在潜行
            if (player.isShiftKeyDown()) {
                // 将玩家自己作为第一个实体
                stack.getOrCreateTag().putInt("FirstEntityID", player.getId());
                if (level.isClientSide) {
                    player.displayClientMessage(Component.literal("已选择自己作为第一个实体"), true);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            } else {
                // 射线检测选择第一个实体
                Entity target = getEntityLookedAt(player, 10.0); // 10.0为检测距离，可调整

                if (target != null && target != player) {
                    // 将第一个实体的ID保存到物品的NBT中
                    stack.getOrCreateTag().putInt("FirstEntityID", target.getId());
                    if (level.isClientSide) {
                        player.displayClientMessage(Component.literal("已选择第一个实体：" + target.getDisplayName().getString()), true);
                    }
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                } else {
                    if (level.isClientSide) {
                        player.displayClientMessage(Component.literal("未找到可选的实体。"), true);
                    }
                    return InteractionResultHolder.fail(stack);
                }
            }
        } else {
            // 已经选择了第一个实体，选择第二个实体

            int firstEntityId = stack.getTag().getInt("FirstEntityID");
            Entity firstEntity = level.getEntity(firstEntityId);

            if (firstEntity == null) {
                if (level.isClientSide) {
                    player.displayClientMessage(Component.literal("第一个实体已不在世界中。"), true);
                }
                stack.getTag().remove("FirstEntityID");
                return InteractionResultHolder.fail(stack);
            }

            Entity secondEntity;

            // 检查玩家是否在潜行
            if (player.isShiftKeyDown()) {
                // 将玩家自己作为第二个实体
                secondEntity = player;
            } else {
                // 正常选择第二个实体
                secondEntity = getEntityLookedAt(player, 10.0);
            }

            if (secondEntity != null && secondEntity != firstEntity) {
                // 保存连接关系，持续600 ticks（30秒）
                EntityConnectionHandler.addConnection(firstEntity, secondEntity, 600);

                if (level.isClientSide) {
                    player.displayClientMessage(Component.literal("已连接实体：" + firstEntity.getDisplayName().getString() + " 和 " + secondEntity.getDisplayName().getString() + "，持续30秒"), true);
                }

                // 移除 NBT 中的 FirstEntityID
                stack.getTag().remove("FirstEntityID");

                // 消耗物品
                stack.shrink(1);

                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            } else {
                if (level.isClientSide) {
                    player.displayClientMessage(Component.literal("未找到可选的第二个实体。"), true);
                }
                return InteractionResultHolder.fail(stack);
            }
        }
    }

    // 工具方法：获取玩家视线中的实体
    private Entity getEntityLookedAt(Player player, double range) {
        Level level = player.level();
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPosition = eyePosition.add(lookVector.scale(range));

        // 创建 ClipContext 对象
        var context = new net.minecraft.world.level.ClipContext(
                eyePosition,
                endPosition,
                net.minecraft.world.level.ClipContext.Block.OUTLINE,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                player
        );

        // 使用 level.clip() 方法进行射线检测
        var blockHitResult = level.clip(context);

        // 获取起点到碰撞点的距离
        double blockDistance = blockHitResult.getLocation().distanceTo(eyePosition);

        // 搜集起点到终点范围内的实体
        AABB searchArea = new AABB(eyePosition, endPosition).inflate(1.0);
        List<Entity> entities = level.getEntities(player, searchArea, e -> e.isPickable() && e != player);

        Entity closestEntity = null;
        double minDistance = blockDistance;

        for (Entity entity : entities) {
            AABB entityAABB = entity.getBoundingBox().inflate(0.3);
            Optional<Vec3> optional = entityAABB.clip(eyePosition, endPosition);

            if (optional.isPresent()) {
                double distance = eyePosition.distanceTo(optional.get());
                if (distance < minDistance) {
                    closestEntity = entity;
                    minDistance = distance;
                }
            } else if (entityAABB.contains(eyePosition)) {
                // 如果玩家在实体内部
                closestEntity = entity;
                minDistance = 0.0;
            }
        }

        return closestEntity;
    }
}

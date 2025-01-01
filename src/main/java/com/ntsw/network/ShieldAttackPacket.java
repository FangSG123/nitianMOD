package com.ntsw.network;

import com.ntsw.ModEnchantments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShieldAttackPacket {

    public ShieldAttackPacket() {
    }

    public static void encode(ShieldAttackPacket msg, FriendlyByteBuf buf) {
        // 不需要编码任何数据
    }

    public static ShieldAttackPacket decode(FriendlyByteBuf buf) {
        return new ShieldAttackPacket();
    }

    public static void handle(ShieldAttackPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            // 添加调试信息
            System.out.println("Received ShieldAttackPacket on server.");

            if (player != null && player.isUsingItem() && player.getUsedItemHand() == InteractionHand.OFF_HAND) {
                System.out.println("Player is using item in off-hand on server.");
                ItemStack offHandItem = player.getOffhandItem();
                if (offHandItem.getItem() instanceof ShieldItem && offHandItem.isEnchanted()) {
                    System.out.println("Off-hand item is an enchanted shield on server.");
                    // 检查盾牌是否具有自定义附魔
                    if (offHandItem.getEnchantmentLevel(ModEnchantments.DUNCUO_ENCHANT.get()) > 0) {
                        System.out.println("Shield has the custom enchantment.");
                        LivingEntity target = getTargetEntity(player);
                        if (target != null) {
                            System.out.println("Found target entity: " + target.getName().getString());
                            player.attack(target);
                        } else {
                            System.out.println("No target entity found.");
                        }
                    } else {
                        System.out.println("Shield does not have the custom enchantment.");
                    }
                } else {
                    System.out.println("Off-hand item is not an enchanted shield on server.");
                }
            } else {
                System.out.println("Player is not using item in off-hand on server or player is null.");
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static LivingEntity getTargetEntity(ServerPlayer player) {
        double reach = 4.0D;
        var eyePosition = player.getEyePosition();
        var lookVector = player.getLookAngle();
        var reachVector = eyePosition.add(lookVector.scale(reach));

        var aabb = player.getBoundingBox().expandTowards(lookVector.scale(reach)).inflate(1.0D);
        var entities = player.level().getEntitiesOfClass(LivingEntity.class, aabb, e -> e != player && e.isAlive());

        LivingEntity closestEntity = null;
        double closestDistance = reach * reach;

        for (LivingEntity entity : entities) {
            var entityAABB = entity.getBoundingBox();
            var entityPosition = entityAABB.getCenter();
            double distance = eyePosition.distanceToSqr(entityPosition);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestEntity = entity;
            }
        }

        // 添加调试信息
        if (closestEntity != null) {
            System.out.println("Target entity found: " + closestEntity.getName().getString());
        } else {
            System.out.println("No entity found within reach.");
        }

        return closestEntity;
    }
}

package com.ntsw.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.Vec3;

public class KnockbackWeapon extends SwordItem {

    public KnockbackWeapon() {
        super(Tiers.WOOD, 5, -2.4F, new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            if (player.onGround()) {
                // 如果玩家在地面上，则进行水平反向击退
                Vec3 lookDirection = player.getLookAngle().normalize().scale(-1.0);
                player.push(lookDirection.x, 0.1, lookDirection.z); // 应用水平击退效果
            } else {
                // 如果玩家在空中，则给予竖直向上的力
                player.push(0, 0.6, 0); // 向上推力
            }
            player.hurtMarked = true; // 确保客户端同步更新
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}

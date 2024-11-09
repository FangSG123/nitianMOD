package com.ntsw.item;

import com.ntsw.ModItems;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class GudingdaoItem extends SwordItem {

    public GudingdaoItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Item.Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (target instanceof Player targetPlayer && attacker != null) {
            if (!hasRequiredItems(targetPlayer)) {
                // 对目标造成双倍伤害
                float baseDamage = getBaseDamage(attacker) * 2;

                // 确认 DamageSource 的正确使用
                DamageSource source;
                if (attacker instanceof Player playerAttacker) {
                    source = targetPlayer.damageSources().playerAttack(playerAttacker);
                } else {
                    source = attacker.damageSources().mobAttack(attacker);
                }

                target.hurt(source, baseDamage);
            }
        }

        return result;
    }

    private boolean hasRequiredItems(Player player) {
        // 检查主物品栏
        for (ItemStack itemStack : player.getInventory().items) {
            if (isRequiredItem(itemStack)) {
                return true;
            }
        }
        // 检查盔甲槽
        for (ItemStack itemStack : player.getInventory().armor) {
            if (isRequiredItem(itemStack)) {
                return true;
            }
        }
        // 检查副手槽
        for (ItemStack itemStack : player.getInventory().offhand) {
            if (isRequiredItem(itemStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRequiredItem(ItemStack itemStack) {
        return itemStack.getItem() == ModItems.TIESUO_LIANHUAN.get()
                || itemStack.getItem() == ModItems.SHAN.get()
                || itemStack.getItem() == ModItems.JIU.get()
                || itemStack.getItem() == ModItems.HUOSHA.get();
    }

    private float getBaseDamage(LivingEntity attacker) {
        // 获取攻击者的攻击伤害属性
        AttributeInstance attackDamageAttribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamageAttribute != null) {
            return (float) attackDamageAttribute.getValue();
        }
        // 默认的基础伤害（例如铁剑的基础伤害）
        return 6.0F;
    }
}

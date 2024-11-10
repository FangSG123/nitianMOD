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

        if (attacker != null) {
            if (target instanceof Player targetPlayer) {
                if (!hasRequiredItems(targetPlayer)) {
                    // 对玩家造成双倍伤害
                    applyDoubleDamage(target, attacker);
                }
            } else {
                // 对非玩家生物（例如僵尸、骷髅等）造成双倍伤害
                applyDoubleDamage(target, attacker);
            }
        }

        return result;
    }

    /**
     * 对目标造成双倍伤害
     *
     * @param target    受击目标
     * @param attacker  攻击者
     */
    private void applyDoubleDamage(LivingEntity target, LivingEntity attacker) {
        float baseDamage = getBaseDamage(attacker) * 2;
        DamageSource source;
        if (attacker instanceof Player playerAttacker) {
            source = target.damageSources().playerAttack(playerAttacker);
        } else {
            source = attacker.damageSources().mobAttack(attacker);
        }

        target.hurt(source, baseDamage);
    }

    /**
     * 检查玩家是否拥有所需物品
     *
     * @param player 玩家实体
     * @return 如果玩家拥有至少一个所需物品，则返回 true，否则返回 false
     */
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

    /**
     * 判断物品是否为所需物品
     *
     * @param itemStack 物品栈
     * @return 如果是所需物品，则返回 true，否则返回 false
     */
    private boolean isRequiredItem(ItemStack itemStack) {
        return itemStack.getItem() == ModItems.TIESUO_LIANHUAN.get()
                || itemStack.getItem() == ModItems.SHAN.get()
                || itemStack.getItem() == ModItems.JIU.get()
                || itemStack.getItem() == ModItems.HUOSHA.get();
    }

    /**
     * 获取攻击者的基础伤害
     *
     * @param attacker 攻击者实体
     * @return 攻击者的基础伤害值
     */
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

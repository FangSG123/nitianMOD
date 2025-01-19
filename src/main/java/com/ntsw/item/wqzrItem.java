package com.ntsw.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class wqzrItem extends Item {
    public wqzrItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        // 从父类获取默认的属性修饰器
        Multimap<Attribute, AttributeModifier> modifiers = super.getDefaultAttributeModifiers(slot);

        // 只在主手时生效
        if (slot == EquipmentSlot.MAINHAND) {
            // 复制一份默认的属性（有些版本需要手动复制，可使用 HashMultimap.create(modifiers)）
            modifiers = HashMultimap.create(modifiers);

            // 移除原有的攻击伤害属性 (避免和默认值叠加)
            modifiers.removeAll(Attributes.ATTACK_DAMAGE);

            // 添加我们自己的攻击伤害属性: 1000 点
            modifiers.put(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                            "Weapon Damage Modifier",
                            1000.0D,  // 伤害数值
                            AttributeModifier.Operation.ADDITION)
            );

            // 如果你想修改攻击速度，也可以在这里添加或覆盖
            // 例如：移除原有攻击速度，再添加自定义的攻击速度
            // modifiers.removeAll(Attributes.ATTACK_SPEED);
            // modifiers.put(
            //     Attributes.ATTACK_SPEED,
            //     new AttributeModifier(BASE_ATTACK_SPEED_UUID,
            //                           "Weapon Attack Speed Modifier",
            //                           -2.4D, // 攻速数值，越小挥刀间隔越久（负值 = 慢）
            //                           AttributeModifier.Operation.ADDITION)
            // );
        }
        return modifiers;
    }
}

package com.ntsw.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SaobaItem extends Item {
    private static final String POTION_EFFECTS_KEY = "PotionEffects";

    public SaobaItem(Properties properties) {
        super(properties);
    }

    /**
     * 向 SaobaItem 添加药水效果
     *
     * @param stack  SaobaItem 的 ItemStack
     * @param effect 要添加的 MobEffect
     * @return 修改后的 ItemStack
     */
    public static ItemStack addPotionEffect(ItemStack stack, MobEffect effect) {
        if (effect == null) return stack;

        CompoundTag tag = stack.getOrCreateTag();
        ListTag effectsList;

        if (tag.contains(POTION_EFFECTS_KEY, 9)) { // 9 表示 ListTag
            effectsList = tag.getList(POTION_EFFECTS_KEY, 8); // 8 表示字符串类型
        } else {
            effectsList = new ListTag();
        }

        ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        if (key != null) {
            String effectKey = key.toString();
            boolean alreadyExists = false;
            for (int i = 0; i < effectsList.size(); i++) {
                if (effectsList.getString(i).equals(effectKey)) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) { // 避免重复添加
                effectsList.add(StringTag.valueOf(effectKey));
                tag.put(POTION_EFFECTS_KEY, effectsList);
                System.out.println("Added potion effect: " + effectKey);
            } else {
                System.out.println("Potion effect already exists: " + effectKey);
            }
        }

        return stack;
    }

    /**
     * 获取 Saoba 物品上的所有药水效果
     *
     * @param stack 物品栈
     * @return 药水效果列表
     */
    public static List<MobEffect> getPotionEffects(ItemStack stack) {
        List<MobEffect> effects = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(POTION_EFFECTS_KEY, 9)) {
            ListTag effectsList = tag.getList(POTION_EFFECTS_KEY, 8);
            for (int i = 0; i < effectsList.size(); i++) {
                String effectKey = effectsList.getString(i);
                ResourceLocation resourceLocation = ResourceLocation.tryParse(effectKey);
                if (resourceLocation != null) {
                    MobEffect effect = BuiltInRegistries.MOB_EFFECT.getOptional(resourceLocation).orElse(null);
                    if (effect != null) {
                        effects.add(effect);
                    }
                }
            }
        }
        return effects;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean hasPotionEffects = false;

        // 调试日志：检查物品的 NBT 标签
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            System.out.println("SaobaItem 没有 NBT 标签.");
        } else {
            if (tag.contains(POTION_EFFECTS_KEY, 9)) { // 9 表示 ListTag
                ListTag effectsList = tag.getList(POTION_EFFECTS_KEY, 8); // 8 表示字符串类型
                System.out.println("SaobaItem 有 " + effectsList.size() + " 个药水效果.");
                for (int i = 0; i < effectsList.size(); i++) {
                    String effectKey = effectsList.getString(i);
                    System.out.println("Applying potion effect: " + effectKey);
                    ResourceLocation resourceLocation = ResourceLocation.tryParse(effectKey);
                    if (resourceLocation != null) {
                        MobEffect effect = BuiltInRegistries.MOB_EFFECT.getOptional(resourceLocation).orElse(null);
                        if (effect != null) {
                            // 应用药水效果
                            target.addEffect(new MobEffectInstance(effect, 200, 0)); // 200 ticks = 10秒
                            hasPotionEffects = true;
                            System.out.println("Applied potion effect: " + effectKey);
                        }
                    }
                }
            } else {
                System.out.println("SaobaItem 有 NBT 标签，但没有 PotionEffects.");
            }
        }

        // 仅当存在药水效果时，应用4点伤害
        if (hasPotionEffects) {
            if (attacker instanceof Player player) {
                target.hurt(attacker.damageSources().playerAttack(player), 4.0F);
                System.out.println("Applied 4.0F damage to target.");
            } else {
                target.hurt(attacker.damageSources().mobAttack(attacker), 4.0F);
                System.out.println("Applied 4.0F damage to target (mob attack).");
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        List<MobEffect> effects = getPotionEffects(stack);
        if (!effects.isEmpty()) {
            tooltip.add(Component.literal("药水效果:"));
            for (MobEffect effect : effects) {
                String effectName = Component.translatable(effect.getDescriptionId()).getString();
                tooltip.add(Component.literal("- " + effectName));
            }
        }
    }
}

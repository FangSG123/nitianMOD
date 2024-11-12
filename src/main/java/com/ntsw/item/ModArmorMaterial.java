package com.ntsw.item;

import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public enum ModArmorMaterial implements ArmorMaterial {
    PIFENG("pifeng", Integer.MAX_VALUE, new int[]{2, 5, 6, 2}, 9,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, Ingredient.of(Items.IRON_INGOT));

    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11}; // 耐久基础值
    private final String name;
    private final int durability;
    private final int[] defensePoints;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Ingredient repairIngredient;

    ModArmorMaterial(String name, int durability, int[] defensePoints, int enchantability,
                     SoundEvent equipSound, float toughness, float knockbackResistance,
                     Ingredient repairIngredient) {
        this.name = name;
        this.durability = durability;
        this.defensePoints = defensePoints;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    // 实现 getDurabilityForType 方法
    @Override
    public int getDurabilityForType(Type type) {
        return BASE_DURABILITY[type.getSlot().getIndex()] * durability;
    }

    @Override
    public int getDefenseForType(Type type) {
        return defensePoints[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}

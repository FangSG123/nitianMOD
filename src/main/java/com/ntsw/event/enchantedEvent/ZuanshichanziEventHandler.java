package com.ntsw.event.enchantedEvent;


import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Multimap;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class ZuanshichanziEventHandler {
    private static final UUID ZUANSHICHANZI_ID = UUID.fromString("12345678-1234-1234-1234-1234567890ab");
    private static final AttributeModifier ZUANSHICHANZI_ENCHANT = new AttributeModifier(ZUANSHICHANZI_ID, "ZuanShiChanZiEnchant", 1D, AttributeModifier.Operation.ADDITION);
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        // 检查物品是否具有zuanshichanzi附魔
        if (heldItem.isEmpty() || heldItem.getEnchantmentLevel(ModEnchantments.ZUANSHICHANZI.get()) <= 0) {
            return;
        }

        BlockState blockState = event.getState();

        // 创建一个钻石铲子
        ItemStack diamondShovel = new ItemStack(Items.DIAMOND_SHOVEL);

        // 检查钻石铲子对该方块的挖掘速度
        float diamondShovelSpeed = diamondShovel.getDestroySpeed(blockState);

        if (diamondShovelSpeed > 1.0F) {
            // 设置挖掘速度为钻石铲子的速度
            event.setNewSpeed(diamondShovelSpeed);
        }
    }

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        // 检查物品是否具有zuanshichanzi附魔
        if (heldItem.isEmpty() || heldItem.getEnchantmentLevel(ModEnchantments.ZUANSHICHANZI.get()) <= 0) {
            return;
        }

        BlockState blockState = event.getTargetBlock();

        // 创建一个钻石铲子
        ItemStack diamondShovel = new ItemStack(Items.DIAMOND_SHOVEL);

        // 检查钻石铲子是否能挖掘该方块
        boolean canHarvest = diamondShovel.isCorrectToolForDrops(blockState);

        if (canHarvest) {
            event.setCanHarvest(true);
        }
    }

    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();

        // 检查物品是否具有zuanshichanzi附魔
        if (stack.isEmpty() || stack.getEnchantmentLevel(ModEnchantments.ZUANSHICHANZI.get()) <= 0) {
            return;
        }

        EquipmentSlot slot = event.getSlotType();

        // 仅在主手或副手时生效
        if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) {
            return;
        }

// 获取钻石铲子的攻击力和攻击速度修正
        ItemStack diamondShovel = new ItemStack(Items.DIAMOND_SHOVEL);
        Multimap<Attribute, AttributeModifier> diamondAttributes = diamondShovel.getAttributeModifiers(slot);

// 获取攻击力和攻击速度的 AttributeModifier
        Collection<AttributeModifier> attackDamageModifiers = diamondAttributes.get(Attributes.ATTACK_DAMAGE);
        Collection<AttributeModifier> attackSpeedModifiers = diamondAttributes.get(Attributes.ATTACK_SPEED);

// 清除当前物品的攻击力和攻击速度修正
        for (AttributeModifier modifier : attackDamageModifiers) {
            event.removeModifier(Attributes.ATTACK_DAMAGE, modifier);
        }

        for (AttributeModifier modifier : attackSpeedModifiers) {
            event.removeModifier(Attributes.ATTACK_SPEED, modifier);
        }


        // 将钻石铲子的攻击力和攻击速度修正添加到当前物品
        if (!diamondAttributes.isEmpty()) {
            for (Map.Entry<Attribute, AttributeModifier> entry : diamondAttributes.entries()) {
                Attribute attribute = entry.getKey();
                AttributeModifier modifier = entry.getValue();
                event.addModifier(attribute, modifier);
            }
        }
    }
}

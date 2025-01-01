package com.ntsw.enchantment;

import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class BlessingOfVanishingEnchant extends Enchantment {

    // 定义升级路线
    private static final Map<Item, Item> UPGRADE_MAP = new HashMap<>();

    static {
        // 工具和武器升级路线（保持不变）
        UPGRADE_MAP.put(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE);
        UPGRADE_MAP.put(Items.STONE_PICKAXE, Items.IRON_PICKAXE);
        UPGRADE_MAP.put(Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE);
        UPGRADE_MAP.put(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);

        UPGRADE_MAP.put(Items.WOODEN_AXE, Items.STONE_AXE);
        UPGRADE_MAP.put(Items.STONE_AXE, Items.IRON_AXE);
        UPGRADE_MAP.put(Items.IRON_AXE, Items.DIAMOND_AXE);
        UPGRADE_MAP.put(Items.DIAMOND_AXE, Items.NETHERITE_AXE);

        UPGRADE_MAP.put(Items.WOODEN_SWORD, Items.STONE_SWORD);
        UPGRADE_MAP.put(Items.STONE_SWORD, Items.IRON_SWORD);
        UPGRADE_MAP.put(Items.IRON_SWORD, Items.DIAMOND_SWORD);
        UPGRADE_MAP.put(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);

        // 盔甲升级路线
        UPGRADE_MAP.put(Items.LEATHER_HELMET, Items.GOLDEN_HELMET);
        UPGRADE_MAP.put(Items.GOLDEN_HELMET, Items.CHAINMAIL_HELMET);
        UPGRADE_MAP.put(Items.CHAINMAIL_HELMET, Items.IRON_HELMET);
        UPGRADE_MAP.put(Items.IRON_HELMET, Items.DIAMOND_HELMET);
        UPGRADE_MAP.put(Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);

        UPGRADE_MAP.put(Items.LEATHER_CHESTPLATE, Items.GOLDEN_CHESTPLATE);
        UPGRADE_MAP.put(Items.GOLDEN_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE);
        UPGRADE_MAP.put(Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE);
        UPGRADE_MAP.put(Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE);
        UPGRADE_MAP.put(Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);

        UPGRADE_MAP.put(Items.LEATHER_LEGGINGS, Items.GOLDEN_LEGGINGS);
        UPGRADE_MAP.put(Items.GOLDEN_LEGGINGS, Items.CHAINMAIL_LEGGINGS);
        UPGRADE_MAP.put(Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS);
        UPGRADE_MAP.put(Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS);
        UPGRADE_MAP.put(Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);

        UPGRADE_MAP.put(Items.LEATHER_BOOTS, Items.GOLDEN_BOOTS);
        UPGRADE_MAP.put(Items.GOLDEN_BOOTS, Items.CHAINMAIL_BOOTS);
        UPGRADE_MAP.put(Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS);
        UPGRADE_MAP.put(Items.IRON_BOOTS, Items.DIAMOND_BOOTS);
        UPGRADE_MAP.put(Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
    }

    public BlessingOfVanishingEnchant() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{
                EquipmentSlot.MAINHAND, EquipmentSlot.HEAD,
                EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        MinecraftForge.EVENT_BUS.register(this);  // 注册事件总线
    }
    @Override
    public boolean isTreasureOnly() {
        return true; // 允许在附魔台获得该附魔
    }
    @Override
    public boolean isDiscoverable() {
        return true; // 允许该附魔在附魔表中出现
    }
    @Override
    public int getMaxLevel() {
        return 1;  // 附魔仅需一级
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.isDamageableItem() || stack.getItem() instanceof ArmorItem;  // 可以附魔到武器和盔甲上
    }

    // 玩家死亡时，升级装备
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 升级装备栏和快捷栏中的装备
            upgradeItems(player, player.getInventory().items);
            upgradeItems(player, player.getInventory().armor);
        }
    }

    // 升级装备方法
    private void upgradeItems(Player player, Iterable<ItemStack> items) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(this, stack) > 0) {
                Item upgradedItem = UPGRADE_MAP.get(stack.getItem());
                if (upgradedItem != null) {
                    // 创建升级后的物品
                    ItemStack upgradedStack = new ItemStack(upgradedItem);
                    // 继承原装备的附魔
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                    EnchantmentHelper.setEnchantments(enchantments, upgradedStack);
                    // 复制原来的耐久度
                    upgradedStack.setDamageValue(stack.getDamageValue());

                    // 替换玩家物品栏中的物品
                    player.getInventory().setItem(i, upgradedStack);  // 用升级后的物品替换

                    // 移除“消失祝福”附魔
                    Map<Enchantment, Integer> newEnchantments = EnchantmentHelper.getEnchantments(upgradedStack);
                    newEnchantments.remove(ModEnchantments.BLESSING_OF_VANISHING.get());
                    EnchantmentHelper.setEnchantments(newEnchantments, upgradedStack);
                }
            }
        }
    }
}

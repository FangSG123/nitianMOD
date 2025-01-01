package com.ntsw.event.enchantedEvent;


import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class ChuanggelipeiEventHandler {

    // 用于存储玩家的物品
    private static final String INVENTORY_TAG = "ChuanggelipeiInventory";

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 检查玩家是否装备了带有 chuanggelipei 附魔的胸甲
        ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestArmor.isEmpty() || chestArmor.getEnchantmentLevel(ModEnchantments.CHUANGGELIPEI.get()) <= 0) {
            return;
        }

        // 存储玩家的物品
        CompoundTag playerData = player.getPersistentData();
        if (playerData.contains(INVENTORY_TAG)) {
            // 已经有未取回的物品
            player.sendSystemMessage(Component.translatable("message.chuanggelipei.items_already_stored"));
            return;
        }

        // 移除胸甲上的 chuanggelipei 附魔
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(chestArmor);
        enchantments.remove(ModEnchantments.CHUANGGELIPEI.get());
        EnchantmentHelper.setEnchantments(enchantments, chestArmor);

        // 保存玩家的物品，包括更新后的胸甲
        CompoundTag inventoryData = savePlayerInventory(player);
        playerData.put(INVENTORY_TAG, inventoryData);

        // 清空玩家的物品栏
        player.getInventory().clearContent();
    }

    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 检查玩家是否装备了带有 chuanggelipei 附魔的胸甲
        ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestArmor.isEmpty() || chestArmor.getEnchantmentLevel(ModEnchantments.CHUANGGELIPEI.get()) <= 0) {
            return;
        }

        // 清除掉落物品
        event.getDrops().clear();
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        CompoundTag oldData = oldPlayer.getPersistentData();
        CompoundTag newData = newPlayer.getPersistentData();

        if (oldData.contains(INVENTORY_TAG)) {
            newData.put(INVENTORY_TAG, oldData.getCompound(INVENTORY_TAG));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();

        // 获取存储的物品
        CompoundTag playerData = player.getPersistentData();
        if (!playerData.contains(INVENTORY_TAG)) {
            return;
        }

        CompoundTag inventoryData = playerData.getCompound(INVENTORY_TAG);

        // 加载所有物品
        List<ItemStack> allItems = loadPlayerInventory(inventoryData);

        // 在玩家复活点周围生成潜影盒
        BlockPos respawnPos = player.blockPosition();
        ServerLevel level = (ServerLevel) player.level();

        // 将所有物品分成每组最多 27 个，分别存储到潜影盒中
        int itemsPerBox = 27;
        int totalBoxes = (allItems.size() + itemsPerBox - 1) / itemsPerBox; // 向上取整
        int boxCount = 0;

        for (int i = 0; i < totalBoxes; i++) {
            int start = i * itemsPerBox;
            int end = Math.min(start + itemsPerBox, allItems.size());
            List<ItemStack> itemsForBox = allItems.subList(start, end);

            BlockPos shulkerBoxPos = findValidPositionAround(level, respawnPos, boxCount);
            if (shulkerBoxPos == null) {
                // 找不到合适的位置
                player.sendSystemMessage(Component.translatable("message.chuanggelipei.no_space_for_shulker_box"));
                break;
            }

            // 生成潜影盒
            DyeColor color = DyeColor.values()[new Random().nextInt(DyeColor.values().length)];
            BlockState shulkerBoxState = ShulkerBoxBlock.getBlockByColor(color).defaultBlockState();
            level.setBlock(shulkerBoxPos, shulkerBoxState, 3);

            // 设置潜影盒的物品
            ShulkerBoxBlockEntity shulkerBoxEntity = (ShulkerBoxBlockEntity) level.getBlockEntity(shulkerBoxPos);
            if (shulkerBoxEntity != null) {
                NonNullList<ItemStack> items = NonNullList.withSize(shulkerBoxEntity.getContainerSize(), ItemStack.EMPTY);
                for (int j = 0; j < itemsForBox.size(); j++) {
                    items.set(j, itemsForBox.get(j));
                }
                for (int j = 0; j < items.size(); j++) {
                    shulkerBoxEntity.setItem(j, items.get(j));
                }
                shulkerBoxEntity.setChanged();
            }

            boxCount++;
        }

        // 移除存储的物品数据
        playerData.remove(INVENTORY_TAG);
    }

    private static CompoundTag savePlayerInventory(Player player) {
        CompoundTag compoundTag = new CompoundTag();

        // 保存主物品栏和热键栏
        ListTag inventoryTag = new ListTag();
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                stack.save(itemTag);
                inventoryTag.add(itemTag);
            }
        }
        compoundTag.put("Inventory", inventoryTag);

        // 保存盔甲槽位
        ListTag armorTag = new ListTag();
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            ItemStack stack = player.getInventory().armor.get(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                stack.save(itemTag);
                armorTag.add(itemTag);
            }
        }
        compoundTag.put("Armor", armorTag);

        // 保存副手槽位
        ListTag offhandTag = new ListTag();
        for (int i = 0; i < player.getInventory().offhand.size(); i++) {
            ItemStack stack = player.getInventory().offhand.get(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                stack.save(itemTag);
                offhandTag.add(itemTag);
            }
        }
        compoundTag.put("Offhand", offhandTag);

        return compoundTag;
    }

    private static List<ItemStack> loadPlayerInventory(CompoundTag compoundTag) {
        List<ItemStack> allItems = new ArrayList<>();

        // 加载主物品栏和热键栏
        ListTag inventoryTag = compoundTag.getList("Inventory", 10);
        for (int i = 0; i < inventoryTag.size(); i++) {
            CompoundTag itemTag = inventoryTag.getCompound(i);
            ItemStack stack = ItemStack.of(itemTag);
            allItems.add(stack);
        }

        // 加载盔甲槽位
        ListTag armorTag = compoundTag.getList("Armor", 10);
        for (int i = 0; i < armorTag.size(); i++) {
            CompoundTag itemTag = armorTag.getCompound(i);
            ItemStack stack = ItemStack.of(itemTag);
            allItems.add(stack);
        }

        // 加载副手槽位
        ListTag offhandTag = compoundTag.getList("Offhand", 10);
        for (int i = 0; i < offhandTag.size(); i++) {
            CompoundTag itemTag = offhandTag.getCompound(i);
            ItemStack stack = ItemStack.of(itemTag);
            allItems.add(stack);
        }

        return allItems;
    }

    private static BlockPos findValidPositionAround(ServerLevel level, BlockPos centerPos, int index) {
        int radius = 2 + index / 4; // 每四个盒子扩大一次半径
        int angle = (index % 4) * 90; // 每个盒子相隔90度
        double radians = Math.toRadians(angle);
        int dx = (int) (Math.cos(radians) * radius);
        int dz = (int) (Math.sin(radians) * radius);
        BlockPos pos = centerPos.offset(dx, 0, dz);
        if (level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above())) {
            return pos;
        } else {
            // 如果该位置不可用，尝试向上寻找可用位置
            for (int dy = 1; dy <= 5; dy++) {
                BlockPos newPos = pos.above(dy);
                if (level.isEmptyBlock(newPos) && level.isEmptyBlock(newPos.above())) {
                    return newPos;
                }
            }
        }
        return null;
    }
}

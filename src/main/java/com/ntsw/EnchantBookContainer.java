package com.ntsw;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;

public class EnchantBookContainer extends AbstractContainerMenu {
    private final Container storageInventory;
    private boolean isBroadcasting = false; // 添加标志位

    // 构造器，创建一个 3x9 的独立存储容器
    public EnchantBookContainer(int id, Inventory playerInventory) {
        super(MenuType.GENERIC_9x3, id);

        // 初始化自定义存储容器，大小为 27
        this.storageInventory = new SimpleContainer(27) {
            @Override
            public void setChanged() {
                super.setChanged();
                if (!isBroadcasting) { // 检查是否正在广播
                    broadcastChanges();
                }
            }
        };

        // 添加自定义存储槽
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(storageInventory, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        // 添加玩家背包槽（27 个背包槽）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 添加玩家快捷栏槽（9个快捷栏槽）
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true; // 允许玩家始终访问容器
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            // 物品在自定义存储槽（0-26）
            if (index < 27) {
                // 尝试将物品移动到玩家背包槽位
                if (!this.moveItemStackTo(stackInSlot, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 物品在玩家背包槽
            else if (index >= 27) {
                // 尝试将物品移动到自定义存储槽（0-26）
                if (!this.moveItemStackTo(stackInSlot, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // 如果槽位为空，则清空槽
            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged(); // 更新槽位状态
            }
        }
        return itemStack;
    }

    @Override
    public void broadcastChanges() {
        if (isBroadcasting) return; // 如果已经在广播，直接返回
        try {
            isBroadcasting = true;
            super.broadcastChanges();
            for (int i = 0; i < this.slots.size(); i++) {
                Slot slot = this.slots.get(i);
                if (slot.hasItem()) {
                    slot.setChanged(); // 更新槽位状态
                }
            }
        } finally {
            isBroadcasting = false;
        }
    }
}

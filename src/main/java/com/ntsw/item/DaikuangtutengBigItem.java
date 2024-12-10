// DaikuangtutengItem.java

package com.ntsw.item;

import com.ntsw.event.DaikuangtutengHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DaikuangtutengBigItem extends Item {
    public DaikuangtutengBigItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // 触发右键效果，发送网络消息
            DaikuangtutengHandler.triggerEffect(player);
        }
        return super.use(level, player, hand);
    }
    public static void updateModelData(ItemStack stack, double accumulatedDamage) {
        if (accumulatedDamage > 80) {
            stack.getOrCreateTag().putInt("CustomModelData", 1001);
        } else {
            if (stack.hasTag()) {
                stack.getTag().remove("CustomModelData");
                if (stack.getTag().isEmpty()) {
                    stack.setTag(null);
                }
            }
        }
    }
}

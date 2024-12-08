// DaikuangtutengItem.java

package com.ntsw.item;

import com.ntsw.event.DaikuangtutengHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class DaikuangtutengItem extends Item {
    public DaikuangtutengItem(Properties properties) {
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
}

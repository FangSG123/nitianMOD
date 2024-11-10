package com.ntsw.item;

import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class PDDzhidun extends ShieldItem {
    private static final int MAX_DURABILITY = 60000;

    public PDDzhidun(Properties properties) {
        super(properties.durability(MAX_DURABILITY));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK; // 使用盾牌的防御动画
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // 最大使用持续时间，类似盾牌
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            // 开始使用物品（进入防御模式）
            player.startUsingItem(hand);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        int currentDurability = stack.getMaxDamage() - stack.getDamageValue();
        tooltip.add(Component.literal("耐久: " + currentDurability + "/" + stack.getMaxDamage()));

        CompoundTag nbt = stack.getOrCreateTag();
        int defenseCount = nbt.getInt("DefenseCount");
        tooltip.add(Component.literal("防御次数: " + defenseCount));
    }
}

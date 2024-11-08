package com.ntsw.item;

import com.ntsw.entity.ShikuaiEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;

public class ShikuaiItem extends SnowballItem {

    public ShikuaiItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 在客户端和服务器上都播放投掷声音
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            System.out.println("summon");
            // 在服务器上生成并投掷 ShikuaiEntity 实体
            ShikuaiEntity shikuaiEntity = new ShikuaiEntity(level, player);
            shikuaiEntity.setItem(itemstack);
            shikuaiEntity.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            shikuaiEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(shikuaiEntity);
        }

        // 消耗一个物品
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}

package com.ntsw.item;

import com.ntsw.ModEntity;
import com.ntsw.ntsw.entity.HeiManBaEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FeiJiPiao extends Item {
    public FeiJiPiao(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 检查是否在服务器端进行
        if (!level.isClientSide()) {
            summonEntities(level, player);
            itemStack.shrink(1); // 使用掉一个图腾
        }

        return InteractionResultHolder.success(itemStack);
    }

    public void summonEntities(Level level, Player player) {
        for (int i = 0; i < 10; i++) {
            HeiManBaEntity heiManBa = new HeiManBaEntity(ModEntity.HeiManBa_Entity.get(), player.level());
            heiManBa.setPos(player.getX() + (level.random.nextDouble() - 0.5) * 10,
                    player.getY() + 10,
                    player.getZ() + (level.random.nextDouble() - 0.5) * 10);
            level.addFreshEntity(heiManBa);
        }
    }
}

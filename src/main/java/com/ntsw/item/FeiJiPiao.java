package com.ntsw.item;

import com.ntsw.ModEntitys;
import com.ntsw.ModSounds;
import com.ntsw.entity.HeiManBaEntity;
import net.minecraft.sounds.SoundSource;
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
        System.out.println("use() method called, isClientSide: " + level.isClientSide()); // 检查是否调用了方法
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            //System.out.println("Server side: Summoning entities"); // 检查是否在服务器端
            summonEntities(level, player);
            itemStack.shrink(1); // 使用掉一个图腾
        } else {
            //System.out.println("Client side: Playing sound"); // 检查是否在客户端
            level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.HeiManBa_Summon.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        return InteractionResultHolder.success(itemStack);
    }

    public void summonEntities(Level level, Player player) {
        for (int i = 0; i < 10; i++) {
            HeiManBaEntity heiManBa = new HeiManBaEntity(ModEntitys.HeiManBa_Entity.get(), player.level());
            if(player.getY() < 40) {
                heiManBa.setPos(player.getX() + (level.random.nextDouble() - 0.5) * 10,
                        player.getY(),
                        player.getZ() + (level.random.nextDouble() - 0.5) * 10);
            }
            else {
                heiManBa.setPos(player.getX() + (level.random.nextDouble() - 0.5) * 10,
                        player.getY() + 25,
                        player.getZ() + (level.random.nextDouble() - 0.5) * 10);
            }
            level.addFreshEntity(heiManBa);
        }
    }
}

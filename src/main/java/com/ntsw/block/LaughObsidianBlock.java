package com.ntsw.block;

import com.ntsw.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.List;
import java.util.Random;

public class LaughObsidianBlock extends Block {

    private static final List<RegistryObject<SoundEvent>> SOUND_EVENTS = List.of(
            ModSounds.SiRenLaugh1,
            ModSounds.SiRenLaugh2,
            ModSounds.SiRenLaugh3,
            ModSounds.SiRenLaugh4,
            ModSounds.SiRenLaugh5,
            ModSounds.SiRenLaugh6,
            ModSounds.SiRenLaugh7
            // 添加更多的声音事件
    );

    public LaughObsidianBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE)
                .strength(3.0f, 3.0f)
                .sound(SoundType.STONE));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide()) {
            // 随机播放笑声
            Random rand = new Random();
            SoundEvent sound = SOUND_EVENTS.get(rand.nextInt(SOUND_EVENTS.size())).get();
            world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

            // 扣除玩家一半生命值
            if (player.getHealth() > 1.0F) { // 确保不会直接致死
                player.hurt(player.damageSources().magic(), player.getHealth() / 2.0F);
            }else {
                player.hurt(player.damageSources().magic(), 1);
            }

            // 关闭范围内怪物的 AI
            world.getEntitiesOfClass(Mob.class,
                            player.getBoundingBox().inflate(5.0)) // 以玩家为中心，半径为5格的范围
                    .forEach(mob -> mob.setNoAi(true)); // 设置怪物 AI 为关闭
        }
        return InteractionResult.SUCCESS;
    }
}

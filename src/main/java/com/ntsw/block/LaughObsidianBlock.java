package com.ntsw.block;

import com.ntsw.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
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
            Random rand = new Random();
            SoundEvent sound = SOUND_EVENTS.get(rand.nextInt(SOUND_EVENTS.size())).get();
            world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}

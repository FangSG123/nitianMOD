package com.ntsw.block;

import com.ntsw.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
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
import org.jetbrains.annotations.NotNull;

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
        super(BlockBehaviour.Properties.copy(Blocks.CRYING_OBSIDIAN)
                .strength(3.0f, 3.0f)
                .sound(SoundType.STONE));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!world.isClientSide()) {
            // 随机播放笑声
            Random rand = new Random();
            SoundEvent sound = SOUND_EVENTS.get(rand.nextInt(SOUND_EVENTS.size())).get();
            int randomNum = (int) rand.nextFloat(4);
            //System.out.println("randomNum = " + randomNum);
            world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            switch(randomNum)
            {
                case 0:
                    if (player.getHealth() > 1.0F) { // 确保不会直接致死
                        player.hurt(player.damageSources().magic(), player.getHealth() / 2.0F);
                    }else {
                        player.hurt(player.damageSources().magic(), 1);
                    }
                    break;
                case 1:
                    // 关闭范围内怪物的 AI
                    world.getEntitiesOfClass(Mob.class,
                                    player.getBoundingBox().inflate(5.0)) // 以玩家为中心，半径为5格的范围
                            .forEach(mob -> mob.setNoAi(true)); // 设置怪物 AI 为关闭
                    break;
                case 2:
                {
                    if (!world.isClientSide && world instanceof ServerLevel serverWorld) {
                        BlockPos playerPos = player.blockPosition();
                        Random random = new Random();

                        for (int i = 0; i < 5; i++) {
                            // 创建新的僵尸实体
                            Creeper creeper = new Creeper(EntityType.CREEPER,serverWorld);

                            // 计算玩家周围的随机偏移（在5格范围内）
                            double offsetX = (random.nextDouble() - 0.5) * 10; // -5到5之间的随机值
                            double offsetZ = (random.nextDouble() - 0.5) * 10;
                            BlockPos spawnPos = playerPos.offset((int) offsetX, 0, (int)offsetZ);

                            // 设置僵尸的位置
                            creeper.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

                            // 将僵尸添加到世界中
                            serverWorld.addFreshEntity(creeper);
                        }
                    }
                    break;
                }
                case 3:
                {
                    if(!world.isClientSide)
                    {
                        ServerLevel serverWorld = (ServerLevel) world;
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT,serverWorld);
                        lightningBolt.setPos(pos.getX(), pos.getY(), pos.getZ());
                        serverWorld.addFreshEntity(lightningBolt);
                    }
                }
            }

        }
        return InteractionResult.SUCCESS;
    }
}

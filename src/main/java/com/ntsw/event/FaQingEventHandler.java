package com.ntsw.event;

import com.ntsw.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mod.EventBusSubscriber(modid = "nitian", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FaQingEventHandler {

    private static final int SPAWN_COOLDOWN = 20; // 设置冷却时间为20 tick
    private static int cooldownTimer = 0; // 冷却计时器，单位为tick

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Level world = player.level();

            // 仅在服务器端执行
            if (!world.isClientSide) {
                // 检查玩家是否拥有“faqing”效果
                if (player.hasEffect(ModEffects.FAQING.get())) {
                    int faqingLevel = Objects.requireNonNull(player.getEffect(ModEffects.FAQING.get())).getAmplifier(); // 获取FAQING药水效果的等级

                    // 检查玩家是否处于潜行状态
                    if (player.isShiftKeyDown()) { // 使用 isShiftKeyDown() 检查潜行状态
                        // 定义一个检测半径
                        double radius = 1.0;
                        // 获取玩家周围的所有实体
                        List<Entity> nearbyEntities = world.getEntities(player, player.getBoundingBox().inflate(radius));

                        // 只有冷却时间结束时才能生成小村民或小僵尸
                        if (cooldownTimer > 0) {
                            cooldownTimer--; // 冷却时间递减
                        }

                        for (Entity entity : nearbyEntities) {
                            // 如果是动物并处于求爱模式
                            if (entity instanceof Animal animal && isAnimalInLoveMode(animal)) {
                                // 生成幼崽
                                spawnBabyAnimal(animal);

                                // 清除动物的求爱模式
                                clearAnimalLoveMode(animal);

                                // 如果FAQING药水效果等级为1，清除效果
                                if (faqingLevel == 0) {
                                    player.removeEffect(ModEffects.FAQING.get());
                                    System.out.println("cleareffect");
                                }
                            }

                            // 如果是村民并且冷却时间结束
                            if (entity instanceof Villager villager && cooldownTimer == 0) {
                                spawnBabyVillager(villager);
                                cooldownTimer = SPAWN_COOLDOWN; // 重置冷却计时器

                                // 如果FAQING药水效果等级为1，清除效果
                                if (faqingLevel == 0) {
                                    player.removeEffect(ModEffects.FAQING.get());
                                }
                            }

                            // 如果是僵尸并且冷却时间结束
                            if (entity instanceof Zombie zombie && cooldownTimer == 0) {
                                spawnBabyZombie(zombie);
                                cooldownTimer = SPAWN_COOLDOWN; // 重置冷却计时器

                                // 如果FAQING药水效果等级为1，清除效果
                                if (faqingLevel == 0) {
                                    player.removeEffect(ModEffects.FAQING.get());
                                }
                            }

                            if (entity instanceof EnderDragon enderDragon && cooldownTimer == 0) {
                                spawnDragonEgg(world, enderDragon);
                                cooldownTimer = SPAWN_COOLDOWN; // 重置冷却计时器

                                // 如果 FAQING 药水效果等级为1，清除效果
                                if (faqingLevel == 0) {
                                    player.removeEffect(ModEffects.FAQING.get());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 检查动物是否处于求爱模式
    private static boolean isAnimalInLoveMode(Animal animal) {
        return animal.isInLove();
    }

    // 在动物位置生成幼崽
    private static void spawnBabyAnimal(Animal animal) {
        // 使用 instanceof 检查 Animal 是否为 AgeableMob 的实例
        if (animal != null) {
            // 继续处理 ageableMob 逻辑
            ServerLevel serverLevel = (ServerLevel) animal.level();

            // 强制转换 EntityType
            EntityType<? extends AgeableMob> type = (EntityType<? extends AgeableMob>) animal.getType();

            // 创建并生成幼崽
            AgeableMob baby = type.create(serverLevel);
            if (baby != null) {
                baby.setBaby(true);  // 设置为婴儿
                baby.setPos(animal.getX(), animal.getY(), animal.getZ());
                baby.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(animal.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                serverLevel.addFreshEntity(baby);  // 将幼崽添加到世界中
            }
        }
    }
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Animal animal && animal.hasEffect(ModEffects.FAQING.get())) {
            animal.setInLoveTime(6000);
        }
    }
    // 在村民位置生成小村民
    private static void spawnBabyVillager(Villager villager) {
        ServerLevel serverLevel = (ServerLevel) villager.level();

        // 创建并生成小村民
        Villager babyVillager = EntityType.VILLAGER.create(serverLevel);
        if (babyVillager != null) {
            babyVillager.setBaby(true);  // 设置为婴儿
            babyVillager.setPos(villager.getX(), villager.getY(), villager.getZ());
            babyVillager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(villager.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            serverLevel.addFreshEntity(babyVillager);  // 将小村民添加到世界中
        }
    }

    // 在僵尸位置生成小僵尸
    private static void spawnBabyZombie(Zombie zombie) {
        ServerLevel serverLevel = (ServerLevel) zombie.level();

        // 创建并生成小僵尸
        Zombie babyZombie = EntityType.ZOMBIE.create(serverLevel);
        if (babyZombie != null) {
            babyZombie.setBaby(true);  // 设置为婴儿
            babyZombie.setPos(zombie.getX(), zombie.getY(), zombie.getZ());
            babyZombie.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(zombie.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            serverLevel.addFreshEntity(babyZombie);  // 将小僵尸添加到世界中
        }
    }

    private static void spawnDragonEgg(Level world, EnderDragon enderDragon) {
        ServerLevel serverLevel = (ServerLevel) world;

        // 定义龙蛋的位置，这里假设基岩传送门在末影龙的附近
        BlockPos dragonEggPos = findBedrockPortalPosition(serverLevel, enderDragon);

        if (dragonEggPos != null) {
            // 设置龙蛋块
            BlockState dragonEggBlock = Blocks.DRAGON_EGG.defaultBlockState();
            serverLevel.setBlock(dragonEggPos, dragonEggBlock, 3);
            System.out.println("生成龙蛋于位置: " + dragonEggPos);
        }
    }

    // 寻找基岩传送门的位置
    private static BlockPos findBedrockPortalPosition(ServerLevel serverLevel, EnderDragon enderDragon) {
        // 这里假设基岩传送门在末影龙的当前位置上方5个区块
        // 您可以根据实际需求调整位置逻辑

        BlockPos dragonPos = enderDragon.blockPosition();
        BlockPos dragonEggPos = dragonPos.above(-2);

        // 检查位置是否为空
        if (serverLevel.getBlockState(dragonEggPos).isAir()) {
            return dragonEggPos;
        }

        // 如果位置不为空，可以尝试其他位置或返回 null
        // 这里简单返回 null，您可以根据需要实现更复杂的逻辑
        return null;
    }

    // 清除动物的求爱模式
    private static void clearAnimalLoveMode(Animal animal) {
        animal.resetLove();
    }
}

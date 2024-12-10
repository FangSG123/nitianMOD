package com.ntsw.event;

import com.ntsw.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "nitian", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FaQingEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Level world = player.level();

            // 仅在服务器端执行
            if (!world.isClientSide) {
                // 检查玩家是否拥有“faqing”效果
                if (player.hasEffect(ModEffects.FAQING.get())) {
                    // 检查玩家是否处于潜行状态
                    if (player.isShiftKeyDown()) { // 使用 isShiftKeyDown() 检查潜行状态
                        // 定义一个检测半径
                        double radius = 3.0;
                        // 获取玩家周围的所有实体
                        List<Entity> nearbyEntities = world.getEntities(player, player.getBoundingBox().inflate(radius));

                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof Animal animal && isAnimalInLoveMode(animal)) {
                                // 生成幼崽
                                spawnBabyAnimal(animal);

                                // 清除动物的求爱模式
                                clearAnimalLoveMode(animal);
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
        if (animal instanceof AgeableMob) {
            AgeableMob ageableMob = (AgeableMob) animal;  // 转换为 AgeableMob 类型
            // 继续处理 ageableMob 逻辑
            ServerLevel serverLevel = (ServerLevel) animal.level();

            // 强制转换 EntityType
            EntityType<? extends AgeableMob> type = (EntityType<? extends AgeableMob>) ageableMob.getType();

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


        // 清除动物的求爱模式
    private static void clearAnimalLoveMode(Animal animal) {
        animal.resetLove();
    }
}

package com.ntsw.item;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE)
public class MCToW extends Item {
    private static final Set<Slime> generatedSlimes = new HashSet<>(); // 存储生成的史莱姆
    private static int slimeKillCount = 0;
    private static long startTime = 0;
    private static int playerScore = 0;
    private static Player activePlayer = null;

    public MCToW(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this); // 注册事件监听器
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            ServerLevel world = (ServerLevel) context.getLevel();
            Player player = context.getPlayer();
            Vec3 clickLocation = context.getClickLocation();

            Set<BlockPos> usedPositions = new HashSet<>();
            Random random = new Random();
            int spawnedSlimes = 0;

            while (spawnedSlimes < 10) { // 生成10个史莱姆
                int offsetX = random.nextInt(3) - 1;
                int offsetZ = random.nextInt(3) - 1;
                int offsetY = random.nextInt(3);

                BlockPos spawnPos = new BlockPos(
                        (int) clickLocation.x + offsetX,
                        (int) clickLocation.y + offsetY,
                        (int) clickLocation.z + offsetZ
                );

                if (!usedPositions.contains(spawnPos)) {
                    usedPositions.add(spawnPos);

                    Slime slime = EntityType.SLIME.create(world);
                    if (slime != null) {
                        slime.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                        slime.setPersistenceRequired();
                        slime.setNoAi(true);
                        slime.setNoGravity(true);
                        slime.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1.0);
                        slime.setHealth(1.0f);
                        world.addFreshEntity(slime);

                        generatedSlimes.add(slime); // 添加到生成的史莱姆集合
                        spawnedSlimes++;
                    }
                }
            }

            if (player != null) {
                startTime = System.currentTimeMillis(); // 记录启动时间
                slimeKillCount = 0; // 重置击杀计数
                playerScore = 0; // 重置积分
                activePlayer = player; // 记录当前玩家
            }
        }
        return InteractionResult.SUCCESS;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player && event.getEntity() instanceof Slime) {
            Slime slime = (Slime) event.getEntity();
            if (generatedSlimes.contains(slime)) {
                slimeKillCount++;
                playerScore += 10; // 每击杀一只史莱姆增加10分
                generatedSlimes.remove(slime); // 从集合中移除已被击杀的史莱姆

                // 检查是否所有史莱姆已被击杀完毕
                if (generatedSlimes.isEmpty()) {
                    endScoring(); // 提前结束计分并发送积分
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Slime && generatedSlimes.contains((Slime) event.getEntity())) {
            event.getDrops().clear(); // 清空掉落物列表，确保无掉落物
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        // 每2个tick扣除1分
        if (startTime > 0 && System.currentTimeMillis() - startTime <= 10000) {
            if (event.phase == TickEvent.Phase.END && event.getServer().getTickCount() % 2 == 0) {
                playerScore = Math.max(playerScore - 1, 0); // 扣除1分，但最低为0分
            }
        }

        // 检查是否超过10秒
        if (startTime > 0 && System.currentTimeMillis() - startTime > 10000) {
            endScoring(); // 正常结束计分
        }
    }

    private static void endScoring() {
        startTime = 0;

        for (Slime slime : generatedSlimes) {
            slime.remove(Entity.RemovalReason.DISCARDED); // 移除未击杀的史莱姆
        }
        generatedSlimes.clear();

        if (activePlayer != null) {
            activePlayer.sendSystemMessage(Component.literal("你的总积分: " + playerScore)); // 发送积分信息
            activePlayer = null;
        }
    }
}

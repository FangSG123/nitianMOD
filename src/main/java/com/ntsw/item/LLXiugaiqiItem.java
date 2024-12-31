package com.ntsw.item;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

public class LLXiugaiqiItem extends Item {

    private static final String MODE_KEY = "Mode";  // 用来保存模式到 NBT
    private static final Random RANDOM = new Random();

    public LLXiugaiqiItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 从 stack 的 NBT 中获取模式，若未设置过则默认设为 1
        int mode = stack.getOrCreateTag().getInt(MODE_KEY);
        if (mode == 0) {
            mode = 1;
            stack.getOrCreateTag().putInt(MODE_KEY, mode);
        }

        // 潜行 + 右键 -> 切换模式
        if (player.isShiftKeyDown()) {
            mode++;
            // 如果模式超过 9，就回到 1
            if (mode > 9) {
                mode = 1;
            }
            stack.getOrCreateTag().putInt(MODE_KEY, mode);

            if (!level.isClientSide) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable(
                                "切换模式 -> " + mode
                        ),
                        true
                );
            }
            return InteractionResultHolder.success(stack);
        }

        // 普通右键 -> 执行当前模式逻辑 (仅服务端执行)
        if (!level.isClientSide) {
            switch (mode) {
                case 1 -> executeMode1((ServerPlayer) player);
                case 2 -> executeMode2((ServerPlayer) player);
                case 3 -> executeMode3((ServerPlayer) player);
                case 4 -> executeMode4((ServerPlayer) player);
                case 5 -> executeMode5((ServerPlayer) player);
                case 6 -> executeMode6((ServerPlayer) player);
                case 7 -> executeMode7((ServerPlayer) player);
                case 8 -> executeMode8((ServerPlayer) player);
                case 9 -> executeMode9((ServerPlayer) player);  // 新增模式9
                default -> {
                    // 理论上不会触发
                }
            }
        }

        return InteractionResultHolder.success(stack);
    }

    /**
     * ======================== 原有模式 1 - 5 ========================
     */

    /**
     * 模式1：消耗16颗绿宝石，给予16秒 抗性提升10 / 力量10 / 速度10
     */
    private void executeMode1(ServerPlayer player) {
        if (!removeEmeralds(player, 16)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        // amplifier=9 -> 10级效果
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 16 * 20, 9));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 16 * 20, 9));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 16 * 20, 9));
    }

    /**
     * 模式2：消耗32颗绿宝石，随机击杀周围一只生物
     */
    private void executeMode2(ServerPlayer player) {
        if (!removeEmeralds(player, 32)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        ServerLevel serverLevel =(ServerLevel) player.level();
        List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(10),
                e -> e != player && e.isAlive() && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(e)
        );

        if (!nearbyEntities.isEmpty()) {
            LivingEntity target = nearbyEntities.get(RANDOM.nextInt(nearbyEntities.size()));
            target.kill(); // 直接击杀
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "成功随机击杀一只 " + target.getName().getString()), true);
        } else {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("附近没有可击杀的生物！"), true);
        }
    }

    /**
     * 模式3：消耗6颗绿宝石，随机获得1个物品
     */
    private void executeMode3(ServerPlayer player) {
        if (!removeEmeralds(player, 6)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        // 获得1个随机物品
        ItemStack randomStack = getRandomItemStack(1);
        player.getInventory().add(randomStack);
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "你获得了随机物品: " + randomStack.getItem()), true);
    }

    /**
     * 模式4：消耗648颗绿宝石，随机获得100个物品
     */
    private void executeMode4(ServerPlayer player) {
        if (!removeEmeralds(player, 65)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        // 获得100个随机物品
        for (int i = 0; i < 100; i++) {
            ItemStack randomStack = getRandomItemStack(1);
            player.getInventory().add(randomStack);
        }
        player.displayClientMessage(net.minecraft.network.chat.Component.literal("成功获得100个随机物品！"), true);
    }

    /**
     * 模式5：消耗666颗绿宝石，将玩家调成创造模式
     */
    private void executeMode5(ServerPlayer player) {
        if (!removeEmeralds(player, 666)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        player.setGameMode(GameType.CREATIVE);
        player.displayClientMessage(net.minecraft.network.chat.Component.literal("你已切换为创造模式！"), true);
    }

    /**
     * ======================== 原有模式 6 - 8 ========================
     */

    /**
     * 模式6：消耗 8 颗绿宝石，给予 30 秒 的 速度10
     */
    private void executeMode6(ServerPlayer player) {
        if (!removeEmeralds(player, 8)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        // 速度10 -> amplifier = 9
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 9));
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "你获得了30秒速度 X(10级)！"), true);
    }

    /**
     * 模式7：消耗 324 颗绿宝石，获得 10 分钟 的 抗性提升4、力量4、速度3、跳跃提升2、夜视1
     */
    private void executeMode7(ServerPlayer player) {
        if (!removeEmeralds(player, 324)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        int duration = 12000; // 10 分钟
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 3)); // 抗性提升4
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 3));      // 力量4
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 2));    // 速度3
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, 1));              // 跳跃提升2
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, duration, 0));      // 夜视1

        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "获得了10分钟 抗性提升4 / 力量4 / 速度3 / 跳跃提升2 / 夜视1！"), true);
    }

    /**
     * 模式8：消耗 100 颗绿宝石，解锁所有进度（成就）
     */
    private void executeMode8(ServerPlayer player) {
        if (!removeEmeralds(player, 100)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        ServerLevel serverLevel =(ServerLevel) player.level();
        var advancements = serverLevel.getServer().getAdvancements();

        for (Advancement adv : advancements.getAllAdvancements()) {
            var progress = player.getAdvancements().getOrStartProgress(adv);
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(adv, criterion);
            }
        }
        player.displayClientMessage(net.minecraft.network.chat.Component.literal("你已解锁所有成就/进度！"), true);
    }

    /**
     * ======================== 新增模式 9 ========================
     */

    /**
     * 模式9：消耗 64 颗绿宝石，清除玩家为中心 16×16×16 范围的方块
     */
    private void executeMode9(ServerPlayer player) {
        if (!removeEmeralds(player, 64)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("绿宝石不足，操作失败！"), true);
            return;
        }
        ServerLevel serverLevel =(ServerLevel) player.level();
        BlockPos playerPos = player.blockPosition();

        // 假设半径为 8，则一共是 16 格 ( -8 ~ +7 )
        int radius = 8;

        // 循环替换
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    BlockPos targetPos = playerPos.offset(x, y, z);
                    // 这里直接替换为空气，如需排除基岩/命令方块等可以加判断
                    serverLevel.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "已清除玩家周围 16×16×16 范围内的方块！"), true);
    }

    /**
     * ======================== 工具方法 ========================
     */

    /**
     * 从玩家背包里移除指定数量的绿宝石，如果成功则返回 true，否则 false
     */
    private boolean removeEmeralds(ServerPlayer player, int count) {
        int removed = 0;
        for (ItemStack slotStack : player.getInventory().items) {
            if (slotStack.is(Items.EMERALD)) {
                int slotCount = slotStack.getCount();
                int toRemove = Math.min(slotCount, count - removed);
                slotStack.shrink(toRemove);
                removed += toRemove;
                if (removed >= count) {
                    return true;
                }
            }
        }
        return removed >= count;
    }

    /**
     * 从已注册物品池里随机取一个物品，并生成指定数量的堆叠
     */
    private ItemStack getRandomItemStack(int size) {
        var items = ForgeRegistries.ITEMS.getValues();
        int idx = RANDOM.nextInt(items.size());
        var item = items.stream().skip(idx).findFirst().orElse(Items.STONE);
        return new ItemStack(item, size);
    }
}

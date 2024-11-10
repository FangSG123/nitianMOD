package com.ntsw.event;

import com.ntsw.Main;
import com.ntsw.ModEffects;
import com.ntsw.ModItems;
import com.ntsw.item.PddItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class PddEventHandler {

    private static final float HEALTH_THRESHOLD = 5.0F;
    private static final float DAMAGE_MULTIPLIER = 0.9F;
    private static final int IMMUNITY_DURATION = 2400; // 120 秒，以 tick 为单位

    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof PddItem) {
            Entity targetEntity = event.getTarget();
            if (targetEntity instanceof Villager villager) {
                if (!player.level().isClientSide) {
                    // 修改村民的交易
                    modifyVillagerTrades(villager, player);
                }
            }
            if (!(targetEntity instanceof LivingEntity)) {
                return; // 如果目标不是 LivingEntity，直接返回
            }

            LivingEntity target = (LivingEntity) targetEntity;

            if (!player.level().isClientSide) {
                // 获取玩家的持久化数据
                CompoundTag playerData = player.getPersistentData();

                // 获取上一次的攻击目标的 UUID
                String lastTargetUUID = playerData.getString("PddLastTargetUUID");
                String currentTargetUUID = target.getStringUUID();

                // 检查攻击目标是否发生变化
                if (!lastTargetUUID.equals(currentTargetUUID)) {
                    // 攻击目标发生了切换，执行清除和重置操作
                    clearZuanshiAndJifen(player);
                    resetPlayerCounters(playerData);

                    // 更新上一次的攻击目标为当前目标
                    playerData.putString("PddLastTargetUUID", currentTargetUUID);
                }

                // 初始化攻击次数和积分数量
                int attackCount = playerData.getInt("PddAttackCount");
                int jifenCount = playerData.getInt("PddJifenCount");

                // 检查是否已发送提示信息
                boolean hasSentDiamondMessage = playerData.getBoolean("HasSentDiamondMessage");
                boolean hasSentJifenMessage = playerData.getBoolean("HasSentJifenMessage");
                boolean hasSentChanceMessage = playerData.getBoolean("HasSentChanceMessage");

                // 攻击次数增加
                attackCount++;
                playerData.putInt("PddAttackCount", attackCount);

                // 检查攻击次数是否达到 200 次
                if (attackCount >= 200) {
                    // 执行特殊操作

                    // 击杀当前目标生物
                    DamageSource damageSource = player.damageSources().fellOutOfWorld();
                    target.hurt(damageSource, Float.MAX_VALUE);

                    // 移除玩家手中的 PddItem
                    player.getInventory().removeItem(heldItem);

                    // 清除所有 zuanshi 和 jifen
                    clearZuanshiAndJifen(player);

                    // 重置计数器
                    resetPlayerCounters(playerData);

                    // 在目标位置产生爆炸
                    target.level().explode(null, target.getX(), target.getY(), target.getZ(), 4.0F, false, Level.ExplosionInteraction.NONE);

                    // 发送提示信息
                    player.sendSystemMessage(Component.literal("ERRORERRORERRORERROR").withStyle(ChatFormatting.RED));

                    event.setCanceled(true); // 取消默认攻击，避免其他效果
                    return;
                }

                if (target.getHealth() < HEALTH_THRESHOLD) {

                    // 第一次发送钻石提示
                    if (!hasSentDiamondMessage) {
                        player.sendSystemMessage(Component.literal("集齐64个钻石可以造成一点伤害").withStyle(ChatFormatting.GREEN));
                        playerData.putBoolean("HasSentDiamondMessage", true);
                    }

                    if (attackCount <= 5) {
                        // 前5次攻击，给予10个钻石
                        ItemStack zuanshi = new ItemStack(ModItems.ZUANSHI.get(), 10);
                        heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                        player.getInventory().add(zuanshi);
                    } else if (attackCount <= 10) {
                        // 接下来5次攻击，给予2个钻石
                        ItemStack zuanshi = new ItemStack(ModItems.ZUANSHI.get(), 2);
                        heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                        player.getInventory().add(zuanshi);
                    } else {
                        if (jifenCount < 63) {
                            // 给予玩家1个积分
                            ItemStack jifen = new ItemStack(ModItems.JIFEN.get());
                            player.getInventory().add(jifen);
                            jifenCount++;
                            heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                            playerData.putInt("PddJifenCount", jifenCount);

                            // 第一次发送积分提示
                            if (!hasSentJifenMessage) {
                                player.sendSystemMessage(Component.literal("集齐64个积分可以兑换一个钻石").withStyle(ChatFormatting.BLUE));
                                playerData.putBoolean("HasSentJifenMessage", true);
                            }
                        } else if (jifenCount == 63) {
                            // 达到63个积分，发送一次性提示
                            if (!hasSentChanceMessage) {
                                player.sendSystemMessage(Component.literal("恭喜你获得抽取100个钻石的机会").withStyle(ChatFormatting.GOLD));
                                playerData.putBoolean("HasSentChanceMessage", true);
                            }
                            jifenCount++; // 增加到64，防止再次进入此条件
                            playerData.putInt("PddJifenCount", jifenCount);
                        } else {
                            heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                            // 已达到最大积分，发送泥土提示并给予泥土
                            player.sendSystemMessage(Component.literal("恭喜你获得泥土 x1").withStyle(ChatFormatting.YELLOW));
                            ItemStack dirt = new ItemStack(Items.DIRT);
                            player.getInventory().add(dirt);
                        }
                    }

                } else {
                    // 造成目标当前生命值的90%伤害
                    float damage = target.getHealth() * DAMAGE_MULTIPLIER;
                    DamageSource damageSource = player.damageSources().playerAttack(player);
                    target.hurt(damageSource, damage);

                    // 施加免疫效果，持续指定的时长
                    target.addEffect(new MobEffectInstance(ModEffects.DAMAGE_IMMUNITY.get(), IMMUNITY_DURATION, 0));

                    // 减少武器耐久度
                    heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof PddItem) {
            if (!player.level().isClientSide) {
                performSelfAttack(player, heldItem);
                event.setCanceled(true); // Prevent default action if any
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof PddItem) {
                if(event.getEntity().getHealth() < 5)
                {
                    event.setAmount(0); // 将伤害量设置为 0
                }
            }
        }
    }

    private static void performSelfAttack(Player player, ItemStack heldItem) {
        // 获取玩家的持久化数据
        CompoundTag playerData = player.getPersistentData();

        // 获取上一次的攻击目标的 UUID
        String lastTargetUUID = playerData.getString("PddLastTargetUUID");
        String currentTargetUUID = player.getUUID().toString(); // Self UUID

        // 检查攻击目标是否发生变化
        if (!lastTargetUUID.equals(currentTargetUUID)) {
            // 攻击目标发生了切换，执行清除和重置操作
            clearZuanshiAndJifen(player);
            resetPlayerCounters(playerData);

            // 更新上一次的攻击目标为当前目标
            playerData.putString("PddLastTargetUUID", currentTargetUUID);
        }

        // 初始化攻击次数和积分数量
        int attackCount = playerData.getInt("PddAttackCount");
        int jifenCount = playerData.getInt("PddJifenCount");

        // 检查是否已发送提示信息
        boolean hasSentDiamondMessage = playerData.getBoolean("HasSentDiamondMessage");
        boolean hasSentJifenMessage = playerData.getBoolean("HasSentJifenMessage");
        boolean hasSentChanceMessage = playerData.getBoolean("HasSentChanceMessage");

        // 攻击次数增加
        attackCount++;
        playerData.putInt("PddAttackCount", attackCount);

        // 检查攻击次数是否达到 200 次
        if (attackCount >= 200) {
            // 执行特殊操作

            // 造成致命伤害
            DamageSource damageSource = player.damageSources().fellOutOfWorld();
            player.hurt(damageSource, Float.MAX_VALUE);

            // 移除玩家手中的 PddItem
            player.getInventory().removeItem(heldItem);

            // 清除所有 zuanshi 和 jifen
            clearZuanshiAndJifen(player);

            // 重置计数器
            resetPlayerCounters(playerData);

            // 在玩家位置产生爆炸
            player.level().explode(null, player.getX(), player.getY(), player.getZ(), 4.0F, false, Level.ExplosionInteraction.NONE);

            // 发送提示信息
            player.sendSystemMessage(Component.literal("ERRORERRORERRORERROR").withStyle(ChatFormatting.RED));

            return;
        }

        if (player.getHealth() < HEALTH_THRESHOLD) {

            // 第一次发送钻石提示
            if (!hasSentDiamondMessage) {
                player.sendSystemMessage(Component.literal("集齐64个钻石可以造成一点伤害").withStyle(ChatFormatting.GREEN));
                playerData.putBoolean("HasSentDiamondMessage", true);
            }

            if (attackCount <= 5) {
                // 前5次攻击，给予10个钻石
                ItemStack zuanshi = new ItemStack(ModItems.ZUANSHI.get(), 10);
                heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                player.getInventory().add(zuanshi);
            } else if (attackCount <= 10) {
                // 接下来5次攻击，给予2个钻石
                ItemStack zuanshi = new ItemStack(ModItems.ZUANSHI.get(), 2);
                heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                player.getInventory().add(zuanshi);
            } else {
                if (jifenCount < 63) {
                    // 给予玩家1个积分
                    ItemStack jifen = new ItemStack(ModItems.JIFEN.get());
                    player.getInventory().add(jifen);
                    jifenCount++;
                    heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    playerData.putInt("PddJifenCount", jifenCount);

                    // 第一次发送积分提示
                    if (!hasSentJifenMessage) {
                        player.sendSystemMessage(Component.literal("集齐64个积分可以兑换一个钻石").withStyle(ChatFormatting.BLUE));
                        playerData.putBoolean("HasSentJifenMessage", true);
                    }
                } else if (jifenCount == 63) {
                    // 达到63个积分，发送一次性提示
                    if (!hasSentChanceMessage) {
                        player.sendSystemMessage(Component.literal("恭喜你获得抽取100个钻石的机会").withStyle(ChatFormatting.GOLD));
                        playerData.putBoolean("HasSentChanceMessage", true);
                    }
                    jifenCount++; // 增加到64，防止再次进入此条件
                    playerData.putInt("PddJifenCount", jifenCount);
                } else {
                    heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    // 已达到最大积分，发送泥土提示并给予泥土
                    player.sendSystemMessage(Component.literal("恭喜你获得泥土 x1").withStyle(ChatFormatting.YELLOW));
                    ItemStack dirt = new ItemStack(Items.DIRT);
                    player.getInventory().add(dirt);
                }
            }

        } else {
            // 造成玩家当前生命值的90%伤害
            float damage = player.getHealth() * DAMAGE_MULTIPLIER;
            DamageSource damageSource = player.damageSources().playerAttack(player);
            player.hurt(damageSource, damage);

            // 施加免疫效果，持续指定的时长
            player.addEffect(new MobEffectInstance(ModEffects.DAMAGE_IMMUNITY.get(),200, 0));

            // 减少武器耐久度
            heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }
    }

    private static void clearZuanshiAndJifen(Player player) {
        if (player.level() instanceof ServerLevel) {
            // 服务器端代码
            ServerLevel level = (ServerLevel) player.level();
            System.out.println("Code is executing.");

// 创建合理的 AABB 边界，覆盖一部分世界或整个世界
// 这里选择了世界的最大高度范围，可以根据需要调整
            AABB boundingBox = new AABB(
                    player.getX() - 1000, 0, player.getZ() - 1000, // 左下角
                    player.getX() + 1000, 256, player.getZ() + 1000 // 右上角
            );

// 获取在 AABB 区域内的所有物品实体
            List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, boundingBox);
            System.out.println("Found " + itemEntities.size() + " items.");

// 遍历找到的物品
            for (ItemEntity itemEntity : itemEntities) {
                ItemStack stack = itemEntity.getItem();
                System.out.println("Found item: " + stack.getItem()); // 打印检测到的物品

                // 判断物品是否是我们需要移除的物品
                if (stack.getItem() == ModItems.ZUANSHI.get() || stack.getItem() == ModItems.JIFEN.get()) {
                    // 移除掉落物
                    itemEntity.remove(Entity.RemovalReason.DISCARDED);
                    System.out.println("Removed item: " + stack.getItem()); // 输出已移除的物品
                }
            }
            // 清除所有玩家物品栏中的相关物品
            for (Player onlinePlayer : level.players()) {
                // 清除主物品栏
                NonNullList<ItemStack> mainInventory = onlinePlayer.getInventory().items;
                for (int i = 0; i < mainInventory.size(); i++) {
                    ItemStack stack = mainInventory.get(i);
                    if (stack.getItem() == ModItems.ZUANSHI.get() || stack.getItem() == ModItems.JIFEN.get()) {
                        mainInventory.set(i, ItemStack.EMPTY);
                    }
                }

                // 清除副手物品
                NonNullList<ItemStack> offhandInventory = onlinePlayer.getInventory().offhand;
                for (int i = 0; i < offhandInventory.size(); i++) {
                    ItemStack stack = offhandInventory.get(i);
                    if (stack.getItem() == ModItems.ZUANSHI.get() || stack.getItem() == ModItems.JIFEN.get()) {
                        offhandInventory.set(i, ItemStack.EMPTY);
                    }
                }

                // 清除盔甲槽位
                NonNullList<ItemStack> armorInventory = onlinePlayer.getInventory().armor;
                for (int i = 0; i < armorInventory.size(); i++) {
                    ItemStack stack = armorInventory.get(i);
                    if (stack.getItem() == ModItems.ZUANSHI.get() || stack.getItem() == ModItems.JIFEN.get()) {
                        armorInventory.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private static void resetPlayerCounters(CompoundTag playerData) {
        playerData.putInt("PddAttackCount", 0);
        playerData.putInt("PddJifenCount", 0);
        playerData.putBoolean("HasSentDiamondMessage", false);
        playerData.putBoolean("HasSentJifenMessage", false);
        playerData.putBoolean("HasSentChanceMessage", false);
        playerData.putBoolean("HasSentMaxJifenMessage", false);
        playerData.putString("PddLastTargetUUID", "");
    }

    private static void modifyVillagerTrades(Villager villager, Player player) {
        MerchantOffers originalOffers = villager.getOffers();
        MerchantOffers newOffers = new MerchantOffers();

        for (MerchantOffer offer : originalOffers) {
            // 创建新的交易，价格设为1个绿宝石
            ItemStack newCostA = new ItemStack(Items.EMERALD, 1);
            ItemStack newCostB = ItemStack.EMPTY;
            ItemStack result = offer.getResult().copy();

            // 创建新的 MerchantOffer
            MerchantOffer newOffer = new MerchantOffer(
                    newCostA,
                    newCostB,
                    result,
                    0, // uses 起始为 0
                    offer.getMaxUses(),
                    offer.getXp(),
                    offer.getPriceMultiplier()
            );

            // 设置交易为不可用
            newOffer.setToOutOfStock();

            newOffers.add(newOffer);
        }

        // 替换村民的交易列表
        villager.setOffers(newOffers);

        // 发送提示信息给玩家
        player.sendSystemMessage(Component.literal("砍价成功!").withStyle(ChatFormatting.RED));
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只处理服务器端的玩家事件
        if (event.phase == TickEvent.Phase.START && !event.player.level().isClientSide) {
            Player player = event.player;

            // 检查玩家是否有 64 个 zuanshi
            int zuanshiCount = 0;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() == ModItems.ZUANSHI.get()) {
                    zuanshiCount += itemStack.getCount();
                }
            }

            if (zuanshiCount >= 64) {
                // 玩家有 64 个 zuanshi，造成 1 点伤害
                player.hurt(player.damageSources().magic(), 1.0F);
                player.sendSystemMessage(Component.literal("你有 64 个钻石，受到 1 点伤害").withStyle(ChatFormatting.RED));
            }

            // 检查玩家是否有 64 个积分
            int jifenCount = 0;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() == ModItems.JIFEN.get()) {
                    jifenCount += itemStack.getCount();
                }
            }

            if (jifenCount >= 64) {
                // 玩家有 64 个积分，在玩家处生成雷电
                    player.level().explode(null, player.getX(), player.getY(), player.getZ(), 4.0F, Level.ExplosionInteraction.BLOCK);
                }
            }
        }

}

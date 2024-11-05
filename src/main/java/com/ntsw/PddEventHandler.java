package com.ntsw;

import com.ntsw.item.PddItem;
import com.ntsw.item.JifenItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            LivingEntity target = (LivingEntity) event.getTarget();

            if (!player.level().isClientSide) {
                if (target.getHealth() < HEALTH_THRESHOLD) {

                    // 获取玩家的持久化数据
                    CompoundTag playerData = player.getPersistentData();

                    // 初始化攻击次数和积分数量
                    int attackCount = playerData.getInt("PddAttackCount");
                    int jifenCount = playerData.getInt("PddJifenCount");

                    // 检查是否已发送提示信息
                    boolean hasSentDiamondMessage = playerData.getBoolean("HasSentDiamondMessage");
                    boolean hasSentJifenMessage = playerData.getBoolean("HasSentJifenMessage");
                    boolean hasSentChanceMessage = playerData.getBoolean("HasSentChanceMessage");
                    boolean hasSentMaxJifenMessage = playerData.getBoolean("HasSentMaxJifenMessage");

                    // 第一次发送钻石提示
                    if (!hasSentDiamondMessage) {
                        player.sendSystemMessage(Component.literal("集齐64个钻石可以造成一点伤害").withStyle(ChatFormatting.GREEN));
                        playerData.putBoolean("HasSentDiamondMessage", true);
                    }

                    attackCount++;

                    if (attackCount <= 5) {
                        // 前5次攻击，给予10个钻石
                        ItemStack zuanshi = new ItemStack(ModItems.ZUANSHI.get(), 10);
                        player.getInventory().add(zuanshi);
                    } else if (attackCount <= 10) {
                        // 接下来5次攻击，给予2个钻石
                        ItemStack zuanshi = new ItemStack(ModItems.ZUANSHI.get(), 2);
                        player.getInventory().add(zuanshi);
                    } else {
                        if (jifenCount < 63) {
                            // 给予玩家1个积分
                            ItemStack jifen = new ItemStack(ModItems.JIFEN.get());
                            player.getInventory().add(jifen);
                            jifenCount++;

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
                        } else {
                            // 已达到最大积分，发送泥土提示并给予泥土
                            player.sendSystemMessage(Component.literal("恭喜你获得泥土 x1").withStyle(ChatFormatting.YELLOW));
                            ItemStack dirt = new ItemStack(Items.DIRT);
                            player.getInventory().add(dirt);
                        }
                    }

                    // 更新玩家数据
                    playerData.putInt("PddAttackCount", attackCount);
                    playerData.putInt("PddJifenCount", jifenCount);

                    event.setCanceled(true); // 取消默认攻击，避免造成伤害

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
}

package com.ntsw.event.enchantedEvent;


import com.mojang.math.Axis;

import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import com.ntsw.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.TickEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnchantmentRenderHandler {
    private static final int SOUND_INTERVAL = 150;
    private static int tickCounter = 130;
    private static int tickCounter2 = 0;
    private static boolean reduceDurable;

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || player != minecraft.player) {
            return;
        }

        // 检查附魔等级
        ItemStack mainHand = player.getMainHandItem();
        int enchantmentLevel = mainHand.getEnchantmentLevel(ModEnchantments.SHALUGUANGHUAN.get());

        if (enchantmentLevel > 0) {
            PoseStack poseStack = event.getPoseStack();

            // 渲染多个物品，每个物品分布在圆周上
            for (int i = 0; i < enchantmentLevel; i++) {
                poseStack.pushPose();

                // 计算物品的位置和旋转
                double time = System.currentTimeMillis() / (100.0 / enchantmentLevel);
                double angle = (360.0 / enchantmentLevel) * i; // 将物品均匀分布
                double radius = 2.0 + (enchantmentLevel * 0.2);  // 旋转半径随等级增加
                double xOffset = radius * Math.cos(Math.toRadians(angle + time));
                double zOffset = radius * Math.sin(Math.toRadians(angle + time));

                // 移动到物品位置并旋转
                poseStack.translate(xOffset, 1.5, zOffset);
                poseStack.mulPose(Axis.YP.rotationDegrees((float) angle));
                poseStack.mulPose(Axis.XP.rotationDegrees(90));

                // 渲染主手的物品
                MultiBufferSource buffer = minecraft.renderBuffers().bufferSource();
                ItemRenderer renderer = minecraft.getItemRenderer();
                RenderSystem.enableDepthTest();
                RenderSystem.enableBlend();
                renderer.renderStatic(mainHand, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), 0);
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();

                poseStack.popPose();
            }

            // 播放音效，仅用于客户端，不扣除耐久
            if (tickCounter >= SOUND_INTERVAL) {
                tickCounter = 0;
                reduceDurable = true;
                player.level().playSound(
                        player,
                        player.blockPosition(),
                        ModSounds.ShaLuGuangHuan_Sound.get(),
                        SoundSource.PLAYERS,
                        1.0F + 0.1F * enchantmentLevel,
                        1.0F
                );
            }
        } else {
            tickCounter = 130;
            reduceDurable = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        ItemStack mainHand = player.getMainHandItem();
        int enchantmentLevel = mainHand.getEnchantmentLevel(ModEnchantments.SHALUGUANGHUAN.get());

        if (enchantmentLevel > 0) {
            tickCounter++;
            double radius = 5.0 * (enchantmentLevel * 0.5);  // 伤害范围
            for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(radius))) {
                if (entity instanceof LivingEntity living && entity != player) {
                    double distance = player.distanceToSqr(entity);
                    if (distance <= radius * radius) {
                        DamageSource damageSource = player.damageSources().generic();
                        living.hurt(damageSource, 2.0F * enchantmentLevel);  // 伤害按等级倍增
                    }
                }
                if (entity instanceof Projectile projectile && player.distanceToSqr(entity) <= radius * radius) {
                    if (projectile.getOwner() != player) {  // 仅删除非玩家射出的投掷物
                        entity.discard();
                    }
                }
            }

            // 每隔 SOUND_INTERVAL tick 扣除耐久（在服务端执行）
            if (!player.level().isClientSide && reduceDurable) {
                tickCounter2++;
                if(tickCounter2 >= 20) {
                    mainHand.hurtAndBreak(enchantmentLevel * 5, player, (p) -> {
                        p.broadcastBreakEvent(player.getUsedItemHand());
                    });
                    tickCounter2 = 0;
                }
            }
        }
    }
}

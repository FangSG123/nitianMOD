package com.ntsw.event.enchantedEvent;

import com.mojang.math.Axis;
import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.vertex.PoseStack;
@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerRotationHandler {
    private static final double BASE_UPWARD_FORCE = 0.1F; // 基础上升力，可以调整大小
    private static final double BASE_DOWNWARD_FORCE = -0.2; // 基础下压力

    // 处理 zhishengji 附魔的向上力效果
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET); // 获取玩家穿的鞋子


        // 检查玩家是否持有带有 zhishengji 附魔的物品
        int zhishengjiLevel = head.getEnchantmentLevel(ModEnchantments.ZHISHENGJI.get());
        int wajuejiLevel = boots.getEnchantmentLevel(ModEnchantments.WAJUEJI.get());
        int shaluguanghuanLevel = mainHandItem.getEnchantmentLevel(ModEnchantments.SHALUGUANGHUAN.get());

        if (zhishengjiLevel > 0) {
            // 根据 shaluguanghuan 的旋转速度来增强力的大小
            double rotationSpeedMultiplier = shaluguanghuanLevel > 0 ? shaluguanghuanLevel * 0.05 : 0.5; // 默认0.1，或根据 shaluguanghuan 等级增强
            double upwardForce = BASE_UPWARD_FORCE * zhishengjiLevel * rotationSpeedMultiplier;
            // 给玩家施加向上的力
            System.out.println("upwardForce" + upwardForce);
            player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + upwardForce, player.getDeltaMovement().z);
        }
        if (wajuejiLevel > 0 && shaluguanghuanLevel > 0) {
            // 根据 shaluguanghuan 的旋转速度来增加下压力
            double rotationSpeedMultiplier = shaluguanghuanLevel > 0 ? shaluguanghuanLevel * 0.05 : 0.1; // 默认0.1或根据 shaluguanghuan 等级增强
            double downwardForce = BASE_DOWNWARD_FORCE * wajuejiLevel * rotationSpeedMultiplier;

            // 给玩家施加向下的力
            player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + downwardForce, player.getDeltaMovement().z);

            // 检查并破坏玩家脚下的方块
            BlockPos posBelow = player.blockPosition().below();
            BlockState blockBelow = player.level().getBlockState(posBelow);

            // 破坏非空气方块并掉落物品
            if (!player.level().isClientSide && blockBelow.getDestroySpeed(player.level(), posBelow) >= 0) {
                player.level().destroyBlock(posBelow, true, player); // 第二个参数为 true 表示会掉落物品
            }
        }
    }


    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        ItemStack mainHandItem = player.getMainHandItem();

        // 获取"shaluguanghuan"附魔的等级
        int enchantmentLevel = mainHandItem.getEnchantmentLevel(ModEnchantments.SHALUGUANGHUAN.get());

        // 如果附魔等级大于0，则应用旋转效果
        if (enchantmentLevel > 0) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            // 计算旋转角度，旋转速度随着附魔等级增加
            long time = System.currentTimeMillis();
            float baseSpeed = 1F;  // 基础旋转速度
            float angle = (time % 3600L) / 10.0F * baseSpeed * enchantmentLevel;

            // 旋转玩家模型
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        // 检查是否需要恢复姿态栈
        ItemStack mainHandItem = event.getEntity().getMainHandItem();
        if (mainHandItem.getEnchantmentLevel(ModEnchantments.SHALUGUANGHUAN.get()) > 0) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.popPose();
        }
    }
}

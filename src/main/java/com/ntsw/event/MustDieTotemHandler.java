// MustDieTotemHandler.java

package com.ntsw.event;

import com.ntsw.ModItems;
import com.ntsw.network.ModMessages;
import com.ntsw.network.PacketFeiJiPiaoTotemEffect;
import com.ntsw.network.PacketMustDieTotemEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "nitian")
public class MustDieTotemHandler {

    private static final String MING_DING_ZHI_SI_TAG = "mingdingzhisi";
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        // 检查实体是否为玩家
        if (event.getEntity() instanceof Player player) {
            // 排除创造模式的玩家
            if (player.isCreative()) {
                return;
            }
            // 检查玩家是否拥有必死图腾
            if(playerHasMustDieTotem(player)) {
                ModMessages.sendToClient(new PacketMustDieTotemEffect(), (ServerPlayer) event.getEntity());
                player.level().getServer().execute(() -> {
                    try {
                        Thread.sleep(2000); // 阻塞线程，延迟 2 秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (player.isAlive()) {
                        player.hurt(player.damageSources().magic(),player.getMaxHealth());
                    }
                });
                // 可选：移除一个必死图腾
                removeOneMustDieTotem(player);
                }
            }
        }

    private static boolean playerHasMustDieTotem(Player player) {
        // 检查玩家的主物品栏和快捷栏是否拥有必死图腾
        return player.getInventory().items.stream().anyMatch(stack ->
                stack.getItem() == ModItems.MUST_DIE_TOTEM.get());
    }

    private static void removeOneMustDieTotem(Player player) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (stack.getItem() == ModItems.MUST_DIE_TOTEM.get()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                }
                break;
            }
        }

    }
}

package com.ntsw.event.enchantedEvent;

import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.world.item.*;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemDropEventHandler {

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        Item item = stack.getItem();
        if ((item == Items.TNT || item instanceof SwordItem || item instanceof PickaxeItem) && stack.getEnchantmentLevel(ModEnchantments.GOUJITIAOQIANG.get()) > 0) {
            // 将附魔信息传递给掉落物实体
            event.getEntity().getPersistentData().putBoolean("hasGoujitiaoqiang", true);
            // 可以存储附魔等级或其他信息，如果需要
        }
    }
}

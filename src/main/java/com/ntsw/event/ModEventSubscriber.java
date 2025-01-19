package com.ntsw.event;


import com.ntsw.Main;
import com.ntsw.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventSubscriber {

    // 订阅事件
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        // 获取死亡实体
        LivingEntity entity = event.getEntity();

        // 检查是否是鸡
        if (entity instanceof Chicken) {
            // 获取世界对象
            Level world = entity.getCommandSenderWorld();

            // 创建掉落物品：jinbi
            ItemStack itemStack = new ItemStack(ModItems.JINBI.get(),1000);

            // 生成物品实体并掉落
            entity.spawnAtLocation(itemStack);
        }
    }

}

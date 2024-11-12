// 文件路径：com.hollow.client.KeyBindings.java

package com.ntsw.item;

import com.ntsw.Main;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    public static final KeyMapping DASH_KEY = new KeyMapping("key.hollow.dash", GLFW.GLFW_KEY_R, "key.category.hollow");
    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DASH_KEY);
    }
}

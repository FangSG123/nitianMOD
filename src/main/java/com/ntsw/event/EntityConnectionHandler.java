package com.ntsw.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class EntityConnectionHandler {

    private static final WeakHashMap<Entity, Entity> connections = new WeakHashMap<>();

    // 添加连接关系
    public static void addConnection(Entity entity1, Entity entity2) {
        connections.put(entity1, entity2);
        connections.put(entity2, entity1);
    }

    // 移除连接关系
    public static void removeConnection(Entity entity) {
        Entity connectedEntity = connections.remove(entity);
        if (connectedEntity != null) {
            connections.remove(connectedEntity);
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (connections.containsKey(entity)) {
            Entity connectedEntity = connections.get(entity);

            if (connectedEntity instanceof LivingEntity livingConnectedEntity && connectedEntity.isAlive()) {
                // 对连接的实体造成相同的伤害
                livingConnectedEntity.hurt(event.getSource(), event.getAmount());
            } else {
                // 如果连接的实体不再有效，移除连接
                removeConnection(entity);
            }
        }
    }
}

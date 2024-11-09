package com.ntsw.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.WeakHashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber
public class EntityConnectionHandler {

    private static final WeakHashMap<Entity, ConnectionData> connections = new WeakHashMap<>();

    // 添加连接关系，包含到期时间
    public static void addConnection(Entity entity1, Entity entity2, long durationTicks) {
        long expiryTime = System.currentTimeMillis() + (durationTicks * 50); // 1 tick = 50 ms

        connections.put(entity1, new ConnectionData(entity2, expiryTime));
        connections.put(entity2, new ConnectionData(entity1, expiryTime));
    }

    // 移除连接关系
    public static void removeConnection(Entity entity) {
        ConnectionData data = connections.remove(entity);
        if (data != null) {
            connections.remove(data.connectedEntity);
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (connections.containsKey(entity)) {
            ConnectionData data = connections.get(entity);
            Entity connectedEntity = data.connectedEntity;

            if (connectedEntity instanceof LivingEntity livingConnectedEntity && connectedEntity.isAlive()) {
                // 对连接的实体造成相同的伤害
                livingConnectedEntity.hurt(event.getSource(), event.getAmount());
            } else {
                // 如果连接的实体不再有效，移除连接
                removeConnection(entity);
            }
        }
    }

    // 定期检查连接是否过期
    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            long currentTime = System.currentTimeMillis();

            Iterator<Map.Entry<Entity, ConnectionData>> iterator = connections.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Entity, ConnectionData> entry = iterator.next();
                Entity entity = entry.getKey();
                ConnectionData data = entry.getValue();

                if (currentTime >= data.expiryTime) {
                    iterator.remove();
                    connections.remove(data.connectedEntity);
                }
            }
        }
    }

    // 内部类，保存连接数据
    private static class ConnectionData {
        public final Entity connectedEntity;
        public final long expiryTime;

        public ConnectionData(Entity connectedEntity, long expiryTime) {
            this.connectedEntity = connectedEntity;
            this.expiryTime = expiryTime;
        }
    }
}

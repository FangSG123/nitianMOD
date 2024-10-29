package com.nailong.nailong;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class NaiLongEntity extends Monster {

    public NaiLongEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public ClientboundAddEntityPacket getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    // 定义实体的属性，如生命值、速度等
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }
    @Override
    protected void registerGoals() {
        // 定义实体的行为
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // 实体的AI逻辑
    }
}

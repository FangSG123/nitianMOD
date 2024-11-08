package com.ntsw.entity;

import com.ntsw.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ShikuaiEntity extends ThrowableItemProjectile {

    public ShikuaiEntity(EntityType<? extends ThrowableItemProjectile> type, Level world) {
        super(type, world);
        System.out.println("ShikuaiEntity created.");
    }

    public ShikuaiEntity(Level world, LivingEntity thrower) {
        super(EntityType.SNOWBALL, thrower, world);
        System.out.println("ShikuaiEntity created by thrower.");
    }

    @Override
    protected Item getDefaultItem() {
        return Items.STONE; // 使用石块作为投掷物的显示效果
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            // 如果命中的实体是生物实体
            if (result.getEntity() instanceof LivingEntity livingEntity) {
                // 施加 REPEL 效果，持续时间和强度可以根据需要调整
                livingEntity.addEffect(new MobEffectInstance(ModEffects.REPEL.get(), 100, 1));
                // 设置伤害
                livingEntity.hurt(damageSources().thrown(this, this.getOwner()), 4.0F);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}

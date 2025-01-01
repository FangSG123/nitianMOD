package com.ntsw.enchantment;

import com.ntsw.ModEnchantments;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class LightningEnchant extends Enchantment {

    private static final Random random = new Random();

    public LightningEnchant() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.MAINHAND});
        MinecraftForge.EVENT_BUS.register(this);  // Register event bus
    }

    @Override
    public boolean isTreasureOnly() {
        return false; // Allow obtaining via enchantment table
    }

    @Override
    public boolean isDiscoverable() {
        return true; // Allow the enchantment to appear in enchantment tables
    }

    @Override
    public int getMaxLevel() {
        return 1; // Only one level
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // Allow enchanting helmets and bows
        return stack.getItem() instanceof ArmorItem || stack.getItem() instanceof BowItem;
    }

    // Handle projectile spawn to mark it if the player has the enchantment
    @SubscribeEvent
    public void onProjectileSpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile) {
            if (projectile.getOwner() instanceof Player player) {
                if (hasLightningEnchantment(player)) {
                    projectile.getPersistentData().putBoolean("hasLightning", true);
                }
            }
        }
    }

    // When any projectile impacts, check for the lightning flag
    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        Level level = projectile.level();
        if (!level.isClientSide && projectile.getPersistentData().getBoolean("hasLightning")) {
            Vec3 hitPosition = projectile.position();
            ServerLevel serverLevel = (ServerLevel) level;
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            lightningBolt.setPos(hitPosition.x, hitPosition.y, hitPosition.z);
            serverLevel.addFreshEntity(lightningBolt);
        }
    }

    // Helmet enchantment lightning effect
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();

        if (!level.isClientSide && player.tickCount % 100 == 0) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LIGHTNING_ENCHANT.get(), helmet) > 0) {
                // 10% chance to trigger lightning
                if (random.nextFloat() < 0.1) {
                    ServerLevel serverLevel = (ServerLevel) level;
                    LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                    lightningBolt.setPos(player.getX(), player.getY(), player.getZ());
                    serverLevel.addFreshEntity(lightningBolt);

                    // Trigger game event for synchronization
                    player.gameEvent(GameEvent.LIGHTNING_STRIKE);
                }
            }
        }
    }

    // Helper method to check for the enchantment
    private boolean hasLightningEnchantment(Player player) {
        // Check helmet
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LIGHTNING_ENCHANT.get(), helmet) > 0) {
            return true;
        }
        // Check main hand
        ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LIGHTNING_ENCHANT.get(), mainHand) > 0) {
            return true;
        }
        return false;
    }
}

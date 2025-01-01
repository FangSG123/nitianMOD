package com.ntsw.event.enchantedEvent;


import com.ntsw.Main;
import com.ntsw.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class DaoTouJiuShuiQiChungQiEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }


        if (player.level().isClientSide) {
            return;
        }


        long time = player.level().getDayTime() % 24000;
        boolean isNight = time >= 12300 && time <= 23850;

        if (!isNight) {
            return;
        }

        float damage = event.getAmount();
        DamageSource source = event.getSource();


        float finalHealth = player.getHealth() - damage;
        if (finalHealth > 0) {
            return;
        }


        ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DAOTOUJIUSHUI.get(), chestArmor) <= 0) {
            return;
        }


        BlockPos pos = player.blockPosition();
        Direction facing = player.getDirection().getOpposite();
        BlockPos footPos = pos;
        BlockPos headPos = pos.relative(facing);

        BlockState footBed = Blocks.RED_BED.defaultBlockState()
                .setValue(BedBlock.FACING, facing)
                .setValue(BedBlock.PART, BedPart.FOOT);

        BlockState headBed = Blocks.RED_BED.defaultBlockState()
                .setValue(BedBlock.FACING, facing)
                .setValue(BedBlock.PART, BedPart.HEAD);


        boolean canReplaceFoot = player.level().getBlockState(footPos).canBeReplaced();
        boolean canReplaceHead = player.level().getBlockState(headPos).canBeReplaced();

        if (canReplaceFoot && canReplaceHead) {

            player.level().setBlock(footPos, footBed, 3);

            player.level().setBlock(headPos, headBed, 3);


            player.level().addParticle(ParticleTypes.HAPPY_VILLAGER, footPos.getX() + 0.5, footPos.getY() + 1.0, footPos.getZ() + 0.5, 0, 0, 0);
            player.level().addParticle(ParticleTypes.HAPPY_VILLAGER, headPos.getX() + 0.5, headPos.getY() + 1.0, headPos.getZ() + 0.5, 0, 0, 0);


            event.setCanceled(true);


            player.setHealth(Math.max(1.0F, player.getHealth() + 1.0F));

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(footPos.getX() + 0.5, footPos.getY(), footPos.getZ() + 0.5);
                serverPlayer.startSleepInBed(footPos);
            }

            removeEnchantmentFromChest(chestArmor, "nitianfumo:daotoujiushui");

            Level world = player.level();
            if (isInNether(world) || isInTheEnd(world)) {

                double explosionX = footPos.getX() + 0.5;
                double explosionY = footPos.getY();
                double explosionZ = footPos.getZ() + 0.5;
                float explosionPower = 4.0F; // Adjust the power as needed
                boolean causesFire = false;
                Explosion.BlockInteraction blockInteraction = Explosion.BlockInteraction.DESTROY;


                world.explode(player, explosionX, explosionY, explosionZ, explosionPower, causesFire, Level.ExplosionInteraction.BLOCK);


                world.addParticle(ParticleTypes.EXPLOSION, explosionX, explosionY, explosionZ, 20, 0, 0);
            }

        } else {

        }
    }
    private static boolean isInNether(Level world) {
        return world.dimension() == Level.NETHER;
    }

    /**
     * Checks if the given world is The End.
     *
     * @param world The world to check.
     * @return True if the world is The End, false otherwise.
     */
    private static boolean isInTheEnd(Level world) {
        return world.dimension() == Level.END;
    }

    /**
     * 移除指定附魔ID的附魔
     *
     * @param chestArmor    需要移除附魔的胸甲
     * @param enchantmentId 需要移除的附魔ID，例如 "nitianfumo:daotoujiushui"
     */
    private static void removeEnchantmentFromChest(ItemStack chestArmor, String enchantmentId) {
        if (chestArmor.hasTag() && chestArmor.getTag().contains("Enchantments", 9)) { // 9代表List类型
            ListTag enchantments = chestArmor.getTag().getList("Enchantments", 10); // 10代表Compound类型
            ListTag newEnchantments = new ListTag();
            for (int i = 0; i < enchantments.size(); i++) {
                CompoundTag enchantmentTag = enchantments.getCompound(i);
                String currentEnchantmentId = enchantmentTag.getString("id");
                if (!currentEnchantmentId.equals(enchantmentId)) {
                    newEnchantments.add(enchantmentTag);
                }
            }
            chestArmor.getTag().put("Enchantments", newEnchantments);
        }
    }

    @SubscribeEvent
    public static void onPlayerDamagedWhileSleeping(LivingAttackEvent event) {
        // 检查是否是玩家
        if (event.getEntity() instanceof Player player) {
            // 检查玩家是否在床上
            if (player.isSleeping()) {
                // 检查头盔是否有附魔
                if (player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(ModEnchantments.QICHUANGQI.get()) > 0) {
                    // 添加效果：抗性提升5
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 4)); // 200 ticks = 10秒
                    // 添加效果：力量5
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 4));
                    // 添加效果：速度5
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 4));
                }
            }
        }
    }

}

package com.ntsw.item;

import com.ntsw.entity.ShikuaiEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZhanshiSaobaItem extends SaobaItem {

    public ZhanshiSaobaItem(Properties properties) {
        super(properties);
    }
    private static final String POTION_EFFECTS_KEY = "PotionEffects";
    // 重写使用逻辑，右键投掷大量投掷物
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // 设置投掷物数量和范围
            int projectileCount = 5; // 可以修改数量
            double spread = 0.5D; // 控制发射的扩散范围

            for (int i = 0; i < projectileCount; i++) {
                // 创建新的 ShikuaiEntity 投掷物
                ShikuaiEntity projectile = new ShikuaiEntity(level, player);
                projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());

                // 设置发射方向和扩散
                Vec3 lookDirection = player.getLookAngle().add(
                        (level.random.nextDouble() - 0.5) * spread,
                        (level.random.nextDouble() - 0.5) * spread,
                        (level.random.nextDouble() - 0.5) * spread
                );
                projectile.setDeltaMovement(lookDirection.scale(1.5)); // 控制投掷速度

                level.addFreshEntity(projectile);
            }

            itemstack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    // 重写 ToolTip
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.literal("右键喷射"));
    }
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean hasPotionEffects = false;
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
        // 调试日志：检查物品的 NBT 标签
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            System.out.println("SaobaItem 没有 NBT 标签.");
        } else {
            if (tag.contains(POTION_EFFECTS_KEY, 9)) { // 9 表示 ListTag
                ListTag effectsList = tag.getList(POTION_EFFECTS_KEY, 8); // 8 表示字符串类型
                System.out.println("SaobaItem 有 " + effectsList.size() + " 个药水效果.");
                for (int i = 0; i < effectsList.size(); i++) {
                    String effectKey = effectsList.getString(i);
                    System.out.println("Applying potion effect: " + effectKey);
                    ResourceLocation resourceLocation = ResourceLocation.tryParse(effectKey);
                    if (resourceLocation != null) {
                        MobEffect effect = BuiltInRegistries.MOB_EFFECT.getOptional(resourceLocation).orElse(null);
                        if (effect != null) {
                            // 应用药水效果
                            target.addEffect(new MobEffectInstance(effect, 200, 0)); // 200 ticks = 10秒
                            hasPotionEffects = true;
                            System.out.println("Applied potion effect: " + effectKey);
                        }
                    }
                }
            } else {
                System.out.println("SaobaItem 有 NBT 标签，但没有 PotionEffects.");
            }
        }

        // 仅当存在药水效果时，应用4点伤害
        if (hasPotionEffects) {
            if (attacker instanceof Player player) {
                target.hurt(attacker.damageSources().playerAttack(player), 4.0F);
                System.out.println("Applied 4.0F damage to target.");
            } else {
                target.hurt(attacker.damageSources().mobAttack(attacker), 4.0F);
                System.out.println("Applied 4.0F damage to target (mob attack).");
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }


}

package com.ntsw.entity;
import com.ntsw.goal.MoveToFarmlandGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class LaoHeiEntity extends Villager {
    private static final double INITIAL_SPEED = 0.1D;
    private static final double SPEED_INCREMENT = 0.1D;
    private static final int RESET_TICKS = 2000;
    private static final int FEEDING_INTERVAL_TICKS = 1000; // 5 minutes in ticks
    private int tickCounter = 0;
    private int lastFedTime = 0;

    public LaoHeiEntity(EntityType<? extends Villager> type, Level world) {
        super(type, world);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(INITIAL_SPEED);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new MoveToFarmlandGoal(this,SPEED_INCREMENT));

    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity directEntity = source.getDirectEntity();

        if (directEntity instanceof NongChangZhuEntity ||
                (directEntity instanceof Player player && player.getMainHandItem().is(Items.LEAD))) {
            System.out.println("加速！");
            double newSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() + SPEED_INCREMENT;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newSpeed);
        }

        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        tickCounter++;

        // 每 2000 个 tick 重置速度
        if (tickCounter >= RESET_TICKS) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(INITIAL_SPEED);
            tickCounter = 0;
        }

        // 检查是否超过 5 分钟未喂食
        if (this.tickCount - lastFedTime > FEEDING_INTERVAL_TICKS) {
            this.hurt(damageSources().starve(), 1.0F); // 持续掉血
        }
    }
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 检查玩家是否手持可喂食的食物
        if (feed(player, itemStack)) {
            return InteractionResult.SUCCESS; // 成功喂食后返回 SUCCESS
        }

        return super.interactAt(player, vec, hand); // 如果未喂食，执行默认交互逻辑
    }


    public boolean feed(Player player, ItemStack itemStack) {
        if (itemStack.is(Items.BREAD) || itemStack.is(Items.APPLE)) { // 指定可以喂食的食物
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1); // 消耗一个食物
            }
            lastFedTime = this.tickCount; // 更新喂食时间
            System.out.println("喂食爱心特效触发"); // 添加日志确认触发

            if (this.level().isClientSide()) {
                // 生成爱心粒子效果在实体头上
                for (int i = 0; i < 5; i++) { // 多生成几次粒子效果来增强效果
                    level().addParticle(ParticleTypes.HEART,
                            this.getX(),
                            this.getY() + 2.0D,  // 提高高度以便显示在头部上方
                            this.getZ(),
                            0.0D,
                            0.2D,  // 添加轻微的 Y 轴速度
                            0.0D);
                }
            } else {
                // 确保在服务器端广播事件
                level().broadcastEntityEvent(this, (byte) 18);
            }

            return true;
        }
        return false;
    }


}

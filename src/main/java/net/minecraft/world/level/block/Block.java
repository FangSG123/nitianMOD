package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;

public class Block {
    public static BlockState pushEntitiesUp(BlockState p_49898_, BlockState p_49899_, LevelAccessor p_238252_, BlockPos p_49901_) {
        // 获取碰撞形状
        VoxelShape voxelshape = Shapes.joinUnoptimized(
            p_49898_.getCollisionShape(p_238252_, p_49901_),
            p_49899_.getCollisionShape(p_238252_, p_49901_), 
            BooleanOp.ONLY_SECOND
        ).move((double)p_49901_.getX(), (double)p_49901_.getY(), (double)p_49901_.getZ());
        
        if (voxelshape.isEmpty()) {
            return p_49899_;
        } else {
            // 处理实体碰撞
            for(Entity entity : p_238252_.getEntities((Entity)null, voxelshape.bounds())) {
                double d0 = Shapes.collide(Direction.Axis.Y, entity.getBoundingBox().move(0.0D, 1.0D, 0.0D), List.of(voxelshape), -1.0D);
                entity.teleportRelative(0.0D, 1.0D + d0, 0.0D);
            }
            return p_49899_;
        }
    }

    // 碰撞形状缓存
    private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder()
        .maximumSize(512L)
        .weakKeys()
        .build(new CacheLoader<VoxelShape, Boolean>() {
            public Boolean load(VoxelShape p_49972_) {
                return !Shapes.joinIsNotEmpty(Shapes.block(), p_49972_, BooleanOp.NOT_SAME);
            }
        });

    public static VoxelShape box(double p_49797_, double p_49798_, double p_49799_, double p_49800_, double p_49801_, double p_49802_) {
        return Shapes.box(p_49797_ / 16.0D, p_49798_ / 16.0D, p_49799_ / 16.0D, p_49800_ / 16.0D, p_49801_ / 16.0D, p_49802_ / 16.0D);
    }
} 
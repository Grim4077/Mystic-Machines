package net.grim.mysticmachine.block.custom;

import com.mojang.serialization.MapCodec;
import net.grim.mysticmachine.block.entity.LiquidPipeBlockEntity;
import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LiquidPipeBlock extends BaseEntityBlock {

    public static final MapCodec<LiquidPipeBlock> CODEC =
            simpleCodec(LiquidPipeBlock::new);

    public LiquidPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LiquidPipeBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide) return null;

        if (type != ModBlockEntities.LIQUID_PIPE_BE.get()) return null;

        return (lvl, pos, st, be) -> {
            LiquidPipeBlockEntity.tick(lvl, pos, st, (LiquidPipeBlockEntity) be);
        };
    }
}
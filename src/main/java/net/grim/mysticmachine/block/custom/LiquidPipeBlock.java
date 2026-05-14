package net.grim.mysticmachine.block.custom;

import com.mojang.serialization.MapCodec;
import net.grim.mysticmachine.block.entity.LiquidPipeBlockEntity;
import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class LiquidPipeBlock extends BaseEntityBlock {

    public static final MapCodec<LiquidPipeBlock> CODEC = simpleCodec(LiquidPipeBlock::new);

    // Connection properties
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST  = BooleanProperty.create("east");
    public static final BooleanProperty WEST  = BooleanProperty.create("west");
    public static final BooleanProperty UP    = BooleanProperty.create("up");
    public static final BooleanProperty DOWN  = BooleanProperty.create("down");

    // Shapes
    private static final VoxelShape CENTER = Block.box(5, 5, 5, 11, 11, 11);

    private static final VoxelShape[] DIRECTION_SHAPES = new VoxelShape[6];

    static {
        DIRECTION_SHAPES[Direction.NORTH.get3DDataValue()] = Block.box(5, 5, 0,     11, 11, 5.01);
        DIRECTION_SHAPES[Direction.SOUTH.get3DDataValue()] = Block.box(5, 5, 10.99, 11, 11, 16);
        DIRECTION_SHAPES[Direction.EAST.get3DDataValue()]  = Block.box(10.99, 5, 5, 16, 11, 11);
        DIRECTION_SHAPES[Direction.WEST.get3DDataValue()]  = Block.box(0, 5, 5,     5.01, 11, 11);
        DIRECTION_SHAPES[Direction.UP.get3DDataValue()]    = Block.box(5, 10.99, 5, 11, 16, 11);
        DIRECTION_SHAPES[Direction.DOWN.get3DDataValue()]  = Block.box(5, 0, 5,     11, 5.01, 11);
    }

    public LiquidPipeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    // ==================== Placement & Updates ====================

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return updateAllConnections(level, pos, defaultBlockState());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return updateAllConnections((Level) level, pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        BlockState newState = updateAllConnections(level, pos, state);
        if (newState != state) {
            level.setBlock(pos, newState, 3);
        }
    }

    private BlockState updateAllConnections(Level level, BlockPos pos, BlockState state) {
        BlockState newState = state;
        for (Direction dir : Direction.values()) {
            boolean connects = canConnectTo(level, pos.relative(dir), dir);
            newState = newState.setValue(getConnectionProperty(dir), connects);
        }
        return newState;
    }

    private boolean canConnectTo(Level level, BlockPos neighborPos, Direction direction) {
        if (level.getBlockState(neighborPos).getBlock() instanceof LiquidPipeBlock) {
            return true;
        }

        var handler = level.getCapability(
                Capabilities.FluidHandler.BLOCK,
                neighborPos,
                direction.getOpposite()
        );
        return handler != null;
    }

    public static BooleanProperty getConnectionProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            case UP    -> UP;
            case DOWN  -> DOWN;
        };
    }

    // ==================== Collision Shape ====================

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CENTER;

        for (Direction dir : Direction.values()) {
            if (state.getValue(getConnectionProperty(dir))) {
                shape = Shapes.or(shape, DIRECTION_SHAPES[dir.get3DDataValue()]);
            }
        }
        return shape;
    }

    // ==================== Block Entity ====================

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
            Level level, BlockState state, BlockEntityType<T> type) {

        if (level.isClientSide) return null;
        if (type != ModBlockEntities.LIQUID_PIPE_BE.get()) return null;

        return (lvl, pos, st, be) ->
                LiquidPipeBlockEntity.tick(lvl, pos, st, (LiquidPipeBlockEntity) be);
    }
}
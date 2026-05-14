    package net.grim.mysticmachine.block.entity;

    import net.grim.mysticmachine.block.custom.LiquidPipeBlock;
    import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
    import net.minecraft.core.BlockPos;
    import net.minecraft.core.Direction;
    import net.minecraft.world.level.Level;
    import net.minecraft.world.level.block.HorizontalDirectionalBlock;
    import net.minecraft.world.level.block.entity.BlockEntity;
    import net.minecraft.world.level.block.state.BlockState;
    import net.neoforged.neoforge.capabilities.Capabilities;
    import net.neoforged.neoforge.fluids.FluidStack;
    import net.neoforged.neoforge.fluids.capability.IFluidHandler;
    import net.neoforged.neoforge.fluids.capability.templates.FluidTank;


    public class LiquidPipeBlockEntity extends BlockEntity {

        private static final int TRANSFER_RATE = 50;

        public final FluidTank tank = new FluidTank(2000);

        public LiquidPipeBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.LIQUID_PIPE_BE.get(), pos, state);
        }

        public static void tick(Level level, BlockPos pos, BlockState state, LiquidPipeBlockEntity pipe) {
            if (level.isClientSide) return;


            // PULL
            for (Direction dir : Direction.values()) {

                if (level.getBlockState(pos.relative(dir)).getBlock() instanceof LiquidPipeBlock) {
                    continue;
                }


                IFluidHandler from = level.getCapability(
                        Capabilities.FluidHandler.BLOCK,
                        pos.relative(dir),
                        null
                );

                if (from == null) continue;

                FluidStack sim = from.drain(TRANSFER_RATE, IFluidHandler.FluidAction.SIMULATE);

                if (sim.isEmpty()) continue;

                int filled = pipe.tank.fill(sim, IFluidHandler.FluidAction.EXECUTE);

                if (filled > 0) {
                    from.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                }
            }


            // PUSH
            FluidStack snapshot = pipe.tank.drain(TRANSFER_RATE, IFluidHandler.FluidAction.SIMULATE);

            if (snapshot.isEmpty()) return;

            for (Direction dir : Direction.values()) {
                if (level.getBlockState(pos.relative(dir)).getBlock() instanceof LiquidPipeBlock) {
                    continue;
                }

                IFluidHandler to = level.getCapability(
                        Capabilities.FluidHandler.BLOCK,
                        pos.relative(dir),
                        null
                );

                if (to == null) continue;

                int accepted = to.fill(snapshot, IFluidHandler.FluidAction.EXECUTE);

                if (accepted > 0) {
                    pipe.tank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                    snapshot.shrink(accepted);
                }

                if (snapshot.isEmpty()) return;
            }
        }
    }
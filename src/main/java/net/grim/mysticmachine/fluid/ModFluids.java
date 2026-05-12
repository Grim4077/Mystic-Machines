package net.grim.mysticmachine.fluid;

import net.grim.mysticmachine.MysticMachine;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids {

    // Registry for FluidTypes (NeoForge specific)
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MysticMachine.MOD_ID);

    // Registry for actual Fluids (vanilla registry)
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, MysticMachine.MOD_ID);

    // The FluidType defines what steam IS - its physical properties
    public static final DeferredHolder<FluidType, FluidType> STEAM_FLUID_TYPE =
            FLUID_TYPES.register("steam", () -> new FluidType(
                    FluidType.Properties.create()
                            .density(-1000)      // Negative = rises like a gas
                            .viscosity(200)      // How fast it flows through pipes
                            .temperature(373)));    // Tells the game its a gas

    // The actual fluid stored in tanks and pipes
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> STEAM =
            FLUIDS.register("steam", () -> new BaseFlowingFluid.Source(steamProperties()));

    // Links the FluidType and Source fluid together
    private static BaseFlowingFluid.Properties steamProperties() {
        return new BaseFlowingFluid.Properties(
                STEAM_FLUID_TYPE,   // What type of fluid is it?
                STEAM,              // What is the source fluid?
                STEAM)              // No flowing variant, use source for both
                .bucket(null)       // No bucket item
                .block(null);       // No world block
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
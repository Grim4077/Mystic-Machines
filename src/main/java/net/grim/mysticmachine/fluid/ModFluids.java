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

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MysticMachine.MOD_ID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, MysticMachine.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> STEAM_FLUID_TYPE =
            FLUID_TYPES.register("steam", () -> new FluidType(
                    FluidType.Properties.create()
                            .density(-1000)
                            .viscosity(200)
                            .temperature(373)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> STEAM =
            FLUIDS.register("steam", () -> new BaseFlowingFluid.Source(steamProperties()));

    private static BaseFlowingFluid.Properties steamProperties() {
        return new BaseFlowingFluid.Properties(
                STEAM_FLUID_TYPE,
                STEAM,
                STEAM)
                .bucket(null)
                .block(null);
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
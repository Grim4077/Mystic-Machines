package net.grim.mysticmachine.block.entity;

import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MysticMachine.MOD_ID);

    // BOILER
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoilerBlockEntity>> BOILER_BE =
            BLOCK_ENTITIES.register("boiler_be",
                    () -> BlockEntityType.Builder.of(
                            BoilerBlockEntity::new,
                            ModBlocks.BOILER.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TurbineBlockEntity>> TURBINE_BE =
            BLOCK_ENTITIES.register("turbine_be",
                    () -> BlockEntityType.Builder.of(
                            TurbineBlockEntity::new,
                            ModBlocks.MACHINE_TURBINE.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
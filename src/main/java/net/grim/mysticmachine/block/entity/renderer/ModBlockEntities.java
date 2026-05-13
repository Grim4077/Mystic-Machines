package net.grim.mysticmachine.block.entity.renderer;

import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.block.ModBlocks;
import net.grim.mysticmachine.block.entity.TurbineBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MysticMachine.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TurbineBlockEntity>> TURBINE_BE =
            BLOCK_ENTITIES.register("turbine_be",
                    () -> BlockEntityType.Builder.of(
                            TurbineBlockEntity::new, ModBlocks.MACHINE_TURBINE.get()).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
package net.grim.mysticmachine.block.entity;

import net.grim.mysticmachine.MysticMachine;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = MysticMachine.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        // Expose the steamTank as a fluid handler on all 6 sides
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,  // What capability are we exposing?
                ModBlockEntities.BOILER_BE.get(), // Which block entity type?
                (blockEntity, side) -> blockEntity.steamTank  // Return the tank for any side
        );
    }
}
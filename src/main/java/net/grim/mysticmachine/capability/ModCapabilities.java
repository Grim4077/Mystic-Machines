package net.grim.mysticmachine.capability;

import net.grim.mysticmachine.block.entity.BoilerBlockEntity;
import net.grim.mysticmachine.block.entity.TurbineBlockEntity;
import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {


        // TURBINE (OUTPUT ENERGY + INPUT STEAM)
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.TURBINE_BE.get(),
                (be, side) -> be.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.TURBINE_BE.get(),
                (be, side) -> be.steamTank
        );

        // BOILER (OUTPUT STEAM)
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.BOILER_BE.get(),
                (be, side) -> be.steamTank
        );

        // LIQUID PIPE (INPUT + OUTPUT STEAM)
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.LIQUID_PIPE_BE.get(),
                (be, side) -> be.tank
        );
    }
}
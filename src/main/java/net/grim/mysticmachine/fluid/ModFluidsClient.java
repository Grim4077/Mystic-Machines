package net.grim.mysticmachine.fluid;

import net.grim.mysticmachine.MysticMachine;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = MysticMachine.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModFluidsClient {

    @SubscribeEvent
    public static void registerFluidExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL =
                    ResourceLocation.fromNamespaceAndPath("mysticmachine", "fluid/steam_still");
            private static final ResourceLocation FLOWING =
                    ResourceLocation.fromNamespaceAndPath("mysticmachine", "fluid/steam_flowing");

            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public int getTintColor() {
                return 0xAADDDDDD;
            }
        }, ModFluids.STEAM_FLUID_TYPE.get());
    }
}
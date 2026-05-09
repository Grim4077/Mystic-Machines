package net.grim.mysticmachine.items;

import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MysticMachine.MOD_ID);

    public static final Supplier<CreativeModeTab> MOD_ITEMS_TAB = CREATIVE_MODE_TAB.register("mod_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SAPPHIRE.get()))
                    .title(Component.translatable("creativetab.mysticmachine.mod_items"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SAPPHIRE);
                        output.accept(ModItems.RUBY);
                    }).build());

    public static final Supplier<CreativeModeTab> MOD_BLOCKS_TAB = CREATIVE_MODE_TAB.register("mod_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SAPPHIRE_BLOCK.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MysticMachine.MOD_ID, "mod_items_tab"))
                    .title(Component.translatable("creativetab.mysticmachine.mod_blocks"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.RUBY_ORE);
                        output.accept(ModBlocks.DEEPSLATE_RUBY_ORE);
                        output.accept(ModBlocks.RUBY_BLOCK);
                        output.accept(ModBlocks.SAPPHIRE_ORE);
                        output.accept(ModBlocks.DEEPSLATE_SAPPHIRE_ORE);
                        output.accept(ModBlocks.SAPPHIRE_BLOCK);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }


}

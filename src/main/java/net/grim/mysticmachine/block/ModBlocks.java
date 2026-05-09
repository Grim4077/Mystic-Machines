package net.grim.mysticmachine.block;

import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.items.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MysticMachine.MOD_ID);

    public static final DeferredBlock<Block> SAPPHIRE_ORE = registerBlock("sapphire_ore",
            () ->new DropExperienceBlock(UniformInt.of(3, 7),
                    BlockBehaviour.Properties.of()
                            .strength(3.0F,3.0F).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM)));
    public static final DeferredBlock<Block> DEEPSLATE_SAPPHIRE_ORE = registerBlock("deepslate_sapphire_ore",
            () ->new DropExperienceBlock(UniformInt.of(3, 7),
                    BlockBehaviour.Properties.of()
                            .strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));
    public static final DeferredBlock<Block> SAPPHIRE_BLOCK = registerBlock("sapphire_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)));

    public static final DeferredBlock<Block> DEEPSLATE_RUBY_ORE = registerBlock("deepslate_ruby_ore",
            () ->new DropExperienceBlock(UniformInt.of(3, 7),
                    BlockBehaviour.Properties.of()
                            .strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));
    public static final DeferredBlock<Block> RUBY_ORE = registerBlock("ruby_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 7),
                    BlockBehaviour.Properties.of()
                            .strength(3.0F, 3.0F).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM)));
    public static final DeferredBlock<Block> RUBY_BLOCK = registerBlock("ruby_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.COPPER_BULB)));




    public static final DeferredBlock<Block> MACHINE_TURBINE = registerBlock("machine_turbine",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    // Machines
    public static final DeferredBlock<Block> BOILER = registerBlock("machine_boiler",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f,6.0f).sound(SoundType.METAL)));




    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name,  () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventbus) {
        BLOCKS.register(eventbus);
    }
}

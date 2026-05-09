package net.grim.mysticmachine.datagen;

import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MysticMachine.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.RUBY_BLOCK.get())
            .add(ModBlocks.DEEPSLATE_RUBY_ORE.get())
            .add(ModBlocks.RUBY_ORE.get())

            .add(ModBlocks.SAPPHIRE_BLOCK.get())
            .add(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get())
            .add(ModBlocks.SAPPHIRE_ORE.get())

            .add(ModBlocks.BOILER.get());

    tag(BlockTags.NEEDS_IRON_TOOL)
            .add(ModBlocks.RUBY_BLOCK.get())
            .add(ModBlocks.DEEPSLATE_RUBY_ORE.get())
            .add(ModBlocks.RUBY_ORE.get())

            .add(ModBlocks.SAPPHIRE_BLOCK.get())
            .add(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get())
            .add(ModBlocks.SAPPHIRE_ORE.get());
    }
}

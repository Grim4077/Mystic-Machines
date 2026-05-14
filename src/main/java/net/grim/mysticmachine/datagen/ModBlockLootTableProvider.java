package net.grim.mysticmachine.datagen;

import net.grim.mysticmachine.block.ModBlocks;
import net.grim.mysticmachine.items.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of() , FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.RUBY_BLOCK.get());
        dropSelf(ModBlocks.SAPPHIRE_BLOCK.get());





        //Machines
        dropSelf((ModBlocks.BOILER.get()));

        dropSelf(ModBlocks.MACHINE_TURBINE.get());

        //Pipes
        dropSelf(ModBlocks.LIQUID_PIPE.get());





        add(ModBlocks.RUBY_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.RUBY_ORE.get(), ModItems.RUBY.get(),
                        1,3));
        add(ModBlocks.DEEPSLATE_RUBY_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.DEEPSLATE_RUBY_ORE.get(), ModItems.RUBY.get(),
                        1,3));

        add(ModBlocks.SAPPHIRE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.SAPPHIRE_ORE.get(), ModItems.SAPPHIRE.get(),
                        1,3));
        add(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get(), ModItems.SAPPHIRE.get(),
                        1,3));
    }

    protected LootTable.Builder createMultipleOreDrops(Block pblock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pblock,
                this.applyExplosionDecay(pblock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops))))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}

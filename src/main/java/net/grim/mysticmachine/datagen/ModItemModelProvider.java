package net.grim.mysticmachine.datagen;

import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.items.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MysticMachine.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SAPPHIRE.get());
        
        basicItem(ModItems.RUBY.get());
    }
}

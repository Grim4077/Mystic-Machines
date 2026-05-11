package net.grim.mysticmachine.screen;

import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.screen.menu.BoilerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MysticMachine.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BoilerMenu>> BOILER_MENU =
            MENUS.register("boiler_menu", () ->
                    IMenuTypeExtension.create(BoilerMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
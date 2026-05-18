package com.yukke9265.cobblestone_xx_compressed.registry;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCrusherMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCentrifugeMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneDissolutionChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFEGeneratorMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFluidMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneLaserDrillMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMelterMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneReactionChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestonePoweredFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneTankMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModMenuType {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
        DeferredRegister.create(Registries.MENU, CobblestonexXCompressed.MODID);

    public static final Supplier<MenuType<CobblestoneFurnaceMenu>> COBBLESTONE_FURNACE_MENU =
        MENU_TYPES.register(
            "cobblestone_furnace_menu",
            () -> IMenuTypeExtension.create(CobblestoneFurnaceMenu::new)
        );

    public static final Supplier<MenuType<CobblestonePoweredFurnaceMenu>> COBBLESTONE_POWERED_FURNACE_MENU =
        MENU_TYPES.register(
            "cobblestone_powered_furnace_menu",
            () -> IMenuTypeExtension.create(CobblestonePoweredFurnaceMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneCrusherMenu>> COBBLESTONE_CRUSHER_MENU =
        MENU_TYPES.register(
            "cobblestone_crusher_menu",
            () -> IMenuTypeExtension.create(CobblestoneCrusherMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneFEGeneratorMenu>> COBBLESTONE_FE_GENERATOR_MENU =
        MENU_TYPES.register(
            "cobblestone_fe_generator_menu",
            () -> IMenuTypeExtension.create(CobblestoneFEGeneratorMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneCentrifugeMenu>> COBBLESTONE_CENTRIFUGE_MENU =
        MENU_TYPES.register(
            "cobblestone_centrifuge_menu",
            () -> IMenuTypeExtension.create(CobblestoneCentrifugeMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneLaserDrillMenu>> COBBLESTONE_LASER_DRILL_MENU =
        MENU_TYPES.register(
            "cobblestone_laser_drill_menu",
            () -> IMenuTypeExtension.create(CobblestoneLaserDrillMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneMixerMenu>> COBBLESTONE_MIXER_MENU =
        MENU_TYPES.register(
            "cobblestone_mixer_menu",
            () -> IMenuTypeExtension.create(CobblestoneMixerMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneMelterMenu>> COBBLESTONE_MELTER_MENU =
        MENU_TYPES.register(
            "cobblestone_melter_menu",
            () -> IMenuTypeExtension.create(CobblestoneMelterMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneReactionChamberMenu>> COBBLESTONE_REACTION_CHAMBER_MENU =
        MENU_TYPES.register(
            "cobblestone_reaction_chamber_menu",
            () -> IMenuTypeExtension.create(CobblestoneReactionChamberMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneDissolutionChamberMenu>> COBBLESTONE_DISSOLUTION_CHAMBER_MENU =
        MENU_TYPES.register(
            "cobblestone_dissolution_chamber_menu",
            () -> IMenuTypeExtension.create(CobblestoneDissolutionChamberMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneFluidMixerMenu>> COBBLESTONE_FLUID_MIXER_MENU =
        MENU_TYPES.register(
            "cobblestone_fluid_mixer_menu",
            () -> IMenuTypeExtension.create(CobblestoneFluidMixerMenu::new)
        );

    public static final Supplier<MenuType<CobblestoneTankMenu>> COBBLESTONE_TANK_MENU =
        MENU_TYPES.register(
            "cobblestone_tank_menu",
            () -> IMenuTypeExtension.create(CobblestoneTankMenu::new)
        );
}

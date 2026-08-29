package com.yukke9265.cobblestone_xx_compressed.jei;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CompressedStoneLootRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneAssemblyMachineRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneChemicalReactorRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneDissolutionChamberRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneEnchanterRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneExtremeCompressorRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneFluidMixerRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneCrusherRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneCentrifugeRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneFurnaceRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneLaserDrillRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneMelterRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneMixerRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestonePoweredFurnaceRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneReactionChamberRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneCrystallizationChamberRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.StoneBreakSimulatorRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.category.WaterGeneratorConversionRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.jei.WaterGeneratorConversionJeiRecipe;
import com.yukke9265.cobblestone_xx_compressed.menu.BaseMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneAssemblyMachineMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneChemicalReactorMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCrusherMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCentrifugeMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneDissolutionChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneEnchanterMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneExtremeCompressorMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFluidMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneLaserDrillMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMelterMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestonePoweredFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneReactionChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCrystallizationChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.StoneBreakSimulatorMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipeHelper;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneAssemblyMachineRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneChemicalReactorRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneDissolutionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneEnchanterRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneExtremeCompressorRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFluidMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneLaserDrillRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMelterRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipeHelper;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneReactionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrystallizationChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneAssemblyMachineScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneCentrifugeScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneChemicalReactorScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneCrystallizationChamberScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneCrusherScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneDissolutionChamberScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneEnchanterScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneExtremeCompressorScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneFluidMixerScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneFurnaceScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneLaserDrillScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneMelterScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneMixerScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestonePoweredCrafterScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestonePoweredFurnaceScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneReactionChamberScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.BaseScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.StoneBreakSimulatorScreen;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import mezz.jei.api.runtime.IJeiRuntime;
import java.util.function.Supplier;

@SuppressWarnings({"null", "unchecked", "rawtypes"})
@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    // JEIu のお気に入り用に RecipeHolder ごと登録する（id を落とさない）。
    public static final RecipeType<RecipeHolder<CobblestoneFurnaceRecipe>> COBBLESTONE_FURNACE_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_FURNACE);

    public static final RecipeType<RecipeHolder<CobblestoneCrusherRecipe>> COBBLESTONE_CRUSHER_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_CRUSHER);

    public static final RecipeType<RecipeHolder<CobblestonePoweredFurnaceRecipe>> COBBLESTONE_POWERED_FURNACE_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_POWERED_FURNACE);

    public static final RecipeType<RecipeHolder<CobblestoneExtremeCompressorRecipe>> COBBLESTONE_EXTREME_COMPRESSOR_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_EXTREME_COMPRESSOR);

    public static final RecipeType<RecipeHolder<CobblestoneCentrifugeRecipe>> COBBLESTONE_CENTRIFUGE_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_CENTRIFUGE);

    public static final RecipeType<RecipeHolder<CobblestoneLaserDrillRecipe>> COBBLESTONE_LASER_DRILL_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_LASER_DRILL);

    public static final RecipeType<RecipeHolder<CobblestoneMixerRecipe>> COBBLESTONE_MIXER_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_MIXER);

    public static final RecipeType<StoneBreakSimulatorJeiRecipe> STONE_BREAK_SIMULATOR_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.STONE_BREAK_SIMULATOR.getNamespace(),
            ModJeiIds.STONE_BREAK_SIMULATOR.getPath(),
            StoneBreakSimulatorJeiRecipe.class
        );

    public static final RecipeType<RecipeHolder<CobblestoneMelterRecipe>> COBBLESTONE_MELTER_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_MELTER);

    public static final RecipeType<RecipeHolder<CobblestoneAssemblyMachineRecipe>> COBBLESTONE_ASSEMBLY_MACHINE_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_ASSEMBLY_MACHINE);

    public static final RecipeType<RecipeHolder<CobblestoneEnchanterRecipe>> COBBLESTONE_ENCHANTER_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_ENCHANTER);

    public static final RecipeType<RecipeHolder<CobblestoneChemicalReactorRecipe>> COBBLESTONE_CHEMICAL_REACTOR_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_CHEMICAL_REACTOR);

    public static final RecipeType<RecipeHolder<CobblestoneReactionChamberRecipe>> COBBLESTONE_REACTION_CHAMBER_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_REACTION_CHAMBER);

    public static final RecipeType<RecipeHolder<CobblestoneCrystallizationChamberRecipe>> COBBLESTONE_CRYSTALLIZATION_CHAMBER_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_CRYSTALLIZATION_CHAMBER);

    public static final RecipeType<RecipeHolder<CobblestoneDissolutionChamberRecipe>> COBBLESTONE_DISSOLUTION_CHAMBER_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_DISSOLUTION_CHAMBER);

    public static final RecipeType<RecipeHolder<CobblestoneFluidMixerRecipe>> COBBLESTONE_FLUID_MIXER_RECIPE_TYPE =
        holderRecipeType(ModJeiIds.COBBLESTONE_FLUID_MIXER);

    public static final RecipeType<CompressedStoneLootJeiRecipe> COMPRESSED_STONE_LOOT_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COMPRESSED_STONE_LOOT.getNamespace(),
            ModJeiIds.COMPRESSED_STONE_LOOT.getPath(),
            CompressedStoneLootJeiRecipe.class
        );

    public static final RecipeType<WaterGeneratorConversionJeiRecipe> WATER_GENERATOR_CONVERSION_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_WATER_GENERATOR_CONVERSION.getNamespace(),
            ModJeiIds.COBBLESTONE_WATER_GENERATOR_CONVERSION.getPath(),
            WaterGeneratorConversionJeiRecipe.class
        );

    private static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> holderRecipeType(ResourceLocation id) {
        return RecipeType.create(id.getNamespace(), id.getPath(), (Class) RecipeHolder.class);
    }

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneFurnaceRecipe>, CobblestoneFurnaceMenu> COBBLESTONE_FURNACE_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_FURNACE,
            COBBLESTONE_FURNACE_RECIPE_TYPE,
            registration -> new CobblestoneFurnaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_FURNACE.get()),
            CobblestoneFurnaceMenu.class,
            ModMenuType.COBBLESTONE_FURNACE_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneCrusherRecipe>, CobblestoneCrusherMenu> COBBLESTONE_CRUSHER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_CRUSHER,
            COBBLESTONE_CRUSHER_RECIPE_TYPE,
            registration -> new CobblestoneCrusherRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_CRUSHER.get()),
            CobblestoneCrusherMenu.class,
            ModMenuType.COBBLESTONE_CRUSHER_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestonePoweredFurnaceRecipe>, CobblestonePoweredFurnaceMenu> COBBLESTONE_POWERED_FURNACE_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_POWERED_FURNACE,
            COBBLESTONE_POWERED_FURNACE_RECIPE_TYPE,
            registration -> new CobblestonePoweredFurnaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_POWERED_FURNACE.get()),
            CobblestonePoweredFurnaceMenu.class,
            ModMenuType.COBBLESTONE_POWERED_FURNACE_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneExtremeCompressorRecipe>, CobblestoneExtremeCompressorMenu> COBBLESTONE_EXTREME_COMPRESSOR_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_EXTREME_COMPRESSOR,
            COBBLESTONE_EXTREME_COMPRESSOR_RECIPE_TYPE,
            registration -> new CobblestoneExtremeCompressorRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_EXTREME_COMPRESSOR.get()),
            CobblestoneExtremeCompressorMenu.class,
            ModMenuType.COBBLESTONE_EXTREME_COMPRESSOR_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneCentrifugeRecipe>, CobblestoneCentrifugeMenu> COBBLESTONE_CENTRIFUGE_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_CENTRIFUGE,
            COBBLESTONE_CENTRIFUGE_RECIPE_TYPE,
            registration -> new CobblestoneCentrifugeRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_CENTRIFUGE.get()),
            CobblestoneCentrifugeMenu.class,
            ModMenuType.COBBLESTONE_CENTRIFUGE_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneLaserDrillRecipe>, CobblestoneLaserDrillMenu> COBBLESTONE_LASER_DRILL_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_LASER_DRILL,
            COBBLESTONE_LASER_DRILL_RECIPE_TYPE,
            registration -> new CobblestoneLaserDrillRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_LASER_DRILL.get()),
            CobblestoneLaserDrillMenu.class,
            ModMenuType.COBBLESTONE_LASER_DRILL_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneMixerRecipe>, CobblestoneMixerMenu> COBBLESTONE_MIXER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_MIXER,
            COBBLESTONE_MIXER_RECIPE_TYPE,
            registration -> new CobblestoneMixerRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_MIXER.get()),
            CobblestoneMixerMenu.class,
            ModMenuType.COBBLESTONE_MIXER_MENU
        );

    private static final MachineJeiDefinition<StoneBreakSimulatorJeiRecipe, StoneBreakSimulatorMenu> STONE_BREAK_SIMULATOR_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.STONE_BREAK_SIMULATOR,
            STONE_BREAK_SIMULATOR_RECIPE_TYPE,
            registration -> new StoneBreakSimulatorRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.STONE_BREAK_SIMULATOR.get()),
            StoneBreakSimulatorMenu.class,
            ModMenuType.STONE_BREAK_SIMULATOR_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneMelterRecipe>, CobblestoneMelterMenu> COBBLESTONE_MELTER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_MELTER,
            COBBLESTONE_MELTER_RECIPE_TYPE,
            registration -> new CobblestoneMelterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_MELTER.get()),
            CobblestoneMelterMenu.class,
            ModMenuType.COBBLESTONE_MELTER_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneAssemblyMachineRecipe>, CobblestoneAssemblyMachineMenu> COBBLESTONE_ASSEMBLY_MACHINE_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_ASSEMBLY_MACHINE,
            COBBLESTONE_ASSEMBLY_MACHINE_RECIPE_TYPE,
            registration -> new CobblestoneAssemblyMachineRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE.get()),
            CobblestoneAssemblyMachineMenu.class,
            ModMenuType.COBBLESTONE_ASSEMBLY_MACHINE_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneEnchanterRecipe>, CobblestoneEnchanterMenu> COBBLESTONE_ENCHANTER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_ENCHANTER,
            COBBLESTONE_ENCHANTER_RECIPE_TYPE,
            registration -> new CobblestoneEnchanterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_ENCHANTER.get()),
            CobblestoneEnchanterMenu.class,
            ModMenuType.COBBLESTONE_ENCHANTER_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneChemicalReactorRecipe>, CobblestoneChemicalReactorMenu> COBBLESTONE_CHEMICAL_REACTOR_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_CHEMICAL_REACTOR,
            COBBLESTONE_CHEMICAL_REACTOR_RECIPE_TYPE,
            registration -> new CobblestoneChemicalReactorRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_CHEMICAL_REACTOR.get()),
            CobblestoneChemicalReactorMenu.class,
            ModMenuType.COBBLESTONE_CHEMICAL_REACTOR_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneReactionChamberRecipe>, CobblestoneReactionChamberMenu> COBBLESTONE_REACTION_CHAMBER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_REACTION_CHAMBER,
            COBBLESTONE_REACTION_CHAMBER_RECIPE_TYPE,
            registration -> new CobblestoneReactionChamberRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_REACTION_CHAMBER.get()),
            CobblestoneReactionChamberMenu.class,
            ModMenuType.COBBLESTONE_REACTION_CHAMBER_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneCrystallizationChamberRecipe>, CobblestoneCrystallizationChamberMenu> COBBLESTONE_CRYSTALLIZATION_CHAMBER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_CRYSTALLIZATION_CHAMBER,
            COBBLESTONE_CRYSTALLIZATION_CHAMBER_RECIPE_TYPE,
            registration -> new CobblestoneCrystallizationChamberRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get()),
            CobblestoneCrystallizationChamberMenu.class,
            ModMenuType.COBBLESTONE_CRYSTALLIZATION_CHAMBER_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneDissolutionChamberRecipe>, CobblestoneDissolutionChamberMenu> COBBLESTONE_DISSOLUTION_CHAMBER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_DISSOLUTION_CHAMBER,
            COBBLESTONE_DISSOLUTION_CHAMBER_RECIPE_TYPE,
            registration -> new CobblestoneDissolutionChamberRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER.get()),
            CobblestoneDissolutionChamberMenu.class,
            ModMenuType.COBBLESTONE_DISSOLUTION_CHAMBER_MENU
        );

    private static final MachineJeiDefinition<RecipeHolder<CobblestoneFluidMixerRecipe>, CobblestoneFluidMixerMenu> COBBLESTONE_FLUID_MIXER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_FLUID_MIXER,
            COBBLESTONE_FLUID_MIXER_RECIPE_TYPE,
            registration -> new CobblestoneFluidMixerRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_FLUID_MIXER.get()),
            CobblestoneFluidMixerMenu.class,
            ModMenuType.COBBLESTONE_FLUID_MIXER_MENU
        );

    private static final List<MachineJeiDefinition<?, ?>> MACHINE_DEFINITIONS = List.of(
        COBBLESTONE_FURNACE_DEFINITION,
        COBBLESTONE_POWERED_FURNACE_DEFINITION,
        COBBLESTONE_EXTREME_COMPRESSOR_DEFINITION,
        COBBLESTONE_CRUSHER_DEFINITION,
        COBBLESTONE_CENTRIFUGE_DEFINITION,
        COBBLESTONE_LASER_DRILL_DEFINITION,
        COBBLESTONE_MIXER_DEFINITION,
        STONE_BREAK_SIMULATOR_DEFINITION,
        COBBLESTONE_MELTER_DEFINITION,
        COBBLESTONE_ASSEMBLY_MACHINE_DEFINITION,
        COBBLESTONE_ENCHANTER_DEFINITION,
        COBBLESTONE_CHEMICAL_REACTOR_DEFINITION,
        COBBLESTONE_REACTION_CHAMBER_DEFINITION,
        COBBLESTONE_CRYSTALLIZATION_CHAMBER_DEFINITION,
        COBBLESTONE_DISSOLUTION_CHAMBER_DEFINITION,
        COBBLESTONE_FLUID_MIXER_DEFINITION
    );

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CompressedStoneLootRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new WaterGeneratorConversionRecipeCategory(registration.getJeiHelpers().getGuiHelper()));

        for (MachineJeiDefinition<?, ?> definition : MACHINE_DEFINITIONS) {
            registration.addRecipeCategories(definition.createCategory(registration));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        registration.addRecipes(COMPRESSED_STONE_LOOT_RECIPE_TYPE, CompressedStoneLootJeiRecipe.createRecipes());
        registration.addRecipes(WATER_GENERATOR_CONVERSION_RECIPE_TYPE, WaterGeneratorConversionJeiRecipe.createRecipes());

        // RecipeHolder ごと渡して JEI registry name（holder.id）を残す。
        registration.addRecipes(
            COBBLESTONE_FURNACE_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_FURNACE.get())
        );

        // 独自レシピ + 通常かまど全件（cookingTime を total CP に換算）を同じカテゴリへ出す。
        registration.addRecipes(
            COBBLESTONE_POWERED_FURNACE_DEFINITION.recipeType(),
            CobblestonePoweredFurnaceRecipeHelper.collectAllDisplayRecipes(minecraft.level)
        );

        registration.addRecipes(
            COBBLESTONE_EXTREME_COMPRESSOR_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_EXTREME_COMPRESSOR.get())
        );

        // 独自レシピ + AE2 刻印機の粉砕相当を同じカテゴリへ出す。
        registration.addRecipes(
            COBBLESTONE_CRUSHER_DEFINITION.recipeType(),
            CobblestoneCrusherRecipeHelper.collectAllDisplayRecipes(minecraft.level)
        );

        registration.addRecipes(
            COBBLESTONE_CENTRIFUGE_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CENTRIFUGE.get())
        );

        registration.addRecipes(
            COBBLESTONE_LASER_DRILL_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_LASER_DRILL.get())
        );

        registration.addRecipes(
            COBBLESTONE_MIXER_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_MIXER.get())
        );

        registration.addRecipes(
            STONE_BREAK_SIMULATOR_DEFINITION.recipeType(),
            StoneBreakSimulatorJeiRecipe.createRecipes(
                minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.STONE_BREAK_SIMULATOR.get())
            )
        );

        registration.addRecipes(
            COBBLESTONE_MELTER_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_MELTER.get())
        );

        registration.addRecipes(
            COBBLESTONE_ASSEMBLY_MACHINE_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_ASSEMBLY_MACHINE.get())
        );

        registration.addRecipes(
            COBBLESTONE_ENCHANTER_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_ENCHANTER.get())
        );

        registration.addRecipes(
            COBBLESTONE_CHEMICAL_REACTOR_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CHEMICAL_REACTOR.get())
        );

        registration.addRecipes(
            COBBLESTONE_REACTION_CHAMBER_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_REACTION_CHAMBER.get())
        );

        registration.addRecipes(
            COBBLESTONE_CRYSTALLIZATION_CHAMBER_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get())
        );

        registration.addRecipes(
            COBBLESTONE_DISSOLUTION_CHAMBER_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_DISSOLUTION_CHAMBER.get())
        );

        registration.addRecipes(
            COBBLESTONE_FLUID_MIXER_DEFINITION.recipeType(),
            minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_FLUID_MIXER.get())
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COBBLESTONE_WATER_GENERATOR.get()), WATER_GENERATOR_CONVERSION_RECIPE_TYPE);
        // バニラ作業台レシピをそのまま使うため、作業台カテゴリの catalyst として登録します。
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COBBLESTONE_POWERED_CRAFTER.get()), RecipeTypes.CRAFTING);

        for (MachineJeiDefinition<?, ?> definition : MACHINE_DEFINITIONS) {
            registration.addRecipeCatalyst(definition.createCatalyst(), definition.recipeType());
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // addGuiContainerHandler は画面クラスごとに登録しておく方が確実なので、
        // JEI 入口を持つ各 Screen を明示的に結びます。
        this.registerBaseScreenGuiHandler(registration, CobblestoneFurnaceScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestonePoweredFurnaceScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneExtremeCompressorScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneCrusherScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneCentrifugeScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneLaserDrillScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneMixerScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestonePoweredCrafterScreen.class);
        this.registerBaseScreenGuiHandler(registration, StoneBreakSimulatorScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneMelterScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneAssemblyMachineScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneEnchanterScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneChemicalReactorScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneReactionChamberScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneCrystallizationChamberScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneDissolutionChamberScreen.class);
        this.registerBaseScreenGuiHandler(registration, CobblestoneFluidMixerScreen.class);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        for (MachineJeiDefinition<?, ?> definition : MACHINE_DEFINITIONS) {
            registerTransferHandler(registration, definition);
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JeiFluidLookupRuntimeBridge.setRuntime(jeiRuntime);
    }

    @Override
    public void onRuntimeUnavailable() {
        JeiFluidLookupRuntimeBridge.clearRuntime();
    }

    private static Optional<IGuiClickableArea> createClickableArea(BaseScreen<?> screen, JeiClickableAreaDefinition definition) {
        Optional<RecipeType<?>> recipeType = findRecipeType(definition.recipeCategoryId());
        if (recipeType.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(IGuiClickableArea.createBasic(
            definition.x(),
            definition.y(),
            definition.width(),
            definition.height(),
            recipeType.get()
        ));
    }

    private static Optional<RecipeType<?>> findRecipeType(ResourceLocation recipeCategoryId) {
        for (MachineJeiDefinition<?, ?> definition : MACHINE_DEFINITIONS) {
            if (definition.recipeCategoryId().equals(recipeCategoryId)) {
                return Optional.of(definition.recipeType());
            }
        }

        return Optional.empty();
    }

    private <T extends BaseScreen<?>> void registerBaseScreenGuiHandler(IGuiHandlerRegistration registration, Class<T> screenClass) {
        registration.addGuiContainerHandler(screenClass, new IGuiContainerHandler<T>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(T screen, double mouseX, double mouseY) {
                return screen.getJeiClickableAreaDefinitions().stream()
                    .map(definition -> ModJeiPlugin.createClickableArea(screen, definition))
                    .flatMap(Optional::stream)
                    .toList();
            }
        });
        registration.addGhostIngredientHandler(screenClass, new SlotFilterGhostIngredientHandler<T>());
    }

    private static <R, C extends BaseMenu> void registerTransferHandler(
        IRecipeTransferRegistration registration,
        MachineJeiDefinition<R, C> definition
    ) {
        registration.addRecipeTransferHandler(new BaseMenuRecipeTransferInfo<>(
            definition.menuClass(),
            definition.menuType().get(),
            definition.recipeType(),
            definition.recipeCategoryId()
        ));
    }

    private record MachineJeiDefinition<R, C extends BaseMenu>(
        ResourceLocation recipeCategoryId,
        RecipeType<R> recipeType,
        CategoryFactory<R> categoryFactory,
        CatalystFactory catalystFactory,
        Class<? extends C> menuClass,
        Supplier<MenuType<C>> menuType
    ) {
        private IRecipeCategory<R> createCategory(IRecipeCategoryRegistration registration) {
            return this.categoryFactory.create(registration);
        }

        private ItemStack createCatalyst() {
            return this.catalystFactory.create();
        }
    }

    @FunctionalInterface
    private interface CategoryFactory<R> {
        IRecipeCategory<R> create(IRecipeCategoryRegistration registration);
    }

    @FunctionalInterface
    private interface CatalystFactory {
        ItemStack create();
    }

    private static class BaseMenuRecipeTransferInfo<C extends BaseMenu, R> implements IRecipeTransferInfo<C, R> {
        private final Class<? extends C> menuClass;
        private final MenuType<C> menuType;
        private final RecipeType<R> recipeType;
        private final ResourceLocation recipeCategoryId;

        private BaseMenuRecipeTransferInfo(
            Class<? extends C> menuClass,
            MenuType<C> menuType,
            RecipeType<R> recipeType,
            ResourceLocation recipeCategoryId
        ) {
            this.menuClass = menuClass;
            this.menuType = menuType;
            this.recipeType = recipeType;
            this.recipeCategoryId = recipeCategoryId;
        }

        @Override
        public Class<? extends C> getContainerClass() {
            return this.menuClass;
        }

        @Override
        public Optional<MenuType<C>> getMenuType() {
            return Optional.of(this.menuType);
        }

        @Override
        public RecipeType<R> getRecipeType() {
            return this.recipeType;
        }

        @Override
        public boolean canHandle(C container, R recipe) {
            return this.findDefinition(container).isPresent();
        }

        @Override
        public List<Slot> getRecipeSlots(C container, R recipe) {
            Optional<JeiRecipeTransferDefinition> definition = this.findDefinition(container);
            if (definition.isEmpty()) {
                return List.of();
            }

            return this.getSlots(container, definition.get().recipeSlotStart(), definition.get().recipeSlotCount());
        }

        @Override
        public List<Slot> getInventorySlots(C container, R recipe) {
            Optional<JeiRecipeTransferDefinition> definition = this.findDefinition(container);
            if (definition.isEmpty()) {
                return List.of();
            }

            return this.getSlots(container, definition.get().inventorySlotStart(), definition.get().inventorySlotCount());
        }

        private Optional<JeiRecipeTransferDefinition> findDefinition(C container) {
            return container.getJeiRecipeTransferDefinitions().stream()
                .filter(definition -> definition.recipeCategoryId().equals(this.recipeCategoryId))
                .findFirst();
        }

        private List<Slot> getSlots(C container, int startIndex, int slotCount) {
            int endIndexExclusive = startIndex + slotCount;
            if (startIndex < 0 || slotCount <= 0 || endIndexExclusive > container.slots.size()) {
                return List.of();
            }

            return List.copyOf(container.slots.subList(startIndex, endIndexExclusive));
        }
    }
}
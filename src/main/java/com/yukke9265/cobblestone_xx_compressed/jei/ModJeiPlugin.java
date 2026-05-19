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
import com.yukke9265.cobblestone_xx_compressed.menu.BaseMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneAssemblyMachineMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneChemicalReactorMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCrusherMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCentrifugeMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneDissolutionChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFluidMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneLaserDrillMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMelterMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestonePoweredFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneReactionChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCrystallizationChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneAssemblyMachineRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneChemicalReactorRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneDissolutionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFluidMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneLaserDrillRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMelterRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneReactionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrystallizationChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.screen.BaseScreen;

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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import java.util.function.Supplier;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    public static final RecipeType<CobblestoneFurnaceRecipe> COBBLESTONE_FURNACE_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_FURNACE.getNamespace(),
            ModJeiIds.COBBLESTONE_FURNACE.getPath(),
            CobblestoneFurnaceRecipe.class
        );

    public static final RecipeType<CobblestoneCrusherRecipe> COBBLESTONE_CRUSHER_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_CRUSHER.getNamespace(),
            ModJeiIds.COBBLESTONE_CRUSHER.getPath(),
            CobblestoneCrusherRecipe.class
        );

    public static final RecipeType<CobblestonePoweredFurnaceRecipe> COBBLESTONE_POWERED_FURNACE_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_POWERED_FURNACE.getNamespace(),
            ModJeiIds.COBBLESTONE_POWERED_FURNACE.getPath(),
            CobblestonePoweredFurnaceRecipe.class
        );

    public static final RecipeType<CobblestoneCentrifugeRecipe> COBBLESTONE_CENTRIFUGE_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_CENTRIFUGE.getNamespace(),
            ModJeiIds.COBBLESTONE_CENTRIFUGE.getPath(),
            CobblestoneCentrifugeRecipe.class
        );

    public static final RecipeType<CobblestoneLaserDrillRecipe> COBBLESTONE_LASER_DRILL_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_LASER_DRILL.getNamespace(),
            ModJeiIds.COBBLESTONE_LASER_DRILL.getPath(),
            CobblestoneLaserDrillRecipe.class
        );

    public static final RecipeType<CobblestoneMixerRecipe> COBBLESTONE_MIXER_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_MIXER.getNamespace(),
            ModJeiIds.COBBLESTONE_MIXER.getPath(),
            CobblestoneMixerRecipe.class
        );

    public static final RecipeType<CobblestoneMelterRecipe> COBBLESTONE_MELTER_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_MELTER.getNamespace(),
            ModJeiIds.COBBLESTONE_MELTER.getPath(),
            CobblestoneMelterRecipe.class
        );

    public static final RecipeType<CobblestoneAssemblyMachineRecipe> COBBLESTONE_ASSEMBLY_MACHINE_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_ASSEMBLY_MACHINE.getNamespace(),
            ModJeiIds.COBBLESTONE_ASSEMBLY_MACHINE.getPath(),
            CobblestoneAssemblyMachineRecipe.class
        );

    public static final RecipeType<CobblestoneChemicalReactorRecipe> COBBLESTONE_CHEMICAL_REACTOR_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_CHEMICAL_REACTOR.getNamespace(),
            ModJeiIds.COBBLESTONE_CHEMICAL_REACTOR.getPath(),
            CobblestoneChemicalReactorRecipe.class
        );

    public static final RecipeType<CobblestoneReactionChamberRecipe> COBBLESTONE_REACTION_CHAMBER_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_REACTION_CHAMBER.getNamespace(),
            ModJeiIds.COBBLESTONE_REACTION_CHAMBER.getPath(),
            CobblestoneReactionChamberRecipe.class
        );

    public static final RecipeType<CobblestoneCrystallizationChamberRecipe> COBBLESTONE_CRYSTALLIZATION_CHAMBER_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_CRYSTALLIZATION_CHAMBER.getNamespace(),
            ModJeiIds.COBBLESTONE_CRYSTALLIZATION_CHAMBER.getPath(),
            CobblestoneCrystallizationChamberRecipe.class
        );

    public static final RecipeType<CobblestoneDissolutionChamberRecipe> COBBLESTONE_DISSOLUTION_CHAMBER_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_DISSOLUTION_CHAMBER.getNamespace(),
            ModJeiIds.COBBLESTONE_DISSOLUTION_CHAMBER.getPath(),
            CobblestoneDissolutionChamberRecipe.class
        );

    public static final RecipeType<CobblestoneFluidMixerRecipe> COBBLESTONE_FLUID_MIXER_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COBBLESTONE_FLUID_MIXER.getNamespace(),
            ModJeiIds.COBBLESTONE_FLUID_MIXER.getPath(),
            CobblestoneFluidMixerRecipe.class
        );

    public static final RecipeType<CompressedStoneLootJeiRecipe> COMPRESSED_STONE_LOOT_RECIPE_TYPE =
        RecipeType.create(
            ModJeiIds.COMPRESSED_STONE_LOOT.getNamespace(),
            ModJeiIds.COMPRESSED_STONE_LOOT.getPath(),
            CompressedStoneLootJeiRecipe.class
        );

    private static final MachineJeiDefinition<CobblestoneFurnaceRecipe, CobblestoneFurnaceMenu> COBBLESTONE_FURNACE_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_FURNACE,
            COBBLESTONE_FURNACE_RECIPE_TYPE,
            registration -> new CobblestoneFurnaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_FURNACE.get()),
            CobblestoneFurnaceMenu.class,
            ModMenuType.COBBLESTONE_FURNACE_MENU
        );

    private static final MachineJeiDefinition<CobblestoneCrusherRecipe, CobblestoneCrusherMenu> COBBLESTONE_CRUSHER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_CRUSHER,
            COBBLESTONE_CRUSHER_RECIPE_TYPE,
            registration -> new CobblestoneCrusherRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_CRUSHER.get()),
            CobblestoneCrusherMenu.class,
            ModMenuType.COBBLESTONE_CRUSHER_MENU
        );

    private static final MachineJeiDefinition<CobblestonePoweredFurnaceRecipe, CobblestonePoweredFurnaceMenu> COBBLESTONE_POWERED_FURNACE_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_POWERED_FURNACE,
            COBBLESTONE_POWERED_FURNACE_RECIPE_TYPE,
            registration -> new CobblestonePoweredFurnaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_POWERED_FURNACE.get()),
            CobblestonePoweredFurnaceMenu.class,
            ModMenuType.COBBLESTONE_POWERED_FURNACE_MENU
        );

    private static final MachineJeiDefinition<CobblestoneCentrifugeRecipe, CobblestoneCentrifugeMenu> COBBLESTONE_CENTRIFUGE_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_CENTRIFUGE,
            COBBLESTONE_CENTRIFUGE_RECIPE_TYPE,
            registration -> new CobblestoneCentrifugeRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_CENTRIFUGE.get()),
            CobblestoneCentrifugeMenu.class,
            ModMenuType.COBBLESTONE_CENTRIFUGE_MENU
        );

    private static final MachineJeiDefinition<CobblestoneLaserDrillRecipe, CobblestoneLaserDrillMenu> COBBLESTONE_LASER_DRILL_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_LASER_DRILL,
            COBBLESTONE_LASER_DRILL_RECIPE_TYPE,
            registration -> new CobblestoneLaserDrillRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_LASER_DRILL.get()),
            CobblestoneLaserDrillMenu.class,
            ModMenuType.COBBLESTONE_LASER_DRILL_MENU
        );

    private static final MachineJeiDefinition<CobblestoneMixerRecipe, CobblestoneMixerMenu> COBBLESTONE_MIXER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_MIXER,
            COBBLESTONE_MIXER_RECIPE_TYPE,
            registration -> new CobblestoneMixerRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_MIXER.get()),
            CobblestoneMixerMenu.class,
            ModMenuType.COBBLESTONE_MIXER_MENU
        );

    private static final MachineJeiDefinition<CobblestoneMelterRecipe, CobblestoneMelterMenu> COBBLESTONE_MELTER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_MELTER,
            COBBLESTONE_MELTER_RECIPE_TYPE,
            registration -> new CobblestoneMelterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_MELTER.get()),
            CobblestoneMelterMenu.class,
            ModMenuType.COBBLESTONE_MELTER_MENU
        );

    private static final MachineJeiDefinition<CobblestoneAssemblyMachineRecipe, CobblestoneAssemblyMachineMenu> COBBLESTONE_ASSEMBLY_MACHINE_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_ASSEMBLY_MACHINE,
            COBBLESTONE_ASSEMBLY_MACHINE_RECIPE_TYPE,
            registration -> new CobblestoneAssemblyMachineRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE.get()),
            CobblestoneAssemblyMachineMenu.class,
            ModMenuType.COBBLESTONE_ASSEMBLY_MACHINE_MENU
        );

    private static final MachineJeiDefinition<CobblestoneChemicalReactorRecipe, CobblestoneChemicalReactorMenu> COBBLESTONE_CHEMICAL_REACTOR_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_CHEMICAL_REACTOR,
            COBBLESTONE_CHEMICAL_REACTOR_RECIPE_TYPE,
            registration -> new CobblestoneChemicalReactorRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_CHEMICAL_REACTOR.get()),
            CobblestoneChemicalReactorMenu.class,
            ModMenuType.COBBLESTONE_CHEMICAL_REACTOR_MENU
        );

    private static final MachineJeiDefinition<CobblestoneReactionChamberRecipe, CobblestoneReactionChamberMenu> COBBLESTONE_REACTION_CHAMBER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_REACTION_CHAMBER,
            COBBLESTONE_REACTION_CHAMBER_RECIPE_TYPE,
            registration -> new CobblestoneReactionChamberRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_REACTION_CHAMBER.get()),
            CobblestoneReactionChamberMenu.class,
            ModMenuType.COBBLESTONE_REACTION_CHAMBER_MENU
        );

    private static final MachineJeiDefinition<CobblestoneCrystallizationChamberRecipe, CobblestoneCrystallizationChamberMenu> COBBLESTONE_CRYSTALLIZATION_CHAMBER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_CRYSTALLIZATION_CHAMBER,
            COBBLESTONE_CRYSTALLIZATION_CHAMBER_RECIPE_TYPE,
            registration -> new CobblestoneCrystallizationChamberRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get()),
            CobblestoneCrystallizationChamberMenu.class,
            ModMenuType.COBBLESTONE_CRYSTALLIZATION_CHAMBER_MENU
        );

    private static final MachineJeiDefinition<CobblestoneDissolutionChamberRecipe, CobblestoneDissolutionChamberMenu> COBBLESTONE_DISSOLUTION_CHAMBER_DEFINITION =
        new MachineJeiDefinition<>(
            ModJeiIds.COBBLESTONE_DISSOLUTION_CHAMBER,
            COBBLESTONE_DISSOLUTION_CHAMBER_RECIPE_TYPE,
            registration -> new CobblestoneDissolutionChamberRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            () -> new ItemStack(ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER.get()),
            CobblestoneDissolutionChamberMenu.class,
            ModMenuType.COBBLESTONE_DISSOLUTION_CHAMBER_MENU
        );

    private static final MachineJeiDefinition<CobblestoneFluidMixerRecipe, CobblestoneFluidMixerMenu> COBBLESTONE_FLUID_MIXER_DEFINITION =
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
        COBBLESTONE_CRUSHER_DEFINITION,
        COBBLESTONE_CENTRIFUGE_DEFINITION,
        COBBLESTONE_LASER_DRILL_DEFINITION,
        COBBLESTONE_MIXER_DEFINITION,
        COBBLESTONE_MELTER_DEFINITION,
        COBBLESTONE_ASSEMBLY_MACHINE_DEFINITION,
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

        // レシピの登録本体は機械ごとに違いますが、plugin 側の呼び出し手順は共通化します。
        List<CobblestoneFurnaceRecipe> recipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_FURNACE.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_FURNACE_DEFINITION.recipeType(), recipes);

        List<CobblestonePoweredFurnaceRecipe> poweredFurnaceRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_POWERED_FURNACE.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_POWERED_FURNACE_DEFINITION.recipeType(), poweredFurnaceRecipes);

        List<CobblestoneCrusherRecipe> crusherRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CRUSHER.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_CRUSHER_DEFINITION.recipeType(), crusherRecipes);

        List<CobblestoneCentrifugeRecipe> centrifugeRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CENTRIFUGE.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_CENTRIFUGE_DEFINITION.recipeType(), centrifugeRecipes);

        List<CobblestoneLaserDrillRecipe> laserDrillRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_LASER_DRILL.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_LASER_DRILL_DEFINITION.recipeType(), laserDrillRecipes);

        List<CobblestoneMixerRecipe> mixerRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_MIXER.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_MIXER_DEFINITION.recipeType(), mixerRecipes);

        List<CobblestoneMelterRecipe> melterRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_MELTER.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_MELTER_DEFINITION.recipeType(), melterRecipes);

        List<CobblestoneAssemblyMachineRecipe> assemblyMachineRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_ASSEMBLY_MACHINE.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_ASSEMBLY_MACHINE_DEFINITION.recipeType(), assemblyMachineRecipes);

        List<CobblestoneChemicalReactorRecipe> chemicalReactorRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CHEMICAL_REACTOR.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_CHEMICAL_REACTOR_DEFINITION.recipeType(), chemicalReactorRecipes);

        List<CobblestoneReactionChamberRecipe> reactionChamberRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_REACTION_CHAMBER.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_REACTION_CHAMBER_DEFINITION.recipeType(), reactionChamberRecipes);

        List<CobblestoneCrystallizationChamberRecipe> crystallizationChamberRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_CRYSTALLIZATION_CHAMBER_DEFINITION.recipeType(), crystallizationChamberRecipes);

        List<CobblestoneDissolutionChamberRecipe> dissolutionChamberRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_DISSOLUTION_CHAMBER.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_DISSOLUTION_CHAMBER_DEFINITION.recipeType(), dissolutionChamberRecipes);

        List<CobblestoneFluidMixerRecipe> fluidMixerRecipes = minecraft.level.getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_FLUID_MIXER.get())
            .stream()
            .map(RecipeHolder::value)
            .toList();
        registration.addRecipes(COBBLESTONE_FLUID_MIXER_DEFINITION.recipeType(), fluidMixerRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (MachineJeiDefinition<?, ?> definition : MACHINE_DEFINITIONS) {
            registration.addRecipeCatalyst(definition.createCatalyst(), definition.recipeType());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // BaseScreen が返すクリック領域定義を、JEI の clickable area へ変換します。
        // これで各 Screen は座標とカテゴリ ID だけを返せばよくなります。
        registration.addGuiContainerHandler((Class<? extends BaseScreen<?>>) (Class<?>) BaseScreen.class, new IGuiContainerHandler<BaseScreen<?>>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(BaseScreen<?> screen, double mouseX, double mouseY) {
                return screen.getJeiClickableAreaDefinitions().stream()
                    .map(ModJeiPlugin::createClickableArea)
                    .flatMap(Optional::stream)
                    .toList();
            }
        });
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        for (MachineJeiDefinition<?, ?> definition : MACHINE_DEFINITIONS) {
            registerTransferHandler(registration, definition);
        }
    }

    private static Optional<IGuiClickableArea> createClickableArea(JeiClickableAreaDefinition definition) {
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
package com.yukke9265.cobblestone_xx_compressed.jei;

import java.util.ArrayList;
import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;
import com.yukke9265.cobblestone_xx_compressed.loot.OreBreakDefinition;
import com.yukke9265.cobblestone_xx_compressed.recipe.StoneBreakSimulatorRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

@SuppressWarnings("null")
public class StoneBreakSimulatorJeiRecipe {
    private final ResourceLocation id;
    private final ItemStack input;
    private final Ingredient pickaxeIngredient;
    private final ItemStack mainOutput;
    private final List<BonusDrop> subOutputs;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public StoneBreakSimulatorJeiRecipe(
        ResourceLocation id,
        ItemStack input,
        Ingredient pickaxeIngredient,
        ItemStack mainOutput,
        List<BonusDrop> subOutputs,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.id = id;
        this.input = input;
        this.pickaxeIngredient = pickaxeIngredient;
        this.mainOutput = mainOutput;
        this.subOutputs = subOutputs;
        this.totalCobblestonePower = totalCobblestonePower;
        this.cobblestonePowerPerTick = cobblestonePowerPerTick;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ItemStack getInput() {
        return this.input;
    }

    public Ingredient getPickaxeIngredient() {
        return this.pickaxeIngredient;
    }

    public ItemStack getMainOutput() {
        return this.mainOutput;
    }

    public List<BonusDrop> getSubOutputs() {
        return this.subOutputs;
    }

    public long getTotalCobblestonePower() {
        return this.totalCobblestonePower;
    }

    public long getCobblestonePowerPerTick() {
        return this.cobblestonePowerPerTick;
    }

    /**
     * 元レシピ Holder を入力スタック単位へ展開する。
     * JEIu 用に「元 id / 入力アイテム path」の合成 id を付ける。
     */
    public static List<StoneBreakSimulatorJeiRecipe> createRecipes(List<RecipeHolder<StoneBreakSimulatorRecipe>> recipeHolders) {
        List<StoneBreakSimulatorJeiRecipe> jeiRecipes = new ArrayList<>();

        for (RecipeHolder<StoneBreakSimulatorRecipe> recipeHolder : recipeHolders) {
            StoneBreakSimulatorRecipe recipe = recipeHolder.value();
            for (ItemStack inputStack : recipe.getIngredient().getItems()) {
                ResourceLocation inputId = BuiltInRegistries.ITEM.getKey(inputStack.getItem());
                ResourceLocation displayId = ResourceLocation.fromNamespaceAndPath(
                    CobblestonexXCompressed.MODID,
                    "stone_break_simulator/" + recipeHolder.id().getPath() + "/" + inputId.getNamespace() + "/" + inputId.getPath()
                );

                CompressedStoneLootDefinition definition = CompressedStoneLootDefinition.findByStoneInput(inputStack);
                if (definition != null) {
                    List<BonusDrop> subOutputs = new ArrayList<>();
                    for (CompressedStoneLootDefinition.BonusLootEntry bonusDrop : definition.getBonusDrops()) {
                        if (!bonusDrop.hasResolvedItem()) {
                            continue;
                        }

                        subOutputs.add(new BonusDrop(new ItemStack(bonusDrop.getItem().get()), formatPercent(bonusDrop.getChance())));
                    }

                    jeiRecipes.add(new StoneBreakSimulatorJeiRecipe(
                        displayId,
                        inputStack.copy(),
                        Ingredient.of(ItemTags.PICKAXES),
                        new ItemStack(definition.getCobblestoneBlock().get()),
                        List.copyOf(subOutputs),
                        recipe.getTotalCobblestonePower(),
                        recipe.getCobblestonePowerPerTick()
                    ));
                    continue;
                }

                OreBreakDefinition oreDefinition = OreBreakDefinition.findByInput(inputStack);
                if (oreDefinition == null || !oreDefinition.hasResolvedJeiDrop()) {
                    continue;
                }

                List<BonusDrop> oreSubOutputs = new ArrayList<>();
                if (!oreDefinition.dropsSelfWithoutSilkTouch()) {
                    oreSubOutputs.add(new BonusDrop(inputStack.copy(), BonusDrop.SILK_TOUCH));
                }

                jeiRecipes.add(new StoneBreakSimulatorJeiRecipe(
                    displayId,
                    inputStack.copy(),
                    Ingredient.of(ItemTags.PICKAXES),
                    oreDefinition.createJeiDropStack(),
                    List.copyOf(oreSubOutputs),
                    recipe.getTotalCobblestonePower(),
                    recipe.getCobblestonePowerPerTick()
                ));
            }
        }

        return jeiRecipes;
    }

    private static String formatPercent(double chance) {
        return String.format("%.0f%%", chance * 100.0d);
    }

    public record BonusDrop(ItemStack stack, String chanceText) {
        public static final String SILK_TOUCH = "silk_touch";
    }
}

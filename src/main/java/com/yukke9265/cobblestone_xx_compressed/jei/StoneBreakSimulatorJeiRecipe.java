package com.yukke9265.cobblestone_xx_compressed.jei;

import java.util.ArrayList;
import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;
import com.yukke9265.cobblestone_xx_compressed.recipe.StoneBreakSimulatorRecipe;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@SuppressWarnings("null")
public class StoneBreakSimulatorJeiRecipe {
    private final ItemStack input;
    private final Ingredient pickaxeIngredient;
    private final ItemStack mainOutput;
    private final List<BonusDrop> subOutputs;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public StoneBreakSimulatorJeiRecipe(
        ItemStack input,
        Ingredient pickaxeIngredient,
        ItemStack mainOutput,
        List<BonusDrop> subOutputs,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.input = input;
        this.pickaxeIngredient = pickaxeIngredient;
        this.mainOutput = mainOutput;
        this.subOutputs = subOutputs;
        this.totalCobblestonePower = totalCobblestonePower;
        this.cobblestonePowerPerTick = cobblestonePowerPerTick;
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

    public static List<StoneBreakSimulatorJeiRecipe> createRecipes(List<StoneBreakSimulatorRecipe> recipes) {
        List<StoneBreakSimulatorJeiRecipe> jeiRecipes = new ArrayList<>();

        for (StoneBreakSimulatorRecipe recipe : recipes) {
            for (ItemStack inputStack : recipe.getIngredient().getItems()) {
                CompressedStoneLootDefinition definition = CompressedStoneLootDefinition.findByStoneInput(inputStack);
                if (definition == null) {
                    continue;
                }

                List<BonusDrop> subOutputs = new ArrayList<>();
                for (CompressedStoneLootDefinition.BonusLootEntry bonusDrop : definition.getBonusDrops()) {
                    if (!bonusDrop.hasResolvedItem()) {
                        continue;
                    }

                    subOutputs.add(new BonusDrop(new ItemStack(bonusDrop.getItem().get()), formatPercent(bonusDrop.getChance())));
                }

                jeiRecipes.add(new StoneBreakSimulatorJeiRecipe(
                    inputStack.copy(),
                    Ingredient.of(ItemTags.PICKAXES),
                    new ItemStack(definition.getCobblestoneBlock().get()),
                    List.copyOf(subOutputs),
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
    }
}
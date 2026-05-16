package com.yukke9265.cobblestone_xx_compressed.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;

import net.minecraft.world.item.ItemStack;

public class CompressedStoneLootJeiRecipe {
    private final ItemStack input;
    private final ItemStack silkTouchDrop;
    private final ItemStack normalDrop;
    private final ItemStack gemDrop;
    private final double gemChance;
    private final ItemStack resourceDrop;
    private final double resourceChance;
    private final boolean hasCountRange;
    private final double minCount;
    private final double maxCount;

    public CompressedStoneLootJeiRecipe(
        ItemStack input,
        ItemStack silkTouchDrop,
        ItemStack normalDrop,
        ItemStack gemDrop,
        double gemChance,
        ItemStack resourceDrop,
        double resourceChance,
        boolean hasCountRange,
        double minCount,
        double maxCount
    ) {
        this.input = input;
        this.silkTouchDrop = silkTouchDrop;
        this.normalDrop = normalDrop;
        this.gemDrop = gemDrop;
        this.gemChance = gemChance;
        this.resourceDrop = resourceDrop;
        this.resourceChance = resourceChance;
        this.hasCountRange = hasCountRange;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public ItemStack getInput() {
        return this.input;
    }

    public ItemStack getSilkTouchDrop() {
        return this.silkTouchDrop;
    }

    public ItemStack getNormalDrop() {
        return this.normalDrop;
    }

    public ItemStack getGemDrop() {
        return this.gemDrop;
    }

    public double getGemChance() {
        return this.gemChance;
    }

    public ItemStack getResourceDrop() {
        return this.resourceDrop;
    }

    public double getResourceChance() {
        return this.resourceChance;
    }

    public boolean hasCountRange() {
        return this.hasCountRange;
    }

    public String getGemChanceText() {
        return formatPercent(this.gemChance);
    }

    public String getResourceChanceText() {
        return formatPercent(this.resourceChance);
    }

    public String getMinCountText() {
        return formatNumber(this.minCount);
    }

    public String getMaxCountText() {
        return formatNumber(this.maxCount);
    }

    public static List<CompressedStoneLootJeiRecipe> createRecipes() {
        List<CompressedStoneLootJeiRecipe> recipes = new ArrayList<>();

        for (CompressedStoneLootDefinition definition : CompressedStoneLootDefinition.getDefinitions()) {
            recipes.add(new CompressedStoneLootJeiRecipe(
                new ItemStack(definition.getStoneBlock().get()),
                new ItemStack(definition.getStoneBlock().get()),
                new ItemStack(definition.getCobblestoneBlock().get()),
                new ItemStack(definition.getGemItem().get()),
                definition.getGemChance(),
                new ItemStack(definition.getResourceItem().get()),
                definition.getResourceChance(),
                definition.hasCountRange(),
                definition.getMinCount(),
                definition.getMaxCount()
            ));
        }

        return recipes;
    }

    private static String formatPercent(double chance) {
        double percent = chance * 100.0d;
        if (Math.rint(percent) == percent) {
            return (int) percent + "%";
        }

        return String.format(Locale.ROOT, "%.1f%%", percent);
    }

    private static String formatNumber(double value) {
        if (Math.rint(value) == value) {
            return Integer.toString((int) value);
        }

        return String.format(Locale.ROOT, "%.1f", value);
    }
}
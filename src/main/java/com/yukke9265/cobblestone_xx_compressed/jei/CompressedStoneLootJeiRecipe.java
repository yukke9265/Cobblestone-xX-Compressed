package com.yukke9265.cobblestone_xx_compressed.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;

import net.minecraft.world.item.ItemStack;

@SuppressWarnings("null")
public class CompressedStoneLootJeiRecipe {
    private final ItemStack input;
    private final ItemStack normalDrop;
    private final List<BonusDrop> bonusDrops;

    public CompressedStoneLootJeiRecipe(
        ItemStack input,
        ItemStack normalDrop,
        List<BonusDrop> bonusDrops
    ) {
        this.input = input;
        this.normalDrop = normalDrop;
        this.bonusDrops = List.copyOf(bonusDrops);
    }

    public ItemStack getInput() {
        return this.input;
    }

    public ItemStack getNormalDrop() {
        return this.normalDrop;
    }

    public List<BonusDrop> getBonusDrops() {
        return this.bonusDrops;
    }

    public static List<CompressedStoneLootJeiRecipe> createRecipes() {
        List<CompressedStoneLootJeiRecipe> recipes = new ArrayList<>();

        for (CompressedStoneLootDefinition definition : CompressedStoneLootDefinition.getDefinitions()) {
            List<BonusDrop> bonusDrops = new ArrayList<>();
            for (CompressedStoneLootDefinition.BonusLootEntry bonusDrop : definition.getBonusDrops()) {
                bonusDrops.add(new BonusDrop(
                    new ItemStack(bonusDrop.getItem().get()),
                    bonusDrop.getChance(),
                    bonusDrop.hasCountRange(),
                    bonusDrop.getMinCount(),
                    bonusDrop.getMaxCount()
                ));
            }

            recipes.add(new CompressedStoneLootJeiRecipe(
                new ItemStack(definition.getStoneBlock().get()),
                new ItemStack(definition.getCobblestoneBlock().get()),
                bonusDrops
            ));
        }

        return recipes;
    }

    public static class BonusDrop {
        private final ItemStack drop;
        private final double chance;

        public BonusDrop(ItemStack drop, double chance, boolean hasCountRange, double minCount, double maxCount) {
            this.drop = drop;
            this.chance = chance;
        }

        public ItemStack getDrop() {
            return this.drop;
        }

        public boolean hasCountRange() {
            return false;
        }

        public String getChanceText() {
            return formatPercent(this.chance);
        }

        public String getMinCountText() {
            return "0";
        }

        public String getMaxCountText() {
            return "0";
        }
    }

    private static String formatPercent(double chance) {
        double percent = chance * 100.0d;
        if (Math.rint(percent) == percent) {
            return (int) percent + "%";
        }

        return String.format(Locale.ROOT, "%.1f%%", percent);
    }
}
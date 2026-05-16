package com.yukke9265.cobblestone_xx_compressed.datagen.loot;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class ModBlockLootTableProvider implements DataProvider {
    private static final String MODID = CobblestonexXCompressed.MODID;

    private final PackOutput output;

    public ModBlockLootTableProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Path rootPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK)
            .resolve(MODID)
            .resolve("loot_table")
            .resolve("blocks");

        futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot("cobblestone_furnace"), rootPath.resolve("cobblestone_furnace.json")));
        futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot("cobblestone_crusher"), rootPath.resolve("cobblestone_crusher.json")));
        futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot("cobblestone_centrifuge"), rootPath.resolve("cobblestone_centrifuge.json")));
        futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot("cobblestone_laser_drill"), rootPath.resolve("cobblestone_laser_drill.json")));
        futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot("cobblestone_mixer"), rootPath.resolve("cobblestone_mixer.json")));
        futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot("compressed_cobblestone"), rootPath.resolve("compressed_cobblestone.json")));
        futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot("cobblestone_machine_casing"), rootPath.resolve("cobblestone_machine_casing.json")));
        futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot("cobblestone_tank"), rootPath.resolve("cobblestone_tank.json")));

        for (ModBlocks.TierCobblestoneGenerator generatorVariant : ModBlocks.TierCobblestoneGenerator.values()) {
            String name = generatorVariant.getRegistryName();
            futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot(name), rootPath.resolve(name + ".json")));
        }

        for (ModBlocks.TierCompressedCobblestone tier : ModBlocks.TierCompressedCobblestone.values()) {
            String name = tier.getRegistryName();
            futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot(name), rootPath.resolve(name + ".json")));
        }

        for (ModBlocks.TierCobblestoneMachineCasing tier : ModBlocks.TierCobblestoneMachineCasing.values()) {
            String name = tier.getRegistryName();
            futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot(name), rootPath.resolve(name + ".json")));
        }

        for (ModBlocks.TierCobblestoneTank tier : ModBlocks.TierCobblestoneTank.values()) {
            String name = tier.getRegistryName();
            futures.add(DataProvider.saveStable(cache, createSimpleSelfDropLoot(name), rootPath.resolve(name + ".json")));
        }

        for (CompressedStoneLootDefinition definition : CompressedStoneLootDefinition.getDefinitions()) {
            futures.add(DataProvider.saveStable(cache, createCompressedStoneLoot(definition), rootPath.resolve(getBlockPath(definition.getStoneBlock().get()) + ".json")));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "CobblestonexXCompressed Block Loot Tables";
    }

    private JsonObject createSimpleSelfDropLoot(String blockName) {
        JsonObject root = createBlockLootRoot(blockName);
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("bonus_rolls", 0.0d);
        pool.add("entries", singleItemEntries(modItemName(blockName), true));
        pool.addProperty("rolls", 1.0d);
        pools.add(pool);
        root.add("pools", pools);
        return root;
    }

    private JsonObject createCompressedStoneLoot(CompressedStoneLootDefinition definition) {
        JsonObject root = createBlockLootRoot(getBlockPath(definition.getStoneBlock().get()));
        JsonArray pools = new JsonArray();

        JsonObject mainPool = new JsonObject();
        mainPool.addProperty("bonus_rolls", 0.0d);
        JsonArray mainEntries = new JsonArray();
        JsonObject alternatives = new JsonObject();
        alternatives.addProperty("type", "minecraft:alternatives");
        JsonArray children = new JsonArray();
        children.add(singleItemEntry(getItemName(definition.getStoneBlock().get()), false, true, false, 0.0d, false, null, 0.0d, 0.0d));
        children.add(singleItemEntry(getItemName(definition.getCobblestoneBlock().get()), true, false, false, 0.0d, false, null, 0.0d, 0.0d));
        alternatives.add("children", children);
        mainEntries.add(alternatives);
        mainPool.add("entries", mainEntries);
        mainPool.addProperty("rolls", 1.0d);
        pools.add(mainPool);

        pools.add(createBonusPool(getItemName(definition.getGemItem().get()), definition.getGemChance(), false, 0.0d, 0.0d));
        pools.add(createBonusPool(
            getItemName(definition.getResourceItem().get()),
            definition.getResourceChance(),
            definition.hasCountRange(),
            definition.getMinCount(),
            definition.getMaxCount()
        ));

        root.add("pools", pools);
        return root;
    }

    private JsonObject createBonusPool(String itemName, double chance, boolean hasCountRange, double minCount, double maxCount) {
        JsonObject pool = new JsonObject();
        pool.addProperty("bonus_rolls", 0.0d);

        JsonArray conditions = new JsonArray();
        conditions.add(createNoSilkTouchCondition());
        JsonObject chanceCondition = new JsonObject();
        chanceCondition.addProperty("chance", chance);
        chanceCondition.addProperty("condition", "minecraft:random_chance");
        conditions.add(chanceCondition);
        pool.add("conditions", conditions);

        JsonArray entries = new JsonArray();
        entries.add(singleItemEntry(itemName, false, false, true, 0.0d, hasCountRange, "minecraft:fortune", minCount, maxCount));
        pool.add("entries", entries);
        pool.addProperty("rolls", 1.0d);
        return pool;
    }

    private JsonArray singleItemEntries(String itemName, boolean survivesExplosion) {
        JsonArray entries = new JsonArray();
        entries.add(singleItemEntry(itemName, survivesExplosion, false, false, 0.0d, false, null, 0.0d, 0.0d));
        return entries;
    }

    private JsonObject singleItemEntry(
        String itemName,
        boolean survivesExplosion,
        boolean silkTouchOnly,
        boolean applyFortune,
        double unusedChance,
        boolean hasCountRange,
        String enchantmentName,
        double minCount,
        double maxCount
    ) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", itemName);

        if (survivesExplosion || silkTouchOnly) {
            JsonArray conditions = new JsonArray();
            if (silkTouchOnly) {
                conditions.add(createSilkTouchCondition());
            }
            if (survivesExplosion) {
                JsonObject survives = new JsonObject();
                survives.addProperty("condition", "minecraft:survives_explosion");
                conditions.add(survives);
            }
            entry.add("conditions", conditions);
        }

        if (applyFortune || hasCountRange) {
            JsonArray functions = new JsonArray();

            if (hasCountRange) {
                JsonObject setCount = new JsonObject();
                setCount.addProperty("add", false);

                JsonObject count = new JsonObject();
                count.addProperty("type", "minecraft:uniform");
                count.addProperty("max", maxCount);
                count.addProperty("min", minCount);
                setCount.add("count", count);
                setCount.addProperty("function", "minecraft:set_count");
                functions.add(setCount);
            }

            if (applyFortune) {
                JsonObject applyBonus = new JsonObject();
                applyBonus.addProperty("enchantment", enchantmentName);
                applyBonus.addProperty("formula", "minecraft:ore_drops");
                applyBonus.addProperty("function", "minecraft:apply_bonus");
                functions.add(applyBonus);
            }

            JsonObject explosionDecay = new JsonObject();
            explosionDecay.addProperty("function", "minecraft:explosion_decay");
            functions.add(explosionDecay);

            entry.add("functions", functions);
        }

        return entry;
    }

    private JsonObject createSilkTouchCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:match_tool");

        JsonObject predicate = new JsonObject();
        JsonObject predicates = new JsonObject();
        JsonArray enchantments = new JsonArray();
        JsonObject enchantment = new JsonObject();
        enchantment.addProperty("enchantments", "minecraft:silk_touch");

        JsonObject levels = new JsonObject();
        levels.addProperty("min", 1);
        enchantment.add("levels", levels);
        enchantments.add(enchantment);

        predicates.add("minecraft:enchantments", enchantments);
        predicate.add("predicates", predicates);
        condition.add("predicate", predicate);
        return condition;
    }

    private JsonObject createNoSilkTouchCondition() {
        JsonObject inverted = new JsonObject();
        inverted.addProperty("condition", "minecraft:inverted");
        inverted.add("term", createSilkTouchCondition());
        return inverted;
    }

    private JsonObject createBlockLootRoot(String blockName) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:block");
        root.addProperty("random_sequence", MODID + ":blocks/" + blockName);
        return root;
    }

    private String modItemName(String path) {
        return MODID + ":" + path;
    }

    private String getBlockPath(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    private String getItemName(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).toString();
    }
}
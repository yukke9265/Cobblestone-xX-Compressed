package com.yukke9265.cobblestone_xx_compressed.datagen.loot;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneCompatLootDefinition;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, CobblestonexXCompressed.MODID);
    }

    @Override
    protected void start() {
        for (CompressedStoneCompatLootDefinition definition : CompressedStoneCompatLootDefinition.getDefinitions()) {
            LootItemCondition[] conditions = new LootItemCondition[] {
                LootTableIdCondition.builder(Objects.requireNonNull(definition.getTargetLootTableId())).build()
            };

            ResourceKey<net.minecraft.core.Registry<LootTable>> lootTableRegistryKey = Objects.requireNonNull(Registries.LOOT_TABLE);

            add(
                "compressed_stone_compat_" + definition.getStoneBlockPath() + "_" + definition.getItemId().getPath(),
                new AddTableLootModifier(
                    conditions,
                    Objects.requireNonNull(ResourceKey.create(lootTableRegistryKey, Objects.requireNonNull(definition.getCompatLootTableId())))
                ),
                new ModLoadedCondition(Objects.requireNonNull(definition.getRequiredModId()))
            );
        }
    }
}
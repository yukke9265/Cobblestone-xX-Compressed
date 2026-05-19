package com.yukke9265.cobblestone_xx_compressed.registry;

import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
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

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, CobblestonexXCompressed.MODID);

    public static final Supplier<RecipeSerializer<CobblestoneFurnaceRecipe>> COBBLESTONE_FURNACE =
        RECIPE_SERIALIZERS.register(
            "cobblestone_furnace",
            () -> new RecipeSerializer<CobblestoneFurnaceRecipe>() {
                @Override
                public MapCodec<CobblestoneFurnaceRecipe> codec() {
                    return CobblestoneFurnaceRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneFurnaceRecipe> streamCodec() {
                    return CobblestoneFurnaceRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestonePoweredFurnaceRecipe>> COBBLESTONE_POWERED_FURNACE =
        RECIPE_SERIALIZERS.register(
            "cobblestone_powered_furnace",
            () -> new RecipeSerializer<CobblestonePoweredFurnaceRecipe>() {
                @Override
                public MapCodec<CobblestonePoweredFurnaceRecipe> codec() {
                    return CobblestonePoweredFurnaceRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestonePoweredFurnaceRecipe> streamCodec() {
                    return CobblestonePoweredFurnaceRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneCrusherRecipe>> COBBLESTONE_CRUSHER =
        RECIPE_SERIALIZERS.register(
            "cobblestone_crusher",
            () -> new RecipeSerializer<CobblestoneCrusherRecipe>() {
                @Override
                public MapCodec<CobblestoneCrusherRecipe> codec() {
                    return CobblestoneCrusherRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneCrusherRecipe> streamCodec() {
                    return CobblestoneCrusherRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneCentrifugeRecipe>> COBBLESTONE_CENTRIFUGE =
        RECIPE_SERIALIZERS.register(
            "cobblestone_centrifuge",
            () -> new RecipeSerializer<CobblestoneCentrifugeRecipe>() {
                @Override
                public MapCodec<CobblestoneCentrifugeRecipe> codec() {
                    return CobblestoneCentrifugeRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneCentrifugeRecipe> streamCodec() {
                    return CobblestoneCentrifugeRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneLaserDrillRecipe>> COBBLESTONE_LASER_DRILL =
        RECIPE_SERIALIZERS.register(
            "cobblestone_laser_drill",
            () -> new RecipeSerializer<CobblestoneLaserDrillRecipe>() {
                @Override
                public MapCodec<CobblestoneLaserDrillRecipe> codec() {
                    return CobblestoneLaserDrillRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneLaserDrillRecipe> streamCodec() {
                    return CobblestoneLaserDrillRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneMixerRecipe>> COBBLESTONE_MIXER =
        RECIPE_SERIALIZERS.register(
            "cobblestone_mixer",
            () -> new RecipeSerializer<CobblestoneMixerRecipe>() {
                @Override
                public MapCodec<CobblestoneMixerRecipe> codec() {
                    return CobblestoneMixerRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneMixerRecipe> streamCodec() {
                    return CobblestoneMixerRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneMelterRecipe>> COBBLESTONE_MELTER =
        RECIPE_SERIALIZERS.register(
            "cobblestone_melter",
            () -> new RecipeSerializer<CobblestoneMelterRecipe>() {
                @Override
                public MapCodec<CobblestoneMelterRecipe> codec() {
                    return CobblestoneMelterRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneMelterRecipe> streamCodec() {
                    return CobblestoneMelterRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneReactionChamberRecipe>> COBBLESTONE_REACTION_CHAMBER =
        RECIPE_SERIALIZERS.register(
            "cobblestone_reaction_chamber",
            () -> new RecipeSerializer<CobblestoneReactionChamberRecipe>() {
                @Override
                public MapCodec<CobblestoneReactionChamberRecipe> codec() {
                    return CobblestoneReactionChamberRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneReactionChamberRecipe> streamCodec() {
                    return CobblestoneReactionChamberRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneCrystallizationChamberRecipe>> COBBLESTONE_CRYSTALLIZATION_CHAMBER =
        RECIPE_SERIALIZERS.register(
            "cobblestone_crystallization_chamber",
            () -> new RecipeSerializer<CobblestoneCrystallizationChamberRecipe>() {
                @Override
                public MapCodec<CobblestoneCrystallizationChamberRecipe> codec() {
                    return CobblestoneCrystallizationChamberRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneCrystallizationChamberRecipe> streamCodec() {
                    return CobblestoneCrystallizationChamberRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneDissolutionChamberRecipe>> COBBLESTONE_DISSOLUTION_CHAMBER =
        RECIPE_SERIALIZERS.register(
            "cobblestone_dissolution_chamber",
            () -> new RecipeSerializer<CobblestoneDissolutionChamberRecipe>() {
                @Override
                public MapCodec<CobblestoneDissolutionChamberRecipe> codec() {
                    return CobblestoneDissolutionChamberRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneDissolutionChamberRecipe> streamCodec() {
                    return CobblestoneDissolutionChamberRecipe.STREAM_CODEC;
                }
            }
        );

    public static final Supplier<RecipeSerializer<CobblestoneFluidMixerRecipe>> COBBLESTONE_FLUID_MIXER =
        RECIPE_SERIALIZERS.register(
            "cobblestone_fluid_mixer",
            () -> new RecipeSerializer<CobblestoneFluidMixerRecipe>() {
                @Override
                public MapCodec<CobblestoneFluidMixerRecipe> codec() {
                    return CobblestoneFluidMixerRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, CobblestoneFluidMixerRecipe> streamCodec() {
                    return CobblestoneFluidMixerRecipe.STREAM_CODEC;
                }
            }
        );
}
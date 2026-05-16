package com.yukke9265.cobblestone_xx_compressed.registry;

import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipe;

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
}
package com.yukke9265.cobblestone_xx_compressed.compat.ae2;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

/**
 * AE2 刻印機のうち「上下スロット無しの INSCRIBE（粉砕相当）」を
 * 丸石クラッシャー用レシピへ変換します。
 * AE2 未導入でも起動できるよう、クラス参照はすべて反射経由です。
 */
public final class Ae2InscriberCrushCompat {
    private static final String AE2_MOD_ID = "ae2";
    private static final String RECIPE_TYPE_CLASS = "appeng.recipes.AERecipeTypes";
    private static final String INSCRIBER_RECIPE_CLASS = "appeng.recipes.handlers.InscriberRecipe";
    private static final String PROCESS_TYPE_CLASS = "appeng.recipes.handlers.InscriberProcessType";

    // 既存の手動 AE2 レシピと同じ CP です。
    private static final long CRUSHER_TOTAL_CP = 800L;
    private static final long CRUSHER_CP_PER_TICK = 4L;

    @Nullable
    private static RecipeType<?> cachedInscriberType;
    private static boolean inscriberTypeResolved;

    private Ae2InscriberCrushCompat() {
    }

    public static boolean isAvailable() {
        return ModList.get().isLoaded(AE2_MOD_ID) && getInscriberRecipeType() != null;
    }

    /**
     * 入力に合う刻印機粉砕レシピがあれば、クラッシャーレシピへ変換して返します。
     */
    public static Optional<CobblestoneCrusherRecipe> findMatchingCrushRecipe(Level level, ItemStack inputStack) {
        if (inputStack.isEmpty() || !isAvailable()) {
            return Optional.empty();
        }

        for (Object rawHolder : getInscriberRecipeHolders(level.getRecipeManager())) {
            CobblestoneCrusherRecipe converted = tryConvertCrushRecipe(rawHolder);
            if (converted != null && converted.getIngredient().test(inputStack)) {
                return Optional.of(converted);
            }
        }

        return Optional.empty();
    }

    public static boolean isValidCrushInput(Level level, ItemStack stack) {
        if (stack.isEmpty() || !isAvailable()) {
            return false;
        }

        for (Object rawHolder : getInscriberRecipeHolders(level.getRecipeManager())) {
            CobblestoneCrusherRecipe converted = tryConvertCrushRecipe(rawHolder);
            if (converted != null && converted.getIngredient().test(stack)) {
                return true;
            }
        }

        return false;
    }

    /**
     * JEI 表示用。粉砕相当の刻印機レシピだけをクラッシャー形式へ並べます。
     */
    public static List<CobblestoneCrusherRecipe> collectCrushDisplayRecipes(Level level) {
        List<CobblestoneCrusherRecipe> recipes = new ArrayList<>();
        if (!isAvailable()) {
            return recipes;
        }

        for (Object rawHolder : getInscriberRecipeHolders(level.getRecipeManager())) {
            CobblestoneCrusherRecipe converted = tryConvertCrushRecipe(rawHolder);
            if (converted == null) {
                continue;
            }

            if (converted.getIngredient().isEmpty() || converted.getResult().isEmpty()) {
                continue;
            }

            recipes.add(converted);
        }

        return recipes;
    }

    @Nullable
    private static CobblestoneCrusherRecipe tryConvertCrushRecipe(Object rawHolder) {
        Object recipe = unwrapRecipe(rawHolder);
        if (recipe == null || !isInscriberRecipe(recipe)) {
            return null;
        }

        // 上下スロットが空で、mode が INSCRIBE のものだけが粉砕相当です。
        if (!isCrushLikeInscriber(recipe)) {
            return null;
        }

        Ingredient middleInput = invokeIngredient(recipe, "getMiddleInput");
        ItemStack result = invokeItemStack(recipe, "getResultItem");
        if (middleInput == null || middleInput.isEmpty() || result == null || result.isEmpty()) {
            return null;
        }

        ResourceLocation resultId = BuiltInRegistries.ITEM.getKey(result.getItem());
        return new CobblestoneCrusherRecipe(
            middleInput,
            resultId,
            Math.max(1, result.getCount()),
            CRUSHER_TOTAL_CP,
            CRUSHER_CP_PER_TICK
        );
    }

    private static boolean isCrushLikeInscriber(Object recipe) {
        Ingredient top = invokeIngredient(recipe, "getTopOptional");
        Ingredient bottom = invokeIngredient(recipe, "getBottomOptional");
        if (top == null || bottom == null) {
            return false;
        }

        // 1.21.1 では空 Ingredient、新しい AE2 では Optional.empty 相当もあり得ます。
        if (!top.isEmpty() || !bottom.isEmpty()) {
            return false;
        }

        Object processType = invoke(recipe, "getProcessType");
        if (processType == null) {
            return false;
        }

        return isInscribeMode(processType);
    }

    private static boolean isInscribeMode(Object processType) {
        try {
            Class<?> processTypeClass = Class.forName(PROCESS_TYPE_CLASS);
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object inscribeConstant = Enum.valueOf((Class) processTypeClass, "INSCRIBE");
            return processType == inscribeConstant || processType.equals(inscribeConstant);
        } catch (ReflectiveOperationException | ClassCastException | IllegalArgumentException exception) {
            // 名前比較にフォールバックします。
            return "INSCRIBE".equals(String.valueOf(processType));
        }
    }

    private static boolean isInscriberRecipe(Object recipe) {
        try {
            Class<?> inscriberClass = Class.forName(INSCRIBER_RECIPE_CLASS);
            return inscriberClass.isInstance(recipe);
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    @Nullable
    private static Object unwrapRecipe(Object rawHolder) {
        if (rawHolder instanceof RecipeHolder<?> holder) {
            return holder.value();
        }

        return rawHolder;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Iterable<?> getInscriberRecipeHolders(RecipeManager recipeManager) {
        RecipeType<?> recipeType = getInscriberRecipeType();
        if (recipeType == null) {
            return List.of();
        }

        try {
            return recipeManager.getAllRecipesFor((RecipeType) recipeType);
        } catch (RuntimeException exception) {
            return List.of();
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static RecipeType<?> getInscriberRecipeType() {
        if (inscriberTypeResolved) {
            return cachedInscriberType;
        }

        inscriberTypeResolved = true;
        if (!ModList.get().isLoaded(AE2_MOD_ID)) {
            return null;
        }

        try {
            Class<?> recipeTypesClass = Class.forName(RECIPE_TYPE_CLASS);
            Object fieldValue = recipeTypesClass.getField("INSCRIBER").get(null);
            if (fieldValue instanceof RecipeType<?> recipeType) {
                cachedInscriberType = recipeType;
                return cachedInscriberType;
            }
        } catch (ReflectiveOperationException exception) {
            cachedInscriberType = null;
        }

        return cachedInscriberType;
    }

    @Nullable
    private static Ingredient invokeIngredient(Object target, String methodName) {
        Object result = invoke(target, methodName);
        if (result instanceof Ingredient ingredient) {
            return ingredient;
        }

        // 新しめの AE2 は Optional<Ingredient> を返す場合があります。
        if (result instanceof Optional<?> optional) {
            Object value = optional.orElse(null);
            if (value instanceof Ingredient ingredient) {
                return ingredient;
            }

            return Ingredient.EMPTY;
        }

        return null;
    }

    @Nullable
    private static ItemStack invokeItemStack(Object target, String methodName) {
        Object result = invoke(target, methodName);
        if (result instanceof ItemStack stack) {
            return stack;
        }

        // 将来の ItemStackTemplate 系にも備え、createStack / toStack を試します。
        if (result != null) {
            Object asStack = invoke(result, "createStack");
            if (asStack instanceof ItemStack stack) {
                return stack;
            }

            asStack = invoke(result, "toStack");
            if (asStack instanceof ItemStack stack) {
                return stack;
            }
        }

        // getResultItem(HolderLookup) の無引数版が無い場合のフォールバックです。
        if ("getResultItem".equals(methodName)) {
            Object noArg = invoke(target, "getResultItem");
            if (noArg instanceof ItemStack stack) {
                return stack;
            }
        }

        return null;
    }

    @Nullable
    private static Object invoke(@Nullable Object target, String methodName) {
        if (target == null) {
            return null;
        }

        try {
            Method method = findNoArgMethod(target.getClass(), methodName);
            return method.invoke(target);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    private static Method findNoArgMethod(Class<?> targetClass, String methodName) throws NoSuchMethodException {
        Class<?> currentClass = targetClass;
        while (currentClass != null) {
            try {
                Method method = currentClass.getDeclaredMethod(methodName);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException exception) {
                currentClass = currentClass.getSuperclass();
            }
        }

        Method method = targetClass.getMethod(methodName);
        method.setAccessible(true);
        return method;
    }
}

package com.yukke9265.cobblestone_xx_compressed.jei;

import com.mojang.blaze3d.platform.InputConstants;

import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

// JEI API に依存する「流体からレシピ/用途を開く」処理をここへ閉じ込めます。
// 共通 Screen からは compat 層経由で呼び出し、JEI 未導入環境でも安全にします。
@SuppressWarnings("null")
public final class JeiFluidLookupRuntimeBridge {
    private static IJeiRuntime jeiRuntime;

    private JeiFluidLookupRuntimeBridge() {
    }

    public static void setRuntime(IJeiRuntime jeiRuntime) {
        JeiFluidLookupRuntimeBridge.jeiRuntime = jeiRuntime;
    }

    public static void clearRuntime() {
        jeiRuntime = null;
    }

    public static boolean handleMouseClick(FluidStack displayedFluid, int mouseButton) {
        IJeiRuntime runtime = jeiRuntime;
        if (runtime == null || displayedFluid.isEmpty()) {
            return false;
        }

        InputConstants.Key inputKey = InputConstants.Type.MOUSE.getOrCreate(mouseButton);
        if (runtime.getKeyMappings().getShowRecipe().isActiveAndMatches(inputKey)) {
            return showRecipes(runtime, displayedFluid, RecipeIngredientRole.OUTPUT);
        }

        if (runtime.getKeyMappings().getShowUses().isActiveAndMatches(inputKey)) {
            return showRecipes(runtime, displayedFluid, RecipeIngredientRole.INPUT);
        }

        return false;
    }

    public static boolean handleKeyPress(FluidStack displayedFluid, int keyCode, int scanCode) {
        IJeiRuntime runtime = jeiRuntime;
        if (runtime == null || displayedFluid.isEmpty()) {
            return false;
        }

        InputConstants.Key inputKey = InputConstants.getKey(keyCode, scanCode);
        if (runtime.getKeyMappings().getShowRecipe().isActiveAndMatches(inputKey)) {
            return showRecipes(runtime, displayedFluid, RecipeIngredientRole.OUTPUT);
        }

        if (runtime.getKeyMappings().getShowUses().isActiveAndMatches(inputKey)) {
            return showRecipes(runtime, displayedFluid, RecipeIngredientRole.INPUT);
        }

        return false;
    }

    private static boolean showRecipes(IJeiRuntime runtime, FluidStack displayedFluid, RecipeIngredientRole role) {
        FluidStack queryFluid = displayedFluid.copyWithAmount(Math.max(displayedFluid.getAmount(), FluidType.BUCKET_VOLUME));
        IFocusFactory focusFactory = runtime.getJeiHelpers().getFocusFactory();
        runtime.getRecipesGui().show(focusFactory.createFocus(role, NeoForgeTypes.FLUID_STACK, queryFluid));
        return true;
    }
}
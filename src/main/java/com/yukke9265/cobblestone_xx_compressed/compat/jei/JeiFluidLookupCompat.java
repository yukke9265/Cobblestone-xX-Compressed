package com.yukke9265.cobblestone_xx_compressed.compat.jei;

import java.lang.reflect.Method;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;

// JEI は任意導入なので、共通 Screen から JEI API を直接参照しないための互換レイヤーです。
// 実体の処理は jei パッケージ内へ隔離し、ここではリフレクションで安全に呼び出します。
public final class JeiFluidLookupCompat {
    private static final String JEI_MOD_ID = "jei";
    private static final String BRIDGE_CLASS_NAME = "com.yukke9265.cobblestone_xx_compressed.jei.JeiFluidLookupRuntimeBridge";

    private static Method handleMouseClickMethod;
    private static Method handleKeyPressMethod;
    private static boolean bridgeLookupAttempted;

    private JeiFluidLookupCompat() {
    }

    public static boolean handleKeyPress(FluidStack displayedFluid, int keyCode, int scanCode) {
        if (displayedFluid.isEmpty() || !ModList.get().isLoaded(JEI_MOD_ID)) {
            return false;
        }

        Method method = getHandleKeyPressMethod();
        if (method == null) {
            return false;
        }

        return invokeBooleanMethod(method, displayedFluid, keyCode, scanCode);
    }

    private static Method getHandleKeyPressMethod() {
        ensureBridgeMethodsResolved();
        return handleKeyPressMethod;
    }

    private static void ensureBridgeMethodsResolved() {
        if (bridgeLookupAttempted) {
            return;
        }

        bridgeLookupAttempted = true;

        try {
            Class<?> bridgeClass = Class.forName(BRIDGE_CLASS_NAME);
            handleKeyPressMethod = bridgeClass.getMethod("handleKeyPress", FluidStack.class, int.class, int.class);
        } catch (ReflectiveOperationException | LinkageError exception) {
            handleKeyPressMethod = null;
        }
    }

    private static boolean invokeBooleanMethod(Method method, Object... args) {
        try {
            Object result = method.invoke(null, args);
            return result instanceof Boolean booleanResult && booleanResult;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }
}
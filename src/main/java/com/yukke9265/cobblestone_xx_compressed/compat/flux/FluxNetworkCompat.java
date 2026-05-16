package com.yukke9265.cobblestone_xx_compressed.compat.flux;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class FluxNetworkCompat {
    private static final String MOD_ID = "fluxnetworks";
    private static final String CAPABILITIES_CLASS_NAME = "sonar.fluxnetworks.api.FluxCapabilities";
    private static final String STORAGE_INTERFACE_CLASS_NAME = "sonar.fluxnetworks.api.energy.IFNEnergyStorage";

    @Nullable
    private static Class<?> fluxStorageInterface;

    @Nullable
    private static BlockCapability<Object, Direction> fluxBlockCapability;

    private FluxNetworkCompat() {
    }

    public static boolean isLoaded() {
        return getFluxStorageInterface() != null && getFluxBlockCapability() != null;
    }

    public static <T extends BlockEntity> void registerBlockEntity(
        RegisterCapabilitiesEvent event,
        BlockEntityType<T> blockEntityType,
        ICapabilityProvider<? super T, Direction, Object> provider
    ) {
        if (!isLoaded()) {
            return;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        BlockCapability rawCapability = getFluxBlockCapability();
        event.registerBlockEntity(rawCapability, blockEntityType, provider);
    }

    @Nullable
    public static Object getBlockEnergyStorage(@Nullable BlockEntity target, Direction side) {
        BlockCapability<Object, Direction> capability = getFluxBlockCapability();
        if (capability == null || target == null || target.isRemoved() || target.getLevel() == null) {
            return null;
        }

        return capability.getCapability(target.getLevel(), target.getBlockPos(), target.getBlockState(), target, side);
    }

    public static boolean canReceive(@Nullable Object storage) {
        Object result = invoke(storage, "canReceive");
        return result instanceof Boolean value && value;
    }

    public static long receiveEnergy(@Nullable Object storage, long amount, boolean simulate) {
        Object result = invoke(storage, "receiveEnergyL", amount, simulate);
        if (result instanceof Number number) {
            return number.longValue();
        }

        return 0L;
    }

    @Nullable
    public static Object createLongEnergyStorage(LongEnergyStorage storage) {
        Class<?> storageInterface = getFluxStorageInterface();
        if (storageInterface == null || getFluxBlockCapability() == null) {
            return null;
        }

        InvocationHandler handler = (proxy, method, args) -> {
            String methodName = method.getName();
            if (methodName.equals("receiveEnergyL")) {
                long amount = args != null && args.length > 0 ? ((Number) args[0]).longValue() : 0L;
                boolean simulate = args != null && args.length > 1 && (Boolean) args[1];
                return storage.receiveEnergyL(amount, simulate);
            }

            if (methodName.equals("extractEnergyL")) {
                long amount = args != null && args.length > 0 ? ((Number) args[0]).longValue() : 0L;
                boolean simulate = args != null && args.length > 1 && (Boolean) args[1];
                return storage.extractEnergyL(amount, simulate);
            }

            if (methodName.equals("getEnergyStoredL")) {
                return storage.getEnergyStoredL();
            }

            if (methodName.equals("getMaxEnergyStoredL")) {
                return storage.getMaxEnergyStoredL();
            }

            if (methodName.equals("canExtract")) {
                return storage.canExtract();
            }

            if (methodName.equals("canReceive")) {
                return storage.canReceive();
            }

            if (methodName.equals("toString")) {
                return "CobblestoneFEGeneratorFluxStorageProxy";
            }

            if (methodName.equals("hashCode")) {
                return System.identityHashCode(proxy);
            }

            if (methodName.equals("equals")) {
                return proxy == (args == null || args.length == 0 ? null : args[0]);
            }

            throw new UnsupportedOperationException("未対応の Flux energy method です: " + methodName);
        };

        return Proxy.newProxyInstance(
            storageInterface.getClassLoader(),
            new Class<?>[] { storageInterface },
            handler
        );
    }

    @Nullable
    private static Class<?> getFluxStorageInterface() {
        if (fluxStorageInterface == null) {
            fluxStorageInterface = findFluxStorageInterface();
        }

        return fluxStorageInterface;
    }

    @Nullable
    private static BlockCapability<Object, Direction> getFluxBlockCapability() {
        if (fluxBlockCapability == null) {
            fluxBlockCapability = findFluxBlockCapability();
        }

        return fluxBlockCapability;
    }

    @Nullable
    private static Class<?> findFluxStorageInterface() {
        if (!ModList.get().isLoaded(MOD_ID)) {
            return null;
        }

        try {
            return Class.forName(STORAGE_INTERFACE_CLASS_NAME);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static BlockCapability<Object, Direction> findFluxBlockCapability() {
        if (!ModList.get().isLoaded(MOD_ID)) {
            return null;
        }

        try {
            Class<?> capabilitiesClass = Class.forName(CAPABILITIES_CLASS_NAME);
            Field blockField = capabilitiesClass.getField("BLOCK");
            Object capability = blockField.get(null);
            if (capability instanceof BlockCapability<?, ?> blockCapability) {
                return (BlockCapability<Object, Direction>) blockCapability;
            }
        } catch (ReflectiveOperationException exception) {
            return null;
        }

        return null;
    }

    @Nullable
    private static Object invoke(@Nullable Object target, String methodName, Object... args) {
        if (target == null) {
            return null;
        }

        try {
            Method method = args.length == 0
                ? findMethod(target.getClass(), methodName)
                : findMethod(target.getClass(), methodName, long.class, boolean.class);
            return method.invoke(target, args);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    private static Method findMethod(Class<?> targetClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> currentClass = targetClass;
        while (currentClass != null) {
            try {
                Method method = currentClass.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException exception) {
                currentClass = currentClass.getSuperclass();
            }
        }

        Method method = targetClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    public interface LongEnergyStorage {
        long receiveEnergyL(long amount, boolean simulate);

        long extractEnergyL(long amount, boolean simulate);

        long getEnergyStoredL();

        long getMaxEnergyStoredL();

        boolean canExtract();

        boolean canReceive();
    }
}
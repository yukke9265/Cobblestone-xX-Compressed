package com.yukke9265.cobblestone_xx_compressed.compat.accessory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * 装飾品スロット mod が入っているときだけ、その装備欄を調べます。
 * 未導入でも起動できるよう、クラス参照は反射経由です。
 */
public final class AccessoryInventoryCompat {
    private static final String CURIOS_MOD_ID = "curios";
    private static final String ACCESSORIES_MOD_ID = "accessories";
    private static final String CURIOS_API_CLASS = "top.theillusivec4.curios.api.CuriosApi";
    private static final String ACCESSORIES_CAPABILITY_CLASS =
        "io.wispforest.accessories.api.AccessoriesCapability";

    @Nullable
    private static Method cachedCuriosInventoryMethod;
    @Nullable
    private static Method cachedCuriosFindMethod;
    @Nullable
    private static Method cachedAccessoriesGetMethod;
    @Nullable
    private static Method cachedAccessoriesEquippedMethod;
    private static boolean curiosResolved;
    private static boolean accessoriesResolved;

    private AccessoryInventoryCompat() {
    }

    public static boolean hasItem(LivingEntity entity, Item item) {
        return hasInCurios(entity, item) || hasInAccessories(entity, item);
    }

    private static boolean hasInCurios(LivingEntity entity, Item item) {
        if (!ModList.get().isLoaded(CURIOS_MOD_ID)) {
            return false;
        }

        Object handler = getCuriosHandler(entity);
        if (handler == null) {
            return false;
        }

        Method findMethod = getCuriosFindMethod(handler);
        if (findMethod == null) {
            return false;
        }

        try {
            return isPresentResult(invokeItemQuery(findMethod, handler, item));
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    private static boolean hasInAccessories(LivingEntity entity, Item item) {
        if (!ModList.get().isLoaded(ACCESSORIES_MOD_ID)) {
            return false;
        }

        Object capability = getAccessoriesCapability(entity);
        if (capability == null) {
            return false;
        }

        Method equippedMethod = getAccessoriesEquippedMethod(capability);
        if (equippedMethod == null) {
            return false;
        }

        try {
            return isPresentResult(invokeItemQuery(equippedMethod, capability, item));
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    @Nullable
    private static Object getCuriosHandler(LivingEntity entity) {
        Method inventoryMethod = getCuriosInventoryMethod();
        if (inventoryMethod == null) {
            return null;
        }

        try {
            return unwrapOptional(inventoryMethod.invoke(null, entity));
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    @Nullable
    private static Object getAccessoriesCapability(LivingEntity entity) {
        Method getMethod = getAccessoriesGetMethod();
        if (getMethod == null) {
            return null;
        }

        try {
            return unwrapOptional(getMethod.invoke(null, entity));
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    @Nullable
    private static Method getCuriosInventoryMethod() {
        if (curiosResolved) {
            return cachedCuriosInventoryMethod;
        }

        curiosResolved = true;
        try {
            Class<?> apiClass = Class.forName(CURIOS_API_CLASS);
            cachedCuriosInventoryMethod = apiClass.getMethod("getCuriosInventory", LivingEntity.class);
        } catch (ReflectiveOperationException exception) {
            cachedCuriosInventoryMethod = null;
        }

        return cachedCuriosInventoryMethod;
    }

    @Nullable
    private static Method getCuriosFindMethod(Object handler) {
        if (cachedCuriosFindMethod != null) {
            return cachedCuriosFindMethod;
        }

        Class<?> handlerClass = handler.getClass();
        cachedCuriosFindMethod = findItemOrPredicateMethod(handlerClass, "findFirstCurio");
        if (cachedCuriosFindMethod == null) {
            cachedCuriosFindMethod = findItemOrPredicateMethod(handlerClass, "isEquipped");
        }

        return cachedCuriosFindMethod;
    }

    @Nullable
    private static Method getAccessoriesGetMethod() {
        if (accessoriesResolved) {
            return cachedAccessoriesGetMethod;
        }

        accessoriesResolved = true;
        try {
            Class<?> capabilityClass = Class.forName(ACCESSORIES_CAPABILITY_CLASS);
            cachedAccessoriesGetMethod = findMethod(capabilityClass, "getOptionally", LivingEntity.class);
            if (cachedAccessoriesGetMethod == null) {
                cachedAccessoriesGetMethod = findMethod(capabilityClass, "get", LivingEntity.class);
            }
        } catch (ReflectiveOperationException exception) {
            cachedAccessoriesGetMethod = null;
        }

        return cachedAccessoriesGetMethod;
    }

    @Nullable
    private static Method getAccessoriesEquippedMethod(Object capability) {
        if (cachedAccessoriesEquippedMethod != null) {
            return cachedAccessoriesEquippedMethod;
        }

        Class<?> capabilityClass = capability.getClass();
        cachedAccessoriesEquippedMethod = findItemOrPredicateMethod(capabilityClass, "isEquipped");
        if (cachedAccessoriesEquippedMethod == null) {
            cachedAccessoriesEquippedMethod = findItemOrPredicateMethod(capabilityClass, "getEquipped");
        }

        return cachedAccessoriesEquippedMethod;
    }

    @Nullable
    private static Method findItemOrPredicateMethod(Class<?> type, String name) {
        Method itemMethod = findMethod(type, name, Item.class);
        if (itemMethod != null) {
            return itemMethod;
        }

        return findMethod(type, name, Predicate.class);
    }

    @Nullable
    private static Method findMethod(Class<?> type, String name, Class<?> parameterType) {
        try {
            return type.getMethod(name, parameterType);
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }

    private static Object invokeItemQuery(Method method, Object target, Item item) throws ReflectiveOperationException {
        Class<?> parameterType = method.getParameterTypes()[0];
        if (parameterType.isAssignableFrom(Item.class)) {
            return method.invoke(target, item);
        }

        return method.invoke(target, (Predicate<ItemStack>) stack -> stack.is(item));
    }

    @Nullable
    private static Object unwrapOptional(Object value) {
        if (value instanceof Optional<?> optional) {
            return optional.orElse(null);
        }

        return value;
    }

    private static boolean isPresentResult(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Optional<?> optional) {
            return optional.isPresent();
        }
        if (value instanceof Collection<?> collection) {
            return containsItem(collection);
        }
        if (value instanceof ItemStack stack) {
            return !stack.isEmpty();
        }

        return true;
    }

    private static boolean containsItem(Collection<?> collection) {
        for (Object element : collection) {
            if (element instanceof ItemStack stack && !stack.isEmpty()) {
                return true;
            }
            if (element instanceof Optional<?> optional && optional.isPresent()) {
                return true;
            }
        }

        return !collection.isEmpty();
    }
}

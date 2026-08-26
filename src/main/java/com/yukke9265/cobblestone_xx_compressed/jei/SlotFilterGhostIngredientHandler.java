package com.yukke9265.cobblestone_xx_compressed.jei;

import java.util.ArrayList;
import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTarget;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTargetType;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.SlotFilter;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.SlotFilterEntry;
import com.yukke9265.cobblestone_xx_compressed.menu.BaseMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.SlotFilterMenuSupport;
import com.yukke9265.cobblestone_xx_compressed.network.SetFilterGhostPayload;
import com.yukke9265.cobblestone_xx_compressed.screen.BaseScreen;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

/*
 * 方針:
 * JEI のアイテム／流体をフィルタ ghost スロットへドロップできるようにします。
 * 選択中ターゲットの種類（item/fluid）に合うものだけ受け付け、サーバへ同期します。
 */
public class SlotFilterGhostIngredientHandler<T extends BaseScreen<?>> implements IGhostIngredientHandler<T> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(T gui, ITypedIngredient<I> ingredient, boolean doStart) {
        if (!(gui.getMenu() instanceof BaseMenu menu)) {
            return List.of();
        }
        if (!menu.hasSlotFilterUi() || !menu.isSlotFilterPanelOpen()) {
            return List.of();
        }

        SlotFilterMenuSupport support = menu.getSlotFilterSupport();
        if (support == null) {
            return List.of();
        }

        FilterTarget selected = support.getSelectedFilterTarget();
        if (selected == null) {
            return List.of();
        }

        ResolvedIngredient resolved = this.resolveIngredient(ingredient, selected.type());
        if (resolved == null) {
            return List.of();
        }

        List<Target<I>> targets = new ArrayList<>();
        int startIndex = support.getGhostSlotStartIndex();
        for (int ghostIndex = 0; ghostIndex < SlotFilter.MAX_ENTRIES; ghostIndex++) {
            Slot slot = menu.getSlot(startIndex + ghostIndex);
            Rect2i area = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
            int payloadGhostIndex = ghostIndex;
            ItemStack payloadItem = resolved.itemStack();
            String payloadFluidId = resolved.fluidId();
            targets.add(new Target<>() {
                @Override
                public Rect2i getArea() {
                    return area;
                }

                @Override
                public void accept(I ignored) {
                    PacketDistributor.sendToServer(new SetFilterGhostPayload(
                        menu.containerId,
                        payloadGhostIndex,
                        payloadItem,
                        payloadFluidId
                    ));
                }
            });
        }
        return targets;
    }

    @Override
    public void onComplete() {
    }

    private ResolvedIngredient resolveIngredient(ITypedIngredient<?> ingredient, FilterTargetType expectedType) {
        Object value = ingredient.getIngredient();

        if (expectedType == FilterTargetType.ITEM) {
            if (ingredient.getType() == VanillaTypes.ITEM_STACK && value instanceof ItemStack stack && !stack.isEmpty()) {
                return new ResolvedIngredient(stack.copyWithCount(1), "");
            }
            return null;
        }

        if (expectedType != FilterTargetType.FLUID) {
            return null;
        }

        if (ingredient.getType() == NeoForgeTypes.FLUID_STACK && value instanceof FluidStack fluidStack && !fluidStack.isEmpty()) {
            Item bucketItem = fluidStack.getFluid().getBucket();
            if (bucketItem != null && bucketItem != Items.AIR) {
                SlotFilterEntry entry = SlotFilterEntry.ofFluid(fluidStack);
                if (entry == null) {
                    return null;
                }
                return new ResolvedIngredient(entry.createDisplayStack(), "");
            }
            return new ResolvedIngredient(
                ItemStack.EMPTY,
                BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString()
            );
        }

        if (ingredient.getType() == VanillaTypes.ITEM_STACK && value instanceof ItemStack stack) {
            SlotFilterEntry entry = SlotFilterEntry.fromCarried(stack, FilterTargetType.FLUID);
            if (entry == null) {
                return null;
            }
            return new ResolvedIngredient(entry.createDisplayStack(), "");
        }

        return null;
    }

    private record ResolvedIngredient(ItemStack itemStack, String fluidId) {
    }
}

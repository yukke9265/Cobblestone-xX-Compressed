package com.yukke9265.cobblestone_xx_compressed.jei.category;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.network.chat.Component;

@SuppressWarnings("null")
public final class JeiCobblestonePowerItems {
    public static final int SLOT_SIZE = 18;

    private static final String TOOLTIP_FUEL_KEY = "jei.cobblestonexxcompressed.cp_supply.fuel";
    private static final String TOOLTIP_CATALYST_KEY = "jei.cobblestonexxcompressed.cp_supply.catalyst";

    private JeiCobblestonePowerItems() {
    }

    public static void addTooltipIfHovered(ITooltipBuilder tooltip, double mouseX, double mouseY, int slotX, int slotY) {
        if (!isMouseOverSlot(mouseX, mouseY, slotX, slotY)) {
            return;
        }

        addCpSupplyTooltip(tooltip);
    }

    public static void addCpSupplyTooltip(ITooltipBuilder tooltip) {
        tooltip.add(Component.translatable(TOOLTIP_FUEL_KEY));
        tooltip.add(Component.translatable(TOOLTIP_CATALYST_KEY));
    }

    private static boolean isMouseOverSlot(double mouseX, double mouseY, int slotX, int slotY) {
        return mouseX >= slotX
            && mouseX < slotX + SLOT_SIZE
            && mouseY >= slotY
            && mouseY < slotY + SLOT_SIZE;
    }
}

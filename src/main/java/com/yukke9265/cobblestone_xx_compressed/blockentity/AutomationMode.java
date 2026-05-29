package com.yukke9265.cobblestone_xx_compressed.blockentity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum AutomationMode {
    DISABLED(0, "disabled", ChatFormatting.GRAY),
    INPUT(1, "input", ChatFormatting.GREEN),
    OUTPUT(2, "output", ChatFormatting.GOLD),
    COBBLESTONE_INPUT(3, "cobblestone_input", ChatFormatting.AQUA),
    INPUT_1(4, "input1", ChatFormatting.DARK_GREEN),
    INPUT_2(5, "input2", ChatFormatting.BLUE),
    OUTPUT_1(6, "output1", ChatFormatting.YELLOW),
    OUTPUT_2(7, "output2", ChatFormatting.LIGHT_PURPLE),
    IN_OUT(8, "in_out", ChatFormatting.RED);

    private final int id;
    private final String translationKeySuffix;
    private final ChatFormatting chatFormatting;

    AutomationMode(int id, String translationKeySuffix, ChatFormatting chatFormatting) {
        this.id = id;
        this.translationKeySuffix = translationKeySuffix;
        this.chatFormatting = chatFormatting;
    }

    public int getId() {
        return this.id;
    }

    public Component createLabelComponent() {
        return Component.translatable("automation_mode.cobblestonexxcompressed." + this.translationKeySuffix)
            .withStyle(this.chatFormatting);
    }

    public boolean isOutputMode() {
        return this == OUTPUT || this == OUTPUT_1 || this == OUTPUT_2 || this == IN_OUT;
    }

    public AutomationMode next() {
        return switch (this) {
            case DISABLED -> INPUT;
            case INPUT -> OUTPUT;
            case OUTPUT -> DISABLED;
            case IN_OUT -> DISABLED;
            case COBBLESTONE_INPUT -> DISABLED;
            case INPUT_1 -> DISABLED;
            case INPUT_2 -> DISABLED;
            case OUTPUT_1 -> DISABLED;
            case OUTPUT_2 -> DISABLED;
        };
    }

    public AutomationMode previous() {
        return switch (this) {
            case DISABLED -> OUTPUT;
            case INPUT -> DISABLED;
            case OUTPUT -> INPUT;
            case IN_OUT -> DISABLED;
            case COBBLESTONE_INPUT -> DISABLED;
            case INPUT_1 -> DISABLED;
            case INPUT_2 -> DISABLED;
            case OUTPUT_1 -> DISABLED;
            case OUTPUT_2 -> DISABLED;
        };
    }

    public static AutomationMode fromId(int id) {
        for (AutomationMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }

        return DISABLED;
    }
}
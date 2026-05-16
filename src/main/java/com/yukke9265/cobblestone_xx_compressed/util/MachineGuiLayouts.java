package com.yukke9265.cobblestone_xx_compressed.util;

public final class MachineGuiLayouts {
    public static final int STANDARD_IMAGE_WIDTH = 176;
    public static final int STANDARD_IMAGE_HEIGHT = 166;
    public static final int STANDARD_TITLE_LABEL_X = 8;
    public static final int STANDARD_TITLE_LABEL_Y = 6;
    public static final int STANDARD_INVENTORY_LABEL_X = 8;
    public static final int STANDARD_INVENTORY_LABEL_Y = 72;
    public static final int SLOT_SIZE = 18;
    public static final int PLAYER_INVENTORY_START_X = 8;
    public static final int PLAYER_INVENTORY_START_Y = 84;
    public static final int HOTBAR_START_Y = 142;
    public static final int UPGRADE_SLOT_X = 176;
    public static final int ACCELERATION_SLOT_Y = 12;
    public static final int ENERGIZED_CUBE_SLOT_Y = ACCELERATION_SLOT_Y + SLOT_SIZE;

    private MachineGuiLayouts() {
    }

    public static final class PoweredMachine {
        public static final int MACHINE_SLOT_Y = 31;
        public static final int INPUT_SLOT_X = 56;
        public static final int OUTPUT_SLOT_X = INPUT_SLOT_X + SLOT_SIZE + 35;
        public static final int POWER_SLOT_X = 10;
        public static final int POWER_SLOT_Y = 51;
        public static final int PROGRESS_BAR_X = 80;
        public static final int PROGRESS_BAR_Y = 31;
        public static final int PROGRESS_BAR_WIDTH = 16;
        public static final int PROGRESS_BAR_HEIGHT = 16;
        public static final int POWER_BAR_X = 36;
        public static final int POWER_BAR_Y = 63;
        public static final int POWER_BAR_WIDTH = 124;
        public static final int POWER_BAR_HEIGHT = 8;

        private PoweredMachine() {
        }
    }

    public static final class Mixer {
        public static final int MACHINE_SLOT_Y = 31;
        public static final int POWER_SLOT_X = 10;
        public static final int POWER_SLOT_Y = 51;
        public static final int INPUT_SLOT_1_X = 36;
        public static final int INPUT_SLOT_1_Y = MACHINE_SLOT_Y;
        public static final int INPUT_SLOT_2_X = INPUT_SLOT_1_X + SLOT_SIZE + 2;
        public static final int INPUT_SLOT_2_Y = MACHINE_SLOT_Y;
        public static final int OUTPUT_SLOT_X = INPUT_SLOT_2_X + SLOT_SIZE + 35;
        public static final int OUTPUT_SLOT_Y = MACHINE_SLOT_Y;
        public static final int PROGRESS_BAR_X = 80;
        public static final int PROGRESS_BAR_Y = 31;
        public static final int PROGRESS_BAR_WIDTH = 16;
        public static final int PROGRESS_BAR_HEIGHT = 16;
        public static final int POWER_BAR_X = 36;
        public static final int POWER_BAR_Y = 63;
        public static final int POWER_BAR_WIDTH = 124;
        public static final int POWER_BAR_HEIGHT = 8;

        private Mixer() {
        }
    }

    public static final class ReactionChamber {
        public static final int MACHINE_SLOT_Y = 31;
        public static final int FLUID_SLOT_X = 14;
        public static final int FLUID_SLOT_Y = MACHINE_SLOT_Y;
        public static final int INPUT_SLOT_1_X = 36;
        public static final int INPUT_SLOT_1_Y = MACHINE_SLOT_Y;
        public static final int INPUT_SLOT_2_X = INPUT_SLOT_1_X + SLOT_SIZE + 2;
        public static final int INPUT_SLOT_2_Y = MACHINE_SLOT_Y;
        public static final int OUTPUT_SLOT_X = INPUT_SLOT_2_X + SLOT_SIZE + 40;
        public static final int OUTPUT_SLOT_Y = MACHINE_SLOT_Y;
        public static final int POWER_SLOT_X = 10;
        public static final int POWER_SLOT_Y = 51;
        public static final int PROGRESS_BAR_X = 80;
        public static final int PROGRESS_BAR_Y = 31;
        public static final int PROGRESS_BAR_WIDTH = 16;
        public static final int PROGRESS_BAR_HEIGHT = 16;
        public static final int POWER_BAR_X = 36;
        public static final int POWER_BAR_Y = 63;
        public static final int POWER_BAR_WIDTH = 124;
        public static final int POWER_BAR_HEIGHT = 8;
        public static final int FLUID_FILL_X = FLUID_SLOT_X + 1;
        public static final int FLUID_FILL_Y = FLUID_SLOT_Y + 1;
        public static final int FLUID_FILL_WIDTH = 16;
        public static final int FLUID_FILL_HEIGHT = 16;

        private ReactionChamber() {
        }
    }

    public static final class Centrifuge {
        public static final int MACHINE_SLOT_Y = 31;
        public static final int POWER_SLOT_X = 10;
        public static final int POWER_SLOT_Y = 51;
        public static final int INPUT_SLOT_X = 56;
        public static final int OUTPUT_SLOT_1_X = INPUT_SLOT_X + SLOT_SIZE + 30;
        public static final int OUTPUT_SLOT_2_X = OUTPUT_SLOT_1_X + SLOT_SIZE;
        public static final int PROGRESS_BAR_X = 80;
        public static final int PROGRESS_BAR_Y = 31;
        public static final int PROGRESS_BAR_WIDTH = 16;
        public static final int PROGRESS_BAR_HEIGHT = 16;
        public static final int POWER_BAR_X = 36;
        public static final int POWER_BAR_Y = 63;
        public static final int POWER_BAR_WIDTH = 124;
        public static final int POWER_BAR_HEIGHT = 8;

        private Centrifuge() {
        }
    }

    public static final class LaserDrill {
        public static final int MACHINE_SLOT_Y = 31;
        public static final int POWER_SLOT_X = 10;
        public static final int POWER_SLOT_Y = 51;
        public static final int INPUT_SLOT_X = 56;
        public static final int OUTPUT_SLOT_1_X = INPUT_SLOT_X + SLOT_SIZE + 30;
        public static final int OUTPUT_SLOT_2_X = OUTPUT_SLOT_1_X + SLOT_SIZE;
        public static final int PROGRESS_BAR_X = 80;
        public static final int PROGRESS_BAR_Y = 31;
        public static final int PROGRESS_BAR_WIDTH = 16;
        public static final int PROGRESS_BAR_HEIGHT = 16;
        public static final int POWER_BAR_X = 36;
        public static final int POWER_BAR_Y = 63;
        public static final int POWER_BAR_WIDTH = 124;
        public static final int POWER_BAR_HEIGHT = 8;

        private LaserDrill() {
        }
    }
}
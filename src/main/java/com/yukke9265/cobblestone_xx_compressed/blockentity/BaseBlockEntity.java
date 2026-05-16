package com.yukke9265.cobblestone_xx_compressed.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class BaseBlockEntity extends BlockEntity {
    public static final int AUTOMATION_FACE_COUNT = AutomationSide.values().length;

    private final AutomationMode[] automationModes = new AutomationMode[] {
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.INPUT,
        AutomationMode.INPUT,
        AutomationMode.INPUT,
        AutomationMode.INPUT
    };
    private boolean autoExportEnabled;

    public BaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public boolean isAutoExportEnabled() {
        return this.autoExportEnabled;
    }

    public int getAutoExportEnabledId() {
        return this.autoExportEnabled ? 1 : 0;
    }

    public void setAutoExportEnabled(boolean autoExportEnabled) {
        this.autoExportEnabled = autoExportEnabled;
        this.setChanged();
    }

    public void toggleAutoExportEnabled() {
        this.setAutoExportEnabled(!this.autoExportEnabled);
    }

    public AutomationMode getAutomationMode(int index) {
        if (index < 0 || index >= AUTOMATION_FACE_COUNT) {
            throw new IllegalArgumentException("不明な自動搬出入設定 index です: " + index);
        }

        return this.automationModes[index];
    }

    public int getAutomationModeId(int index) {
        return this.getAutomationMode(index).getId();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        return this.getAutomationMode(automationSide.getIndex());
    }

    public int getAutomationModeId(AutomationSide automationSide) {
        return this.getAutomationMode(automationSide).getId();
    }

    public void cycleAutomationMode(int index) {
        AutomationMode nextMode = this.getAutomationMode(index).next();
        this.setAutomationMode(index, nextMode);
    }

    public void cycleAutomationMode(int index, AutomationMode[] allowedModes) {
        if (allowedModes == null || allowedModes.length == 0) {
            throw new IllegalArgumentException("allowedModes が空です");
        }

        AutomationMode currentMode = this.getAutomationMode(index);
        int currentAllowedIndex = 0;

        for (int modeIndex = 0; modeIndex < allowedModes.length; modeIndex++) {
            if (allowedModes[modeIndex] == currentMode) {
                currentAllowedIndex = modeIndex;
                break;
            }
        }

        int nextAllowedIndex = (currentAllowedIndex + 1) % allowedModes.length;
        this.setAutomationMode(index, allowedModes[nextAllowedIndex]);
    }

    public void setAutomationMode(int index, AutomationMode mode) {
        if (index < 0 || index >= AUTOMATION_FACE_COUNT) {
            throw new IllegalArgumentException("不明な自動搬出入設定 index です: " + index);
        }

        this.automationModes[index] = mode;

        this.setChanged();
    }

    protected void saveAutomationModes(CompoundTag tag) {
        for (AutomationSide automationSide : AutomationSide.values()) {
            String key = automationSide.getSerializedName() + "AutomationMode";
            tag.putInt(key, this.getAutomationModeId(automationSide));
        }

        tag.putBoolean("autoExportEnabled", this.autoExportEnabled);
    }

    protected void loadAutomationModes(CompoundTag tag) {
        for (AutomationSide automationSide : AutomationSide.values()) {
            String key = automationSide.getSerializedName() + "AutomationMode";
            this.setAutomationMode(automationSide.getIndex(), AutomationMode.fromId(tag.getInt(key)));
        }

        this.setAutoExportEnabled(tag.getBoolean("autoExportEnabled"));
    }

    protected ItemStack pushItemStackToConfiguredSides(ItemStack stack, AutomationMode... targetModes) {
        if (!this.autoExportEnabled || stack.isEmpty()) {
            return stack;
        }

        Level currentLevel = this.level;
        if (currentLevel == null) {
            return stack;
        }

        BlockState currentState = this.getBlockState();
        ItemStack remainingStack = stack;

        for (Direction direction : Direction.values()) {
            if (remainingStack.isEmpty()) {
                break;
            }

            AutomationSide automationSide = AutomationSide.fromWorldSide(direction, currentState);
            AutomationMode currentMode = this.getAutomationMode(automationSide);
            if (!this.matchesAnyAutomationMode(currentMode, targetModes)) {
                continue;
            }

            IItemHandler targetHandler = currentLevel.getCapability(
                Capabilities.ItemHandler.BLOCK,
                this.worldPosition.relative(direction),
                direction.getOpposite()
            );
            if (targetHandler == null) {
                continue;
            }

            remainingStack = ItemHandlerHelper.insertItem(targetHandler, remainingStack, false);
        }

        return remainingStack;
    }

    private boolean matchesAnyAutomationMode(AutomationMode currentMode, AutomationMode[] targetModes) {
        for (AutomationMode targetMode : targetModes) {
            if (currentMode == targetMode) {
                return true;
            }
        }

        return false;
    }

    public void tick() {
        // 共通の基底BlockEntityです。
        // デフォルトでは毎ティック何もしません。
    }
}
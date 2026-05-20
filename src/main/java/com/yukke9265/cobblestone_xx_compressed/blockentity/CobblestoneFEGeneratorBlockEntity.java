package com.yukke9265.cobblestone_xx_compressed.blockentity;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.compat.flux.FluxNetworkCompat;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFEGeneratorMenu;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

public class CobblestoneFEGeneratorBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int COBBLESTONE_SLOT_INDEX = 0;

    // 既存の energized cube 最大倍率を固定上限として先に織り込んだ値です。
    // GUI に upgrade slot を持たせなくても、内部 CP と FE を long 前提で扱えるようにしておきます。
    public static final long MAX_COBBLESTONE_POWER = 274_877_906_944L;
    public static final long MAX_FORGE_ENERGY = 274_877_906_944L;

    private static final int DATA_INDEX_STORED_POWER = 0;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 1;
    private static final int DATA_INDEX_MAX_STORED_POWER = 2;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_STORED_ENERGY = 4;
    private static final int DATA_INDEX_STORED_ENERGY_UPPER = 5;
    private static final int DATA_INDEX_MAX_STORED_ENERGY = 6;
    private static final int DATA_INDEX_MAX_STORED_ENERGY_UPPER = 7;
    private static final int DATA_INDEX_CONVERTED_ENERGY = 8;
    private static final int DATA_INDEX_CONVERTED_ENERGY_UPPER = 9;
    private static final int DATA_INDEX_EXPORTED_ENERGY = 10;
    private static final int DATA_INDEX_EXPORTED_ENERGY_UPPER = 11;
    private static final int DATA_INDEX_AUTOMATION_START = 12;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_AUTOMATION_START + AUTOMATION_FACE_COUNT;

    private long storedCobblestonePower;
    private long storedForgeEnergy;
    private long lastConvertedForgeEnergy;
    private long lastExportedForgeEnergy;
    private boolean isAvailable = true;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot != COBBLESTONE_SLOT_INDEX) {
                return false;
            }

            return CobblestonePoweredFurnaceBlockEntity.isCobblestonePowerItem(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneFEGeneratorBlockEntity.this.setChanged();
        }
    };

    private final IItemHandler inputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneFEGeneratorBlockEntity.this.itemStackHandler.getStackInSlot(COBBLESTONE_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneFEGeneratorBlockEntity.this.itemStackHandler.insertItem(COBBLESTONE_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneFEGeneratorBlockEntity.this.itemStackHandler.getSlotLimit(COBBLESTONE_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot != 0) {
                return false;
            }

            return CobblestoneFEGeneratorBlockEntity.this.itemStackHandler.isItemValid(COBBLESTONE_SLOT_INDEX, stack);
        }
    };

    private final IEnergyStorage[] energyStorages = new IEnergyStorage[Direction.values().length + 1];
    private final Object[] fluxEnergyStorages = new Object[Direction.values().length + 1];

    public CobblestoneFEGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_FE_GENERATOR_BLOCK_ENTITY.get(), pos, state);
    }

    public long getStoredCobblestonePower() {
        return this.storedCobblestonePower;
    }

    public long getMaxCobblestonePower() {
        return MAX_COBBLESTONE_POWER;
    }

    public long getStoredForgeEnergy() {
        return this.storedForgeEnergy;
    }

    public long getMaxForgeEnergy() {
        return MAX_FORGE_ENERGY;
    }

    public long getLastConvertedForgeEnergy() {
        return this.lastConvertedForgeEnergy;
    }

    public long getLastExportedForgeEnergy() {
        return this.lastExportedForgeEnergy;
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public IItemHandler getAutomationItemHandler(@Nullable Direction side) {
        if (side == null) {
            return this.inputAutomationHandler;
        }

        BlockState currentState = this.getBlockState();
        AutomationSide automationSide = AutomationSide.fromWorldSide(side, currentState);
        AutomationMode automationMode = this.getAutomationMode(automationSide);
        if (automationMode == AutomationMode.COBBLESTONE_INPUT) {
            return this.inputAutomationHandler;
        }

        return EmptyItemHandler.INSTANCE;
    }

    @Nullable
    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        if (!this.canExtractEnergy(side)) {
            return null;
        }

        int capabilityIndex = this.getCapabilityIndex(side);
        IEnergyStorage storage = this.energyStorages[capabilityIndex];
        if (storage == null) {
            storage = new GeneratorEnergyStorage(side);
            this.energyStorages[capabilityIndex] = storage;
        }
        return storage;
    }

    @Nullable
    public Object getFluxEnergyCapability(@Nullable Direction side) {
        if (!this.canExtractEnergy(side)) {
            return null;
        }

        if (!FluxNetworkCompat.isLoaded()) {
            return null;
        }

        int capabilityIndex = this.getCapabilityIndex(side);
        Object storage = this.fluxEnergyStorages[capabilityIndex];
        if (storage == null) {
            storage = FluxNetworkCompat.createLongEnergyStorage(new GeneratorFluxEnergyStorage(side));
            this.fluxEnergyStorages[capabilityIndex] = storage;
        }

        return storage;
    }

    public void reverseIsAvailable() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.isAvailable = !this.isAvailable;
        this.setChanged();
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        Level currentLevel = this.level;
        BlockState currentState = this.getBlockState();

        this.clampStoredCobblestonePower();
        this.clampStoredForgeEnergy();
        this.tryAbsorbCobblestonePower();
        this.lastConvertedForgeEnergy = 0L;
        this.lastExportedForgeEnergy = 0L;

        long convertedEnergy = 0L;
        if (this.isAvailable) {
            convertedEnergy = this.convertCobblestonePowerToForgeEnergy();
        }
        this.lastConvertedForgeEnergy = convertedEnergy;

        boolean shouldTurnOn = convertedEnergy > 0L;
        BlockState updatedState = currentState.setValue(OnOffBlock.ON, shouldTurnOn);
        if (updatedState != currentState) {
            currentLevel.setBlock(this.worldPosition, updatedState, 3);
        }

        this.lastExportedForgeEnergy += this.pushForgeEnergyToConfiguredSides();
    }

    private void tryAbsorbCobblestonePower() {
        ItemStack cobblestoneStack = this.itemStackHandler.getStackInSlot(COBBLESTONE_SLOT_INDEX);
        long convertedPower = CobblestonePoweredFurnaceBlockEntity.getCobblestonePowerValueForAutomation(cobblestoneStack);
        if (convertedPower <= 0L) {
            return;
        }

        if (this.storedCobblestonePower + convertedPower > this.getMaxCobblestonePower()) {
            return;
        }

        cobblestoneStack.shrink(1);
        this.storedCobblestonePower += convertedPower;
        this.setChanged();
    }

    private long convertCobblestonePowerToForgeEnergy() {
        long neededEnergy = this.getMaxForgeEnergy() - this.storedForgeEnergy;
        if (neededEnergy <= 0L || this.storedCobblestonePower <= 0L) {
            return 0L;
        }

        long toConvert = Math.min(this.storedCobblestonePower, neededEnergy);
        this.storedCobblestonePower -= toConvert;
        this.storedForgeEnergy += toConvert;
        this.setChanged();
        return toConvert;
    }

    private long pushForgeEnergyToConfiguredSides() {
        if (!this.isAutoExportEnabled() || this.storedForgeEnergy <= 0L || this.level == null) {
            return 0L;
        }

        Level currentLevel = this.level;
        BlockState currentState = this.getBlockState();
        long exportedEnergy = 0L;
        for (Direction direction : Direction.values()) {
            if (this.storedForgeEnergy <= 0L) {
                break;
            }

            AutomationSide automationSide = AutomationSide.fromWorldSide(direction, currentState);
            AutomationMode automationMode = this.getAutomationMode(automationSide);
            if (automationMode != AutomationMode.OUTPUT && automationMode != AutomationMode.IN_OUT) {
                continue;
            }

            BlockEntity targetBlockEntity = currentLevel.getBlockEntity(this.worldPosition.relative(direction));
            Object fluxStorage = FluxNetworkCompat.getBlockEnergyStorage(targetBlockEntity, direction.getOpposite());
            if (FluxNetworkCompat.canReceive(fluxStorage)) {
                long accepted = FluxNetworkCompat.receiveEnergy(fluxStorage, this.storedForgeEnergy, false);
                if (accepted > 0L) {
                    this.storedForgeEnergy -= accepted;
                    exportedEnergy += accepted;
                    this.setChanged();
                    continue;
                }
            }

            IEnergyStorage targetStorage = currentLevel.getCapability(
                Capabilities.EnergyStorage.BLOCK,
                this.worldPosition.relative(direction),
                direction.getOpposite()
            );
            if (targetStorage == null || !targetStorage.canReceive()) {
                continue;
            }

            int toSend = (int) Math.min(this.storedForgeEnergy, Integer.MAX_VALUE);
            int accepted = targetStorage.receiveEnergy(toSend, false);
            if (accepted <= 0) {
                continue;
            }

            this.storedForgeEnergy -= accepted;
            exportedEnergy += accepted;
            this.setChanged();
        }

        return exportedEnergy;
    }

    private boolean canExtractEnergy(@Nullable Direction side) {
        if (side == null) {
            return true;
        }

        BlockState currentState = this.getBlockState();
        AutomationSide automationSide = AutomationSide.fromWorldSide(side, currentState);
        AutomationMode automationMode = this.getAutomationMode(automationSide);
        return automationMode == AutomationMode.OUTPUT || automationMode == AutomationMode.IN_OUT;
    }

    private long extractForgeEnergy(long amount, boolean simulate) {
        if (amount <= 0L || this.storedForgeEnergy <= 0L) {
            return 0L;
        }

        long extractedEnergy = Math.min(this.storedForgeEnergy, amount);
        if (!simulate) {
            this.storedForgeEnergy -= extractedEnergy;
            this.lastExportedForgeEnergy += extractedEnergy;
            this.setChanged();
        }
        return extractedEnergy;
    }

    private void clampStoredCobblestonePower() {
        long maxPower = this.getMaxCobblestonePower();
        if (this.storedCobblestonePower > maxPower) {
            this.storedCobblestonePower = maxPower;
            this.setChanged();
        }
    }

    private void clampStoredForgeEnergy() {
        long maxEnergy = this.getMaxForgeEnergy();
        if (this.storedForgeEnergy > maxEnergy) {
            this.storedForgeEnergy = maxEnergy;
            this.setChanged();
        }
    }

    private int getCapabilityIndex(@Nullable Direction side) {
        return side == null ? Direction.values().length : side.get3DDataValue();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("storedCobblestonePower", this.storedCobblestonePower);
        tag.putLong("storedForgeEnergy", this.storedForgeEnergy);
        tag.putBoolean("isAvailable", this.isAvailable);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.storedForgeEnergy = tag.getLong("storedForgeEnergy");
        this.isAvailable = tag.getBoolean("isAvailable");
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_fe_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData generatorData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_STORED_POWER) {
                    return LongDataHelper.lowerInt(storedCobblestonePower);
                }

                if (index == DATA_INDEX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(storedCobblestonePower);
                }

                if (index == DATA_INDEX_MAX_STORED_POWER) {
                    return LongDataHelper.lowerInt(getMaxCobblestonePower());
                }

                if (index == DATA_INDEX_MAX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(getMaxCobblestonePower());
                }

                if (index == DATA_INDEX_STORED_ENERGY) {
                    return LongDataHelper.lowerInt(storedForgeEnergy);
                }

                if (index == DATA_INDEX_STORED_ENERGY_UPPER) {
                    return LongDataHelper.upperInt(storedForgeEnergy);
                }

                if (index == DATA_INDEX_MAX_STORED_ENERGY) {
                    return LongDataHelper.lowerInt(getMaxForgeEnergy());
                }

                if (index == DATA_INDEX_MAX_STORED_ENERGY_UPPER) {
                    return LongDataHelper.upperInt(getMaxForgeEnergy());
                }

                if (index == DATA_INDEX_CONVERTED_ENERGY) {
                    return LongDataHelper.lowerInt(lastConvertedForgeEnergy);
                }

                if (index == DATA_INDEX_CONVERTED_ENERGY_UPPER) {
                    return LongDataHelper.upperInt(lastConvertedForgeEnergy);
                }

                if (index == DATA_INDEX_EXPORTED_ENERGY) {
                    return LongDataHelper.lowerInt(lastExportedForgeEnergy);
                }

                if (index == DATA_INDEX_EXPORTED_ENERGY_UPPER) {
                    return LongDataHelper.upperInt(lastExportedForgeEnergy);
                }

                int automationIndex = index - DATA_INDEX_AUTOMATION_START;
                if (automationIndex >= 0 && automationIndex < AUTOMATION_FACE_COUNT) {
                    return getAutomationModeId(automationIndex);
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return getAutoExportEnabledId();
                }

                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == DATA_INDEX_STORED_POWER) {
                    storedCobblestonePower = LongDataHelper.toLong(value, LongDataHelper.upperInt(storedCobblestonePower));
                }

                if (index == DATA_INDEX_STORED_POWER_UPPER) {
                    storedCobblestonePower = LongDataHelper.toLong(LongDataHelper.lowerInt(storedCobblestonePower), value);
                }

                if (index == DATA_INDEX_STORED_ENERGY) {
                    storedForgeEnergy = LongDataHelper.toLong(value, LongDataHelper.upperInt(storedForgeEnergy));
                }

                if (index == DATA_INDEX_STORED_ENERGY_UPPER) {
                    storedForgeEnergy = LongDataHelper.toLong(LongDataHelper.lowerInt(storedForgeEnergy), value);
                }

                if (index == DATA_INDEX_CONVERTED_ENERGY) {
                    lastConvertedForgeEnergy = LongDataHelper.toLong(value, LongDataHelper.upperInt(lastConvertedForgeEnergy));
                }

                if (index == DATA_INDEX_CONVERTED_ENERGY_UPPER) {
                    lastConvertedForgeEnergy = LongDataHelper.toLong(LongDataHelper.lowerInt(lastConvertedForgeEnergy), value);
                }

                if (index == DATA_INDEX_EXPORTED_ENERGY) {
                    lastExportedForgeEnergy = LongDataHelper.toLong(value, LongDataHelper.upperInt(lastExportedForgeEnergy));
                }

                if (index == DATA_INDEX_EXPORTED_ENERGY_UPPER) {
                    lastExportedForgeEnergy = LongDataHelper.toLong(LongDataHelper.lowerInt(lastExportedForgeEnergy), value);
                }

                int automationIndex = index - DATA_INDEX_AUTOMATION_START;
                if (automationIndex >= 0 && automationIndex < AUTOMATION_FACE_COUNT) {
                    setAutomationMode(automationIndex, AutomationMode.fromId(value));
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    setAutoExportEnabled(value != 0);
                }
            }

            @Override
            public int getCount() {
                return DATA_INDEX_AUTO_EXPORT + 1;
            }
        };

        return new CobblestoneFEGeneratorMenu(containerId, playerInventory, this, generatorData);
    }

    private class GeneratorEnergyStorage implements IEnergyStorage {
        @Nullable
        private final Direction side;

        private GeneratorEnergyStorage(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!CobblestoneFEGeneratorBlockEntity.this.canExtractEnergy(this.side)) {
                return 0;
            }

            return (int) Math.min(CobblestoneFEGeneratorBlockEntity.this.extractForgeEnergy(maxExtract, simulate), Integer.MAX_VALUE);
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(CobblestoneFEGeneratorBlockEntity.this.getStoredForgeEnergy(), Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(CobblestoneFEGeneratorBlockEntity.this.getMaxForgeEnergy(), Integer.MAX_VALUE);
        }

        @Override
        public boolean canExtract() {
            return CobblestoneFEGeneratorBlockEntity.this.canExtractEnergy(this.side);
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }

    private class GeneratorFluxEnergyStorage implements FluxNetworkCompat.LongEnergyStorage {
        @Nullable
        private final Direction side;

        private GeneratorFluxEnergyStorage(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public long receiveEnergyL(long amount, boolean simulate) {
            return 0L;
        }

        @Override
        public long extractEnergyL(long amount, boolean simulate) {
            if (!CobblestoneFEGeneratorBlockEntity.this.canExtractEnergy(this.side)) {
                return 0L;
            }

            return CobblestoneFEGeneratorBlockEntity.this.extractForgeEnergy(amount, simulate);
        }

        @Override
        public long getEnergyStoredL() {
            return CobblestoneFEGeneratorBlockEntity.this.getStoredForgeEnergy();
        }

        @Override
        public long getMaxEnergyStoredL() {
            return CobblestoneFEGeneratorBlockEntity.this.getMaxForgeEnergy();
        }

        @Override
        public boolean canExtract() {
            return CobblestoneFEGeneratorBlockEntity.this.canExtractEnergy(this.side);
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
}
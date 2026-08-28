package com.yukke9265.cobblestone_xx_compressed.blockentity;

import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneFeCubeBlock;
import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.compat.flux.FluxNetworkCompat;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFeCubeMenu;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
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

public class CobblestoneFeCubeBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int CHARGE_SLOT_INDEX = 0;
    private static final int INVENTORY_SLOT_COUNT = 1;

    private static final int DATA_INDEX_STORED_ENERGY = 0;
    private static final int DATA_INDEX_STORED_ENERGY_UPPER = 1;
    private static final int DATA_INDEX_MAX_STORED_ENERGY = 2;
    private static final int DATA_INDEX_MAX_STORED_ENERGY_UPPER = 3;
    private static final int DATA_INDEX_IMPORTED_ENERGY = 4;
    private static final int DATA_INDEX_IMPORTED_ENERGY_UPPER = 5;
    private static final int DATA_INDEX_EXPORTED_ENERGY = 6;
    private static final int DATA_INDEX_EXPORTED_ENERGY_UPPER = 7;
    private static final int DATA_INDEX_AUTOMATION_START = 8;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_SOUND_MUTED = DATA_INDEX_AUTO_INSERT + 1;

    private long storedForgeEnergy;
    private long lastImportedForgeEnergy;
    private long lastExportedForgeEnergy;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(INVENTORY_SLOT_COUNT) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == CHARGE_SLOT_INDEX) {
                return CobblestoneFEGeneratorBlockEntity.isChargeableItem(stack);
            }

            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == CHARGE_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneFeCubeBlockEntity.this.setChanged();
        }
    };

    private final IEnergyStorage[] energyStorages = new IEnergyStorage[Direction.values().length + 1];
    private final Object[] fluxEnergyStorages = new Object[Direction.values().length + 1];

    public CobblestoneFeCubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_FE_CUBE_BLOCK_ENTITY.get(), pos, state);
    }

    public long getStoredForgeEnergy() {
        return this.storedForgeEnergy;
    }

    public long getMaxForgeEnergy() {
        BlockState currentState = this.getBlockState();
        if (currentState.getBlock() instanceof CobblestoneFeCubeBlock feCubeBlock) {
            return feCubeBlock.getCapacity();
        }

        return ModBlocks.BASE_FE_CUBE_CAPACITY;
    }

    public long getLastImportedForgeEnergy() {
        return this.lastImportedForgeEnergy;
    }

    public long getLastExportedForgeEnergy() {
        return this.lastExportedForgeEnergy;
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public IItemHandler getAutomationItemHandler(@Nullable Direction side) {
        // 充電スロットは手動専用なので、方向付き面には公開しません。
        if (side == null) {
            return this.itemStackHandler;
        }

        return EmptyItemHandler.INSTANCE;
    }

    @Nullable
    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        if (!this.canReceiveEnergy(side) && !this.canExtractEnergy(side)) {
            return null;
        }

        int capabilityIndex = this.getCapabilityIndex(side);
        IEnergyStorage storage = this.energyStorages[capabilityIndex];
        if (storage == null) {
            storage = new FeCubeEnergyStorage(side);
            this.energyStorages[capabilityIndex] = storage;
        }
        return storage;
    }

    @Nullable
    public Object getFluxEnergyCapability(@Nullable Direction side) {
        if (!this.canReceiveEnergy(side) && !this.canExtractEnergy(side)) {
            return null;
        }

        if (!FluxNetworkCompat.isLoaded()) {
            return null;
        }

        int capabilityIndex = this.getCapabilityIndex(side);
        Object storage = this.fluxEnergyStorages[capabilityIndex];
        if (storage == null) {
            storage = FluxNetworkCompat.createLongEnergyStorage(new FeCubeFluxEnergyStorage(side));
            this.fluxEnergyStorages[capabilityIndex] = storage;
        }

        return storage;
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        Level currentLevel = this.level;
        BlockState currentState = this.getBlockState();

        this.clampStoredForgeEnergy();
        this.lastImportedForgeEnergy = 0L;
        this.lastExportedForgeEnergy = 0L;

        long importedEnergy = this.pullForgeEnergyFromConfiguredSides();
        long exportedEnergy = this.chargeItemInSlot();
        exportedEnergy += this.pushForgeEnergyToConfiguredSides();

        boolean shouldTurnOn = importedEnergy > 0L || exportedEnergy > 0L;
        BlockState updatedState = currentState.setValue(OnOffBlock.ON, shouldTurnOn);
        if (updatedState != currentState) {
            currentLevel.setBlock(this.worldPosition, updatedState, 3);
        }
    }

    private long chargeItemInSlot() {
        if (this.storedForgeEnergy <= 0L) {
            return 0L;
        }

        ItemStack chargeStack = this.itemStackHandler.getStackInSlot(CHARGE_SLOT_INDEX);
        if (chargeStack.isEmpty()) {
            return 0L;
        }

        IEnergyStorage itemEnergyStorage = chargeStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (itemEnergyStorage == null || !itemEnergyStorage.canReceive()) {
            return 0L;
        }

        int toSend = (int) Math.min(this.storedForgeEnergy, Integer.MAX_VALUE);
        int accepted = itemEnergyStorage.receiveEnergy(toSend, false);
        if (accepted <= 0) {
            return 0L;
        }

        this.storedForgeEnergy -= accepted;
        this.lastExportedForgeEnergy += accepted;
        this.setChanged();
        return accepted;
    }

    private long pullForgeEnergyFromConfiguredSides() {
        if (!this.isAutoInsertEnabled() || this.storedForgeEnergy >= this.getMaxForgeEnergy() || this.level == null) {
            return 0L;
        }

        Level currentLevel = this.level;
        BlockState currentState = this.getBlockState();
        long importedEnergy = 0L;

        for (Direction direction : Direction.values()) {
            if (this.storedForgeEnergy >= this.getMaxForgeEnergy()) {
                break;
            }

            AutomationSide automationSide = AutomationSide.fromWorldSide(direction, currentState);
            AutomationMode automationMode = this.getAutomationMode(automationSide);
            if (automationMode != AutomationMode.INPUT && automationMode != AutomationMode.IN_OUT) {
                continue;
            }

            long neededEnergy = this.getMaxForgeEnergy() - this.storedForgeEnergy;
            if (neededEnergy <= 0L) {
                break;
            }

            BlockEntity sourceBlockEntity = currentLevel.getBlockEntity(this.worldPosition.relative(direction));
            Object fluxStorage = FluxNetworkCompat.getBlockEnergyStorage(sourceBlockEntity, direction.getOpposite());
            if (FluxNetworkCompat.canExtract(fluxStorage)) {
                long extracted = FluxNetworkCompat.extractEnergy(fluxStorage, neededEnergy, false);
                if (extracted > 0L) {
                    importedEnergy += this.receiveForgeEnergy(extracted, false);
                    continue;
                }
            }

            IEnergyStorage sourceStorage = currentLevel.getCapability(
                Capabilities.EnergyStorage.BLOCK,
                this.worldPosition.relative(direction),
                direction.getOpposite()
            );
            if (sourceStorage == null || !sourceStorage.canExtract()) {
                continue;
            }

            int toPull = (int) Math.min(neededEnergy, Integer.MAX_VALUE);
            int extracted = sourceStorage.extractEnergy(toPull, false);
            if (extracted <= 0) {
                continue;
            }

            importedEnergy += this.receiveForgeEnergy(extracted, false);
        }

        return importedEnergy;
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
                    this.lastExportedForgeEnergy += accepted;
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
            this.lastExportedForgeEnergy += accepted;
            this.setChanged();
        }

        return exportedEnergy;
    }

    private boolean canReceiveEnergy(@Nullable Direction side) {
        if (side == null) {
            return true;
        }

        BlockState currentState = this.getBlockState();
        AutomationSide automationSide = AutomationSide.fromWorldSide(side, currentState);
        AutomationMode automationMode = this.getAutomationMode(automationSide);
        return automationMode == AutomationMode.INPUT || automationMode == AutomationMode.IN_OUT;
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

    private long receiveForgeEnergy(long amount, boolean simulate) {
        if (amount <= 0L || this.storedForgeEnergy >= this.getMaxForgeEnergy()) {
            return 0L;
        }

        long acceptedEnergy = Math.min(amount, this.getMaxForgeEnergy() - this.storedForgeEnergy);
        if (!simulate) {
            this.storedForgeEnergy += acceptedEnergy;
            this.lastImportedForgeEnergy += acceptedEnergy;
            this.setChanged();
        }

        return acceptedEnergy;
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
        tag.putLong("storedForgeEnergy", this.storedForgeEnergy);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.storedForgeEnergy = tag.getLong("storedForgeEnergy");
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData feCubeData = new ContainerData() {
            @Override
            public int get(int index) {
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

                if (index == DATA_INDEX_IMPORTED_ENERGY) {
                    return LongDataHelper.lowerInt(lastImportedForgeEnergy);
                }

                if (index == DATA_INDEX_IMPORTED_ENERGY_UPPER) {
                    return LongDataHelper.upperInt(lastImportedForgeEnergy);
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

                if (index == DATA_INDEX_AUTO_INSERT) {
                    return getAutoInsertEnabledId();
                }

                if (index == DATA_INDEX_SOUND_MUTED) {
                    return getSoundMutedId();
                }

                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == DATA_INDEX_STORED_ENERGY) {
                    storedForgeEnergy = LongDataHelper.toLong(value, LongDataHelper.upperInt(storedForgeEnergy));
                }

                if (index == DATA_INDEX_STORED_ENERGY_UPPER) {
                    storedForgeEnergy = LongDataHelper.toLong(LongDataHelper.lowerInt(storedForgeEnergy), value);
                }

                if (index == DATA_INDEX_IMPORTED_ENERGY) {
                    lastImportedForgeEnergy = LongDataHelper.toLong(value, LongDataHelper.upperInt(lastImportedForgeEnergy));
                }

                if (index == DATA_INDEX_IMPORTED_ENERGY_UPPER) {
                    lastImportedForgeEnergy = LongDataHelper.toLong(LongDataHelper.lowerInt(lastImportedForgeEnergy), value);
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

                if (index == DATA_INDEX_AUTO_INSERT) {
                    setAutoInsertEnabled(value != 0);
                }

                if (index == DATA_INDEX_SOUND_MUTED) {
                    setSoundMuted(value != 0);
                }
            }

            @Override
            public int getCount() {
                return DATA_INDEX_SOUND_MUTED + 1;
            }
        };

        return new CobblestoneFeCubeMenu(containerId, playerInventory, this, feCubeData);
    }

    private class FeCubeEnergyStorage implements IEnergyStorage {
        @Nullable
        private final Direction side;

        private FeCubeEnergyStorage(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!CobblestoneFeCubeBlockEntity.this.canReceiveEnergy(this.side)) {
                return 0;
            }

            return (int) Math.min(CobblestoneFeCubeBlockEntity.this.receiveForgeEnergy(maxReceive, simulate), Integer.MAX_VALUE);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!CobblestoneFeCubeBlockEntity.this.canExtractEnergy(this.side)) {
                return 0;
            }

            return (int) Math.min(CobblestoneFeCubeBlockEntity.this.extractForgeEnergy(maxExtract, simulate), Integer.MAX_VALUE);
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(CobblestoneFeCubeBlockEntity.this.getStoredForgeEnergy(), Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(CobblestoneFeCubeBlockEntity.this.getMaxForgeEnergy(), Integer.MAX_VALUE);
        }

        @Override
        public boolean canExtract() {
            return CobblestoneFeCubeBlockEntity.this.canExtractEnergy(this.side);
        }

        @Override
        public boolean canReceive() {
            return CobblestoneFeCubeBlockEntity.this.canReceiveEnergy(this.side);
        }
    }

    private class FeCubeFluxEnergyStorage implements FluxNetworkCompat.LongEnergyStorage {
        @Nullable
        private final Direction side;

        private FeCubeFluxEnergyStorage(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public long receiveEnergyL(long amount, boolean simulate) {
            if (!CobblestoneFeCubeBlockEntity.this.canReceiveEnergy(this.side)) {
                return 0L;
            }

            return CobblestoneFeCubeBlockEntity.this.receiveForgeEnergy(amount, simulate);
        }

        @Override
        public long extractEnergyL(long amount, boolean simulate) {
            if (!CobblestoneFeCubeBlockEntity.this.canExtractEnergy(this.side)) {
                return 0L;
            }

            return CobblestoneFeCubeBlockEntity.this.extractForgeEnergy(amount, simulate);
        }

        @Override
        public long getEnergyStoredL() {
            return CobblestoneFeCubeBlockEntity.this.getStoredForgeEnergy();
        }

        @Override
        public long getMaxEnergyStoredL() {
            return CobblestoneFeCubeBlockEntity.this.getMaxForgeEnergy();
        }

        @Override
        public boolean canExtract() {
            return CobblestoneFeCubeBlockEntity.this.canExtractEnergy(this.side);
        }

        @Override
        public boolean canReceive() {
            return CobblestoneFeCubeBlockEntity.this.canReceiveEnergy(this.side);
        }
    }
}

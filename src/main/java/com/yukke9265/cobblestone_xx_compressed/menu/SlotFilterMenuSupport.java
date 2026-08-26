package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.ArrayList;
import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTarget;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTargetType;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.ISlotFilterHost;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.SlotFilter;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.SlotFilterEntry;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.SlotFilterMode;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/*
 * 方針:
 * メニュー側のフィルタ操作をここに集めます。
 * ghost 1x9 の中身と選択中ターゲット／WLBL を同期し、BE の SlotFilter と双方向に繋ぎます。
 */
public final class SlotFilterMenuSupport {
    public static final int FILTER_PREV_BUTTON_ID = 700;
    public static final int FILTER_NEXT_BUTTON_ID = 701;
    public static final int FILTER_MODE_BUTTON_ID = 702;
    public static final int FILTER_TOGGLE_BUTTON_ID = 703;

    private static final int DATA_INDEX_SELECTED = 0;
    private static final int DATA_INDEX_MODE = 1;
    private static final int DATA_INDEX_PANEL_OPEN = 2;
    private static final int DATA_COUNT = 3;

    private final BaseMenu menu;
    private final ISlotFilterHost filterHost;
    private final ItemStackHandler ghostHandler;
    private final ContainerData filterData;
    private final int ghostSlotStartIndex;
    private boolean suppressGhostWrite;

    public SlotFilterMenuSupport(BaseMenu menu, ISlotFilterHost filterHost) {
        this.menu = menu;
        this.filterHost = filterHost;
        this.ghostHandler = new ItemStackHandler(SlotFilter.MAX_ENTRIES) {
            @Override
            protected void onContentsChanged(int slot) {
                if (!SlotFilterMenuSupport.this.suppressGhostWrite) {
                    SlotFilterMenuSupport.this.writeGhostToSelectedFilter();
                }
            }
        };
        this.filterData = new SimpleContainerData(DATA_COUNT);
        this.ghostSlotStartIndex = menu.slots.size();

        menu.addSlotFilterDataSlots(this.filterData);
        this.addGhostSlots();
        this.reloadGhostFromSelectedFilter();
        this.syncFilterData();
    }

    public boolean hasFilterTargets() {
        return !this.filterHost.getFilterTargets().isEmpty();
    }

    public boolean isPanelOpen() {
        return this.filterData.get(DATA_INDEX_PANEL_OPEN) != 0;
    }

    public int getSelectedFilterIndex() {
        return this.filterData.get(DATA_INDEX_SELECTED);
    }

    public SlotFilterMode getSelectedFilterMode() {
        return SlotFilterMode.fromId(this.filterData.get(DATA_INDEX_MODE));
    }

    public FilterTarget getSelectedFilterTarget() {
        List<FilterTarget> targets = this.filterHost.getFilterTargets();
        if (targets.isEmpty()) {
            return null;
        }

        int index = this.getSelectedFilterIndex();
        if (index < 0 || index >= targets.size()) {
            return targets.get(0);
        }
        return targets.get(index);
    }

    public boolean handleButtonClick(int buttonId) {
        if (!this.hasFilterTargets()) {
            return false;
        }

        if (buttonId == FILTER_TOGGLE_BUTTON_ID) {
            this.filterData.set(DATA_INDEX_PANEL_OPEN, this.isPanelOpen() ? 0 : 1);
            return true;
        }

        if (!this.isPanelOpen()) {
            return false;
        }

        if (buttonId == FILTER_PREV_BUTTON_ID) {
            this.changeSelectedIndex(-1);
            return true;
        }
        if (buttonId == FILTER_NEXT_BUTTON_ID) {
            this.changeSelectedIndex(1);
            return true;
        }
        if (buttonId == FILTER_MODE_BUTTON_ID) {
            FilterTarget target = this.getSelectedFilterTarget();
            if (target == null) {
                return true;
            }
            this.filterHost.getSlotFilters().getOrCreate(target.id()).cycleMode();
            this.syncFilterData();
            this.filterHostAsBlockEntitySetChanged();
            return true;
        }
        return false;
    }

    public boolean handleGhostClick(int slotId, int button, ClickType clickType, Player player) {
        if (!this.isPanelOpen() || !this.isGhostSlotIndex(slotId)) {
            return false;
        }

        int ghostIndex = slotId - this.ghostSlotStartIndex;
        ItemStack carried = this.menu.getCarried();
        FilterTarget target = this.getSelectedFilterTarget();
        if (target == null) {
            return true;
        }

        if (button == 1 || clickType == ClickType.THROW) {
            this.ghostHandler.setStackInSlot(ghostIndex, ItemStack.EMPTY);
            return true;
        }

        if (carried.isEmpty()) {
            this.ghostHandler.setStackInSlot(ghostIndex, ItemStack.EMPTY);
            return true;
        }

        return this.applyGhostItem(ghostIndex, carried);
    }

    public int getGhostSlotStartIndex() {
        return this.ghostSlotStartIndex;
    }

    public boolean applyGhostItem(int ghostIndex, ItemStack carriedLike) {
        if (!this.isPanelOpen() || !this.isValidGhostIndex(ghostIndex)) {
            return false;
        }

        FilterTarget target = this.getSelectedFilterTarget();
        if (target == null) {
            return false;
        }

        if (carriedLike.isEmpty()) {
            this.ghostHandler.setStackInSlot(ghostIndex, ItemStack.EMPTY);
            return true;
        }

        SlotFilterEntry entry = SlotFilterEntry.fromCarried(carriedLike, target.type());
        if (entry == null) {
            return false;
        }

        this.ghostHandler.setStackInSlot(ghostIndex, entry.createDisplayStack());
        return true;
    }

    public boolean applyGhostFluid(int ghostIndex, net.neoforged.neoforge.fluids.FluidStack fluidStack) {
        if (!this.isPanelOpen() || !this.isValidGhostIndex(ghostIndex)) {
            return false;
        }

        FilterTarget target = this.getSelectedFilterTarget();
        if (target == null || target.type() != FilterTargetType.FLUID) {
            return false;
        }

        SlotFilterEntry entry = SlotFilterEntry.ofFluid(fluidStack);
        if (entry == null) {
            return false;
        }

        this.ghostHandler.setStackInSlot(ghostIndex, entry.createDisplayStack());
        return true;
    }

    private boolean isValidGhostIndex(int ghostIndex) {
        return ghostIndex >= 0 && ghostIndex < SlotFilter.MAX_ENTRIES;
    }

    private void addGhostSlots() {
        int startX = MachineGuiLayouts.SlotFilter.GHOST_START_X;
        int y = MachineGuiLayouts.SlotFilter.GHOST_SLOT_Y;
        for (int index = 0; index < SlotFilter.MAX_ENTRIES; index++) {
            int x = startX + index * MachineGuiLayouts.SLOT_SIZE;
            this.menu.addSlotFilterSlot(new GhostFilterSlot(
                this.ghostHandler,
                index,
                x,
                y,
                this::isPanelOpen
            ));
        }
    }

    private void changeSelectedIndex(int delta) {
        List<FilterTarget> targets = this.filterHost.getFilterTargets();
        if (targets.isEmpty()) {
            return;
        }

        int size = targets.size();
        int next = Math.floorMod(this.getSelectedFilterIndex() + delta, size);
        this.filterData.set(DATA_INDEX_SELECTED, next);
        this.reloadGhostFromSelectedFilter();
        this.syncFilterData();
    }

    private void reloadGhostFromSelectedFilter() {
        this.suppressGhostWrite = true;
        try {
            for (int index = 0; index < SlotFilter.MAX_ENTRIES; index++) {
                this.ghostHandler.setStackInSlot(index, ItemStack.EMPTY);
            }

            FilterTarget target = this.getSelectedFilterTarget();
            if (target == null) {
                return;
            }

            SlotFilter filter = this.filterHost.getSlotFilters().get(target.id());
            if (filter == null) {
                return;
            }

            List<SlotFilterEntry> entries = filter.getEntries();
            for (int index = 0; index < entries.size() && index < SlotFilter.MAX_ENTRIES; index++) {
                SlotFilterEntry entry = entries.get(index);
                if (entry != null) {
                    this.ghostHandler.setStackInSlot(index, entry.createDisplayStack());
                }
            }
        } finally {
            this.suppressGhostWrite = false;
        }
    }

    private void writeGhostToSelectedFilter() {
        if (this.filterHost instanceof net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
            if (blockEntity.getLevel() != null && blockEntity.getLevel().isClientSide) {
                return;
            }
        }

        FilterTarget target = this.getSelectedFilterTarget();
        if (target == null) {
            return;
        }

        List<ItemStack> displayStacks = new ArrayList<>();
        for (int index = 0; index < SlotFilter.MAX_ENTRIES; index++) {
            displayStacks.add(this.ghostHandler.getStackInSlot(index));
        }

        SlotFilter filter = this.filterHost.getSlotFilters().getOrCreate(target.id());
        FilterTargetType type = target.type();
        filter.setEntriesFromDisplayStacks(displayStacks, type);
        this.filterHostAsBlockEntitySetChanged();
        this.syncFilterData();
    }

    private void syncFilterData() {
        FilterTarget target = this.getSelectedFilterTarget();
        if (target == null) {
            this.filterData.set(DATA_INDEX_MODE, SlotFilterMode.WHITELIST.getId());
            return;
        }

        SlotFilter filter = this.filterHost.getSlotFilters().getOrCreate(target.id());
        this.filterData.set(DATA_INDEX_MODE, filter.getMode().getId());
        // PANEL_OPEN は開閉トグル専用なので、ここでは触りません。
    }

    private boolean isGhostSlotIndex(int slotId) {
        return slotId >= this.ghostSlotStartIndex && slotId < this.ghostSlotStartIndex + SlotFilter.MAX_ENTRIES;
    }

    private void filterHostAsBlockEntitySetChanged() {
        if (this.filterHost instanceof net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
            blockEntity.setChanged();
        }
    }
}

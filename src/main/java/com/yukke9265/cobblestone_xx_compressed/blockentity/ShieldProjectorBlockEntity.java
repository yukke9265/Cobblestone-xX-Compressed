package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.List;
import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.menu.ShieldProjectorMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.ShieldProjectorRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.ShieldProjectorRecipeHelper;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.shield.ShieldProjectorClientSync;
import com.yukke9265.cobblestone_xx_compressed.shield.ShieldProjectorTracker;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

/**
 * CP を消費して共有シールドを生成し、範囲内プレイヤーを保護します。
 */
public class ShieldProjectorBlockEntity extends PoweredMachineBlockEntityBase<ShieldProjectorRecipe> implements MenuProvider {
    public static final int POWER_SLOT_INDEX = 0;
    public static final int ACCELERATION_SLOT_INDEX = 1;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 2;
    public static final int PARALLEL_SLOT_INDEX = 3;
    public static final int CUSTOM_UPGRADE_SLOT_0_INDEX = 4;
    public static final int CUSTOM_UPGRADE_SLOT_1_INDEX = 5;
    public static final int CUSTOM_UPGRADE_SLOT_2_INDEX = 6;
    public static final int CUSTOM_UPGRADE_SLOT_3_INDEX = 7;
    public static final int INVENTORY_SLOT_COUNT = 8;

    public static final long MAX_COBBLESTONE_POWER = 4000L;
    public static final long BASE_MAX_SHIELD = 1000L;
    public static final double BASE_RANGE = 16.0D;

    private static final int MACHINE_SPECIFIC_DATA_COUNT = 6;
    private static final int DATA_INDEX_STORED_SHIELD = DATA_INDEX_MACHINE_SPECIFIC_START;
    private static final int DATA_INDEX_STORED_SHIELD_UPPER = DATA_INDEX_MACHINE_SPECIFIC_START + 1;
    private static final int DATA_INDEX_MAX_SHIELD = DATA_INDEX_MACHINE_SPECIFIC_START + 2;
    private static final int DATA_INDEX_MAX_SHIELD_UPPER = DATA_INDEX_MACHINE_SPECIFIC_START + 3;
    private static final int DATA_INDEX_SHIELD_RATE = DATA_INDEX_MACHINE_SPECIFIC_START + 4;
    private static final int DATA_INDEX_SHIELD_RATE_UPPER = DATA_INDEX_MACHINE_SPECIFIC_START + 5;

    private static final int CLIENT_SYNC_INTERVAL_TICKS = 10;

    private long storedShield;
    private long lastShieldGenerationRate;
    private int clientSyncCooldown;
    private boolean trackedAsActive;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(INVENTORY_SLOT_COUNT) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == POWER_SLOT_INDEX) {
                return isCobblestonePowerItem(stack);
            }

            if (slot == ACCELERATION_SLOT_INDEX) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }

            if (slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }

            if (slot == PARALLEL_SLOT_INDEX) {
                return MachineUpgradeHelper.isParallelChip(stack);
            }

            // 独自アップグレードは経路だけ先行し、当面は投入不可にします。
            if (isCustomUpgradeSlot(slot)) {
                return ShieldProjectorUpgradeHelper.isValidCustomUpgrade(slot, stack);
            }

            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            ShieldProjectorBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX
                || slot == ENERGIZED_CUBE_SLOT_INDEX
                || slot == PARALLEL_SLOT_INDEX
                || isCustomUpgradeSlot(slot)) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler cobblestoneInputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        POWER_SLOT_INDEX
    );

    public ShieldProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHIELD_PROJECTOR_BLOCK_ENTITY.get(), pos, state);
    }

    public static boolean isCustomUpgradeSlot(int slot) {
        return slot >= CUSTOM_UPGRADE_SLOT_0_INDEX && slot <= CUSTOM_UPGRADE_SLOT_3_INDEX;
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public IItemHandler getAutomationItemHandler(Direction side) {
        return this.getConfiguredAutomationItemHandler(
            side,
            EmptyItemHandler.INSTANCE,
            this.cobblestoneInputAutomationHandler,
            EmptyItemHandler.INSTANCE,
            this.cobblestoneInputAutomationHandler
        );
    }

    @Override
    protected long getBaseMaxCobblestonePower() {
        return MAX_COBBLESTONE_POWER;
    }

    @Override
    protected int getPowerSlotIndex() {
        return POWER_SLOT_INDEX;
    }

    @Override
    protected int getOutputSlotIndex() {
        return -1;
    }

    @Override
    protected void pushOutputsToConfiguredSides() {
        // アイテム出力が無いため何もしません。
    }

    @Override
    protected Optional<ShieldProjectorRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        return ShieldProjectorRecipeHelper.findRecipe(currentLevel);
    }

    @Override
    protected boolean canProcessRecipe(ShieldProjectorRecipe recipe) {
        return this.storedShield < this.getMaxShieldCapacity();
    }

    @Override
    protected boolean shouldResetProgress(ShieldProjectorRecipe recipe) {
        // シールド満タン時は CP 不足と同様に待機し、進捗は維持します。
        return false;
    }

    @Override
    protected int getRecipeProcessingTime(ShieldProjectorRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(ShieldProjectorRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(ShieldProjectorRecipe recipe) {
        this.addShield(recipe.getShieldOutput());
        this.refreshShieldGenerationRate(recipe);
    }

    private void refreshShieldGenerationRate(ShieldProjectorRecipe recipe) {
        long craftsPerCompletion = Math.max(1L, this.getCraftsPerCompletion());
        long processingTime = Math.max(1L, recipe.getProcessingTime());
        long progressStep = Math.max(1L, this.getProgressStep(recipe.getCobblestonePowerPerTick()));
        // 1 tick あたりの目安生成量（加速込み）。GUI 表示用です。
        this.lastShieldGenerationRate = Math.max(1L, (recipe.getShieldOutput() * craftsPerCompletion * progressStep) / processingTime);
    }

    public void addShield(long amount) {
        if (amount <= 0L) {
            return;
        }

        long maxShield = this.getMaxShieldCapacity();
        long before = this.storedShield;
        this.storedShield = Math.min(maxShield, this.storedShield + amount);
        if (this.storedShield != before) {
            this.setChanged();
            this.syncShieldToClientsSoon();
        }
    }

    /**
     * 被ダメ肩代わり。吸収した量を返し、残りシールドを減らします。
     */
    public float absorbDamage(float damage) {
        if (damage <= 0.0f || this.storedShield <= 0L) {
            return 0.0f;
        }

        long absorbable = Math.min(this.storedShield, (long) Math.ceil(damage));
        this.storedShield -= absorbable;
        this.setChanged();
        this.syncShieldToClientsSoon();
        return absorbable;
    }

    public long getStoredShield() {
        return this.storedShield;
    }

    public long getMaxShieldCapacity() {
        // 独自アップグレード未実装中は基本値だけを返します。
        return ShieldProjectorUpgradeHelper.getMaxShieldCapacity(this.itemStackHandler, BASE_MAX_SHIELD);
    }

    public double getEffectiveRange() {
        return ShieldProjectorUpgradeHelper.getEffectiveRange(this.itemStackHandler, BASE_RANGE);
    }

    public long getLastShieldGenerationRate() {
        return this.lastShieldGenerationRate;
    }

    public boolean canProtectPlayers() {
        return this.getIsAvailable() && this.level != null && !this.level.isClientSide();
    }

    public boolean isPlayerInRange(Player player) {
        if (player.level() != this.level) {
            return false;
        }

        double range = this.getEffectiveRange();
        double distanceSquared = player.distanceToSqr(
            this.worldPosition.getX() + 0.5D,
            this.worldPosition.getY() + 0.5D,
            this.worldPosition.getZ() + 0.5D
        );
        return distanceSquared <= range * range;
    }

    private AABB createProtectionArea() {
        double range = this.getEffectiveRange();
        BlockPos pos = this.worldPosition;
        return new AABB(
            pos.getX() + 0.5D - range,
            pos.getY() + 0.5D - range,
            pos.getZ() + 0.5D - range,
            pos.getX() + 0.5D + range,
            pos.getY() + 0.5D + range,
            pos.getZ() + 0.5D + range
        );
    }

    @Override
    public void tick() {
        super.tick();

        Level currentLevel = this.level;
        if (currentLevel == null || currentLevel.isClientSide()) {
            return;
        }

        this.updateTrackerRegistration();
        this.clampStoredShield();
        this.protectPlayersInRange();
        this.syncShieldToNearbyPlayers();
    }

    private void updateTrackerRegistration() {
        boolean shouldTrack = this.getIsAvailable();
        if (shouldTrack && !this.trackedAsActive) {
            ShieldProjectorTracker.add(this.level, this.worldPosition);
            this.trackedAsActive = true;
            return;
        }

        if (!shouldTrack && this.trackedAsActive) {
            ShieldProjectorTracker.remove(this.level, this.worldPosition);
            this.trackedAsActive = false;
        }
    }

    private void clampStoredShield() {
        long maxShield = this.getMaxShieldCapacity();
        if (this.storedShield > maxShield) {
            this.storedShield = maxShield;
            this.setChanged();
        }
    }

    private void protectPlayersInRange() {
        if (!this.canProtectPlayers()) {
            return;
        }

        List<ServerPlayer> players = ((ServerLevel) this.level).getEntitiesOfClass(
            ServerPlayer.class,
            this.createProtectionArea(),
            player -> this.isPlayerInRange(player) && player.isAlive()
        );

        for (ServerPlayer player : players) {
            float maxHealth = player.getMaxHealth();
            if (player.getHealth() < maxHealth) {
                player.setHealth(maxHealth);
            }
        }
    }

    private void syncShieldToClientsSoon() {
        this.clientSyncCooldown = 0;
    }

    private void syncShieldToNearbyPlayers() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.clientSyncCooldown > 0) {
            this.clientSyncCooldown--;
            return;
        }

        this.clientSyncCooldown = CLIENT_SYNC_INTERVAL_TICKS;
        if (!this.getIsAvailable()) {
            return;
        }

        List<ServerPlayer> players = serverLevel.getEntitiesOfClass(
            ServerPlayer.class,
            this.createProtectionArea(),
            player -> this.isPlayerInRange(player)
        );

        for (ServerPlayer player : players) {
            ShieldProjectorClientSync.sendToPlayer(
                player,
                this.worldPosition,
                this.storedShield,
                this.getMaxShieldCapacity()
            );
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide() && this.getIsAvailable()) {
            ShieldProjectorTracker.add(this.level, this.worldPosition);
            this.trackedAsActive = true;
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide()) {
            ShieldProjectorTracker.remove(this.level, this.worldPosition);
            this.trackedAsActive = false;
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("storedShield", this.storedShield);
        tag.putLong("lastShieldGenerationRate", this.lastShieldGenerationRate);
    }

    @Override
    protected void loadAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
        this.storedShield = tag.getLong("storedShield");
        this.lastShieldGenerationRate = tag.getLong("lastShieldGenerationRate");
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putLong("storedShield", this.storedShield);
        tag.putLong("maxShield", this.getMaxShieldCapacity());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        this.storedShield = tag.getLong("storedShield");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.shield_projector");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData projectorData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_STORED_SHIELD) {
                    return LongDataHelper.lowerInt(ShieldProjectorBlockEntity.this.storedShield);
                }

                if (index == DATA_INDEX_STORED_SHIELD_UPPER) {
                    return LongDataHelper.upperInt(ShieldProjectorBlockEntity.this.storedShield);
                }

                if (index == DATA_INDEX_MAX_SHIELD) {
                    return LongDataHelper.lowerInt(ShieldProjectorBlockEntity.this.getMaxShieldCapacity());
                }

                if (index == DATA_INDEX_MAX_SHIELD_UPPER) {
                    return LongDataHelper.upperInt(ShieldProjectorBlockEntity.this.getMaxShieldCapacity());
                }

                if (index == DATA_INDEX_SHIELD_RATE) {
                    return LongDataHelper.lowerInt(ShieldProjectorBlockEntity.this.lastShieldGenerationRate);
                }

                if (index == DATA_INDEX_SHIELD_RATE_UPPER) {
                    return LongDataHelper.upperInt(ShieldProjectorBlockEntity.this.lastShieldGenerationRate);
                }

                return ShieldProjectorBlockEntity.this.getPoweredMachineCommonData(index, MACHINE_SPECIFIC_DATA_COUNT);
            }

            @Override
            public void set(int index, int value) {
                if (index == DATA_INDEX_STORED_SHIELD) {
                    ShieldProjectorBlockEntity.this.storedShield = LongDataHelper.toLong(
                        value,
                        LongDataHelper.upperInt(ShieldProjectorBlockEntity.this.storedShield)
                    );
                    return;
                }

                if (index == DATA_INDEX_STORED_SHIELD_UPPER) {
                    ShieldProjectorBlockEntity.this.storedShield = LongDataHelper.toLong(
                        LongDataHelper.lowerInt(ShieldProjectorBlockEntity.this.storedShield),
                        value
                    );
                    return;
                }

                if (index == DATA_INDEX_SHIELD_RATE) {
                    ShieldProjectorBlockEntity.this.lastShieldGenerationRate = LongDataHelper.toLong(
                        value,
                        LongDataHelper.upperInt(ShieldProjectorBlockEntity.this.lastShieldGenerationRate)
                    );
                    return;
                }

                if (index == DATA_INDEX_SHIELD_RATE_UPPER) {
                    ShieldProjectorBlockEntity.this.lastShieldGenerationRate = LongDataHelper.toLong(
                        LongDataHelper.lowerInt(ShieldProjectorBlockEntity.this.lastShieldGenerationRate),
                        value
                    );
                    return;
                }

                ShieldProjectorBlockEntity.this.setPoweredMachineCommonData(index, value, MACHINE_SPECIFIC_DATA_COUNT);
            }

            @Override
            public int getCount() {
                return ShieldProjectorBlockEntity.this.getPoweredMachineDataCount(MACHINE_SPECIFIC_DATA_COUNT);
            }
        };

        return new ShieldProjectorMenu(containerId, playerInventory, this, projectorData);
    }

    // Menu から参照するレイアウト定数の置き場所を明示します。
    public static final class GuiSlots {
        public static final int CUSTOM_UPGRADE_START_X = MachineGuiLayouts.PoweredMachine.INPUT_SLOT_X - 9;
        public static final int CUSTOM_UPGRADE_Y = 17;
        public static final int CUSTOM_UPGRADE_SPACING = MachineGuiLayouts.SLOT_SIZE;

        private GuiSlots() {
        }
    }
}

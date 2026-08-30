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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
    public static final int CUSTOM_UPGRADE_SLOT_COUNT = 8;
    public static final int CUSTOM_UPGRADE_LAST_INDEX = CUSTOM_UPGRADE_SLOT_0_INDEX + CUSTOM_UPGRADE_SLOT_COUNT - 1;
    public static final int INVENTORY_SLOT_COUNT = CUSTOM_UPGRADE_LAST_INDEX + 1;

    public static final long MAX_COBBLESTONE_POWER = 4000L;
    public static final long BASE_MAX_SHIELD = 100L;
    public static final double BASE_RANGE = 16.0D;

    private static final int MACHINE_SPECIFIC_DATA_COUNT = 4;
    private static final int DATA_INDEX_STORED_SHIELD = DATA_INDEX_MACHINE_SPECIFIC_START;
    private static final int DATA_INDEX_STORED_SHIELD_UPPER = DATA_INDEX_MACHINE_SPECIFIC_START + 1;
    private static final int DATA_INDEX_MAX_SHIELD = DATA_INDEX_MACHINE_SPECIFIC_START + 2;
    private static final int DATA_INDEX_MAX_SHIELD_UPPER = DATA_INDEX_MACHINE_SPECIFIC_START + 3;

    private static final int CLIENT_SYNC_INTERVAL_TICKS = 10;

    private long storedShield;
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

            // 独自アップグレードは範囲・変換量・容量モジュールを8枠へ入れます。
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
        super(ModBlockEntities.COBBLESTONE_SHIELD_PROJECTOR_BLOCK_ENTITY.get(), pos, state);
    }

    public static boolean isCustomUpgradeSlot(int slot) {
        return slot >= CUSTOM_UPGRADE_SLOT_0_INDEX && slot <= CUSTOM_UPGRADE_LAST_INDEX;
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
    public boolean canInstallUpgradeItem(ItemStack stack) {
        if (super.canInstallUpgradeItem(stack)) {
            return true;
        }

        return this.findCustomUpgradeInstallSlot(stack) >= 0;
    }

    @Override
    public ItemStack installUpgradeItem(ItemStack stack, boolean simulate) {
        if (super.canInstallUpgradeItem(stack)) {
            return super.installUpgradeItem(stack, simulate);
        }

        int slot = this.findCustomUpgradeInstallSlot(stack);
        if (slot < 0) {
            return null;
        }

        ItemStack replacedStack = this.itemStackHandler.getStackInSlot(slot).copy();
        if (simulate) {
            return replacedStack;
        }

        this.itemStackHandler.setStackInSlot(slot, stack.copyWithCount(1));
        this.setChanged();
        return replacedStack;
    }

    private int findCustomUpgradeInstallSlot(ItemStack stack) {
        if (!ShieldProjectorUpgradeHelper.isValidCustomUpgrade(stack)) {
            return -1;
        }

        for (int slot = CUSTOM_UPGRADE_SLOT_0_INDEX; slot <= CUSTOM_UPGRADE_LAST_INDEX; slot++) {
            if (this.itemStackHandler.getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }

        return -1;
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
        long totalCobblestonePower = ShieldProjectorUpgradeHelper.getEffectiveTotalCobblestonePower(
            this.itemStackHandler,
            recipe.getTotalCobblestonePower(),
            recipe.getShieldOutput()
        );
        long cobblestonePowerPerTick = recipe.getCobblestonePowerPerTick();
        long processingTime = (totalCobblestonePower + cobblestonePowerPerTick - 1L) / cobblestonePowerPerTick;
        return Math.max(1, (int) Math.min(Integer.MAX_VALUE, processingTime));
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(ShieldProjectorRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(ShieldProjectorRecipe recipe) {
        long shieldOutput = ShieldProjectorUpgradeHelper.getEffectiveShieldOutput(
            this.itemStackHandler,
            recipe.getShieldOutput()
        );
        this.addShield(shieldOutput);
    }

    /**
     * 今のレシピと upgrade から、1 回完了あたりの総消費 CP を返します。
     * GUI はスロット変更直後にこの値を見ます。
     */
    public long getPreviewTotalCobblestonePower() {
        Optional<ShieldProjectorRecipe> recipeOptional = this.findMatchingRecipe();
        if (recipeOptional.isEmpty()) {
            return 0L;
        }

        ShieldProjectorRecipe recipe = recipeOptional.get();
        return ShieldProjectorUpgradeHelper.getEffectiveTotalCobblestonePower(
            this.itemStackHandler,
            recipe.getTotalCobblestonePower(),
            recipe.getShieldOutput()
        );
    }

    /**
     * 今のレシピと加速チップから、想定 CP/t を返します。
     * 稼働中でなくても、upgrade を置いた時点の値を出します。
     */
    public long getPreviewCobblestonePowerPerTick() {
        Optional<ShieldProjectorRecipe> recipeOptional = this.findMatchingRecipe();
        if (recipeOptional.isEmpty()) {
            return 0L;
        }

        long cobblestonePowerPerTick = this.getRecipeCobblestonePowerPerTick(recipeOptional.get());
        return cobblestonePowerPerTick * this.getUpgradeAccelerationMultiplier();
    }

    /**
     * 今のレシピと upgrade から、1 tick あたりの目安シールド生成量を返します。
     * 処理が止まっていても、HUD 用に設定値を出します。
     */
    public long getPreviewShieldGenerationRate() {
        Optional<ShieldProjectorRecipe> recipeOptional = this.findMatchingRecipe();
        if (recipeOptional.isEmpty()) {
            return 0L;
        }

        ShieldProjectorRecipe recipe = recipeOptional.get();
        long shieldOutput = ShieldProjectorUpgradeHelper.getEffectiveShieldOutput(
            this.itemStackHandler,
            recipe.getShieldOutput()
        );
        long craftsPerCompletion = Math.max(1L, this.getCraftsPerCompletion());
        long accelerationMultiplier = Math.max(1L, this.getUpgradeAccelerationMultiplier());
        long processingTime = Math.max(1L, this.getRecipeProcessingTime(recipe));
        long generatedPerCycle = shieldOutput * craftsPerCompletion * accelerationMultiplier;
        if (generatedPerCycle <= 0L) {
            return 0L;
        }

        long rate = generatedPerCycle / processingTime;
        if (rate > 0L) {
            return rate;
        }

        // 1 tick 未満の生成でも、HUD では 0 に潰さないようにします。
        return 1L;
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

    /**
     * 肩代わりが効いたときのフィードバック音です。GUI の消音が ON なら鳴らしません。
     */
    public void playAbsorbFeedbackSound(Player player) {
        if (this.isSoundMuted()) {
            return;
        }

        Level currentLevel = this.level;
        if (currentLevel == null || currentLevel.isClientSide()) {
            return;
        }

        currentLevel.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.SHIELD_BLOCK,
            SoundSource.PLAYERS,
            0.35f,
            1.15f
        );
    }

    public long getStoredShield() {
        return this.storedShield;
    }

    public long getMaxShieldCapacity() {
        return ShieldProjectorUpgradeHelper.getMaxShieldCapacity(this.itemStackHandler, BASE_MAX_SHIELD);
    }

    public double getEffectiveRange() {
        return ShieldProjectorUpgradeHelper.getEffectiveRange(this.itemStackHandler, BASE_RANGE);
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
                this.getMaxShieldCapacity(),
                this.getPreviewShieldGenerationRate()
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
    }

    @Override
    protected void loadAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
        this.storedShield = tag.getLong("storedShield");
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
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_shield_projector");
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

                ShieldProjectorBlockEntity.this.setPoweredMachineCommonData(index, value, MACHINE_SPECIFIC_DATA_COUNT);
            }

            @Override
            public int getCount() {
                return ShieldProjectorBlockEntity.this.getPoweredMachineDataCount(MACHINE_SPECIFIC_DATA_COUNT);
            }
        };

        return new ShieldProjectorMenu(containerId, playerInventory, this, projectorData);
    }

    // Menu / Screen 共通の GUI 座標です。
    // 進捗と独自 upgrade はタイトル直下に置き、Shield / Total・CP/t / CP 文字と被らないようにします。
    public static final class GuiSlots {
        public static final int PROGRESS_BAR_X = MachineGuiLayouts.PLAYER_INVENTORY_START_X;
        public static final int PROGRESS_BAR_Y = 16;
        public static final int PROGRESS_BAR_WIDTH = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_WIDTH;
        public static final int PROGRESS_BAR_HEIGHT = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_HEIGHT;
        public static final int CUSTOM_UPGRADE_Y = PROGRESS_BAR_Y;
        public static final int CUSTOM_UPGRADE_START_X = PROGRESS_BAR_X + PROGRESS_BAR_WIDTH + 4;
        public static final int CUSTOM_UPGRADE_SPACING = MachineGuiLayouts.SLOT_SIZE;
        // 8枠を進捗の右へ1段で並べます。幅は 8+16+4 + 8*18 = 172 で GUI 内に収まります。
        public static final int CUSTOM_UPGRADE_COLUMNS = 8;
        public static final int SHIELD_LABEL_X = MachineGuiLayouts.PoweredMachine.POWER_BAR_X;
        // スロット下端（Y+18）の下から、CP ラベルまでの間に Shield / Total・CP/t を置きます。
        public static final int SHIELD_LABEL_Y = 36;
        public static final int SHIELD_RATE_LABEL_Y = 45;

        private GuiSlots() {
        }

        public static int getCustomUpgradeSlotX(int index) {
            int column = index % CUSTOM_UPGRADE_COLUMNS;
            return CUSTOM_UPGRADE_START_X + column * CUSTOM_UPGRADE_SPACING;
        }

        public static int getCustomUpgradeSlotY(int index) {
            int row = index / CUSTOM_UPGRADE_COLUMNS;
            return CUSTOM_UPGRADE_Y + row * CUSTOM_UPGRADE_SPACING;
        }
    }
}

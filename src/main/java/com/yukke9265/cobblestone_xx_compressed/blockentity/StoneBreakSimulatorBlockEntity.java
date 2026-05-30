package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;
import com.yukke9265.cobblestone_xx_compressed.loot.StoneBreakSimulatorLootHelper;
import com.yukke9265.cobblestone_xx_compressed.menu.StoneBreakSimulatorMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.StoneBreakSimulatorRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

@SuppressWarnings("null")
public class StoneBreakSimulatorBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int INPUT_SLOT_1_INDEX = 0;
    public static final int INPUT_SLOT_2_INDEX = 1;
    public static final int POWER_SLOT_INDEX = 2;
    public static final int MAIN_OUTPUT_SLOT_INDEX = 3;
    public static final int SUB_OUTPUT_START_INDEX = 4;
    public static final int SUB_OUTPUT_END_INDEX = 8;
    public static final int ACCELERATION_SLOT_INDEX = 9;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 10;
    public static final long MAX_COBBLESTONE_POWER = 16000L;

    private static final int OUTPUT_SLOT_COUNT = 6;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_AUTOMATION_START = 6;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = DATA_INDEX_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = DATA_INDEX_CURRENT_POWER_RATE + 1;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_CURRENT_POWER_RATE_UPPER + 1;
    private static final int[] ALL_OUTPUT_SLOTS = new int[] { 3, 4, 5, 6, 7, 8 };
    private static final int[] SUB_OUTPUT_SLOTS = new int[] { 4, 5, 6, 7, 8 };

    private int progress = 0;
    private int maxProgress = 0;
    private long storedCobblestonePower = 0L;
    private boolean isAvailable = true;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(11) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (StoneBreakSimulatorBlockEntity.this.isOutputSlot(slot)) {
                return false;
            }

            if (slot == INPUT_SLOT_1_INDEX) {
                return CompressedStoneLootDefinition.findByStoneInput(stack) != null;
            }

            if (slot == INPUT_SLOT_2_INDEX) {
                return StoneBreakSimulatorBlockEntity.this.isPickaxe(stack);
            }

            if (slot == POWER_SLOT_INDEX) {
                return CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack);
            }

            if (slot == ACCELERATION_SLOT_INDEX) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }

            if (slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }

            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            StoneBreakSimulatorBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == INPUT_SLOT_2_INDEX || slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler inputAutomationHandler = this.createSequentialInsertHandler(new int[] { INPUT_SLOT_1_INDEX, INPUT_SLOT_2_INDEX });
    private final IItemHandler inputSlot1AutomationHandler = this.createSingleSlotInsertHandler(INPUT_SLOT_1_INDEX);
    private final IItemHandler inputSlot2AutomationHandler = this.createSingleSlotInsertHandler(INPUT_SLOT_2_INDEX);
    private final IItemHandler cobblestoneInputAutomationHandler = this.createSingleSlotInsertHandler(POWER_SLOT_INDEX);
    private final IItemHandler outputAutomationHandler = this.createOutputHandler(ALL_OUTPUT_SLOTS);
    private final IItemHandler outputMainAutomationHandler = this.createOutputHandler(new int[] { MAIN_OUTPUT_SLOT_INDEX });
    private final IItemHandler outputSubAutomationHandler = this.createOutputHandler(SUB_OUTPUT_SLOTS);
    private final IItemHandler automationAccessHandler = this.createAutomationAccessHandler();

    public StoneBreakSimulatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STONE_BREAK_SIMULATOR_BLOCK_ENTITY.get(), pos, state);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public long getStoredCobblestonePower() {
        return this.storedCobblestonePower;
    }

    public long getMaxCobblestonePower() {
        return MAX_COBBLESTONE_POWER * this.getEnergizedCubeMultiplier();
    }

    public long getCurrentCobblestonePowerConsumption() {
        if (!this.isAvailable) {
            return 0L;
        }

        Optional<RecipeHolder<StoneBreakSimulatorRecipe>> recipeHolder = this.getCurrentRecipe();
        if (recipeHolder.isEmpty()) {
            return 0L;
        }

        StoneBreakSimulatorRecipe recipe = recipeHolder.get().value();
        if (!this.canProcess(recipe)) {
            return 0L;
        }

        long cobblestonePowerPerTick = recipe.getCobblestonePowerPerTick();
        return cobblestonePowerPerTick * this.getProgressStep(cobblestonePowerPerTick);
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public IItemHandler getAutomationItemHandler(Direction side) {
        if (side == null) {
            return this.automationAccessHandler;
        }

        BlockState currentState = this.getBlockState();
        AutomationSide automationSide = AutomationSide.fromWorldSide(side, currentState);
        AutomationMode automationMode = this.getAutomationMode(automationSide);

        if (automationMode == AutomationMode.INPUT) {
            return this.inputAutomationHandler;
        }

        if (automationMode == AutomationMode.INPUT_1) {
            return this.inputSlot1AutomationHandler;
        }

        if (automationMode == AutomationMode.INPUT_2) {
            return this.inputSlot2AutomationHandler;
        }

        if (automationMode == AutomationMode.COBBLESTONE_INPUT) {
            return this.cobblestoneInputAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return this.outputAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT_1) {
            return this.outputMainAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT_2) {
            return this.outputSubAutomationHandler;
        }

        if (automationMode == AutomationMode.IN_OUT) {
            return this.automationAccessHandler;
        }

        return EmptyItemHandler.INSTANCE;
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
        boolean shouldTurnOn = false;

        this.clampStoredCobblestonePower();
        this.tryAbsorbCobblestonePower();

        Optional<RecipeHolder<StoneBreakSimulatorRecipe>> recipeHolder = this.getCurrentRecipe();
        if (this.isAvailable && recipeHolder.isPresent()) {
            StoneBreakSimulatorRecipe recipe = recipeHolder.get().value();
            int currentProcessingTime = this.getProcessingTime(recipe);
            if (this.maxProgress != currentProcessingTime) {
                this.maxProgress = currentProcessingTime;
                if (this.progress > this.maxProgress) {
                    this.progress = this.maxProgress;
                }
                this.setChanged();
            }

            if (this.canProcess(recipe)) {
                int progressStep = this.getProgressStep(recipe.getCobblestonePowerPerTick());
                this.progress += progressStep;
                this.storedCobblestonePower -= recipe.getCobblestonePowerPerTick() * progressStep;
                shouldTurnOn = true;
                this.setChanged();

                if (this.progress >= this.maxProgress) {
                    this.craft(recipe);
                    this.progress = 0;
                    this.setChanged();
                }
            } else if (this.progress != 0) {
                this.progress = 0;
                this.setChanged();
            }
        } else {
            if (this.progress != 0) {
                this.progress = 0;
                this.setChanged();
            }

            if (this.maxProgress != 0) {
                this.maxProgress = 0;
                this.setChanged();
            }
        }

        BlockState updatedState = currentState.setValue(OnOffBlock.ON, shouldTurnOn);
        if (updatedState != currentState) {
            currentLevel.setBlock(this.worldPosition, updatedState, 3);
        }

        this.pushOutputToConfiguredSides();
    }

    private void pushOutputToConfiguredSides() {
        ItemStack mainOutputStack = this.itemStackHandler.getStackInSlot(MAIN_OUTPUT_SLOT_INDEX);
        if (!mainOutputStack.isEmpty()) {
            ItemStack remainingMainOutput = this.pushItemStackToConfiguredSides(mainOutputStack.copy(), AutomationMode.OUTPUT, AutomationMode.OUTPUT_1, AutomationMode.IN_OUT);
            this.itemStackHandler.setStackInSlot(MAIN_OUTPUT_SLOT_INDEX, remainingMainOutput);
        }

        for (int slotIndex : SUB_OUTPUT_SLOTS) {
            ItemStack subOutputStack = this.itemStackHandler.getStackInSlot(slotIndex);
            if (subOutputStack.isEmpty()) {
                continue;
            }

            ItemStack remainingSubOutput = this.pushItemStackToConfiguredSides(subOutputStack.copy(), AutomationMode.OUTPUT, AutomationMode.OUTPUT_2, AutomationMode.IN_OUT);
            this.itemStackHandler.setStackInSlot(slotIndex, remainingSubOutput);
        }
    }

    private void tryAbsorbCobblestonePower() {
        ItemStack powerStack = this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX);
        long convertedPower = CobblestoneCrusherBlockEntity.getCobblestonePowerValueForAutomation(powerStack);
        if (convertedPower <= 0) {
            return;
        }

        if (this.storedCobblestonePower + convertedPower > this.getMaxCobblestonePower()) {
            return;
        }

        powerStack.shrink(1);
        this.storedCobblestonePower += convertedPower;
        this.setChanged();
    }

    @SuppressWarnings("null")
    public boolean canQuickMoveToInput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        for (RecipeHolder<StoneBreakSimulatorRecipe> recipeHolder : currentLevel.getRecipeManager().getAllRecipesFor(ModRecipeTypes.STONE_BREAK_SIMULATOR.get())) {
            if (recipeHolder.value().getIngredient().test(stack)) {
                return true;
            }
        }

        return false;
    }

    private Optional<RecipeHolder<StoneBreakSimulatorRecipe>> getCurrentRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        return currentLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.STONE_BREAK_SIMULATOR.get(), input, currentLevel);
    }

    private boolean canProcess(StoneBreakSimulatorRecipe recipe) {
        ItemStack toolStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX);
        if (!this.isPickaxe(toolStack)) {
            return false;
        }

        int progressStep = this.getProgressStep(recipe.getCobblestonePowerPerTick());
        if (progressStep <= 0) {
            return false;
        }

        return this.canOutput(recipe, toolStack);
    }

    private int getProcessingTime(StoneBreakSimulatorRecipe recipe) {
        ItemStack toolStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX);
        long adjustedTotalPower = this.getAdjustedTotalCobblestonePower(recipe, toolStack);
        long cobblestonePowerPerTick = Math.max(1L, recipe.getCobblestonePowerPerTick());
        long processingTime = (adjustedTotalPower + cobblestonePowerPerTick - 1L) / cobblestonePowerPerTick;
        return Math.max(1, (int) Math.min(Integer.MAX_VALUE, processingTime));
    }

    private int getProgressStep(long cobblestonePowerPerTick) {
        if (cobblestonePowerPerTick <= 0) {
            return 0;
        }

        int accelerationMultiplier = this.getAccelerationMultiplier();
        int remainingProgress = this.maxProgress - this.progress;
        if (remainingProgress <= 0) {
            return 0;
        }

        int maxProgressStep = Math.min(accelerationMultiplier, remainingProgress);
        long maxPowerStep = this.storedCobblestonePower / cobblestonePowerPerTick;
        return Math.min(maxProgressStep, (int) Math.min(Integer.MAX_VALUE, maxPowerStep));
    }

    private int getAccelerationMultiplier() {
        ItemStack accelerationStack = this.itemStackHandler.getStackInSlot(ACCELERATION_SLOT_INDEX);
        int multiplier = MachineUpgradeHelper.getAccelerationMultiplier(accelerationStack);
        if (multiplier <= 0) {
            return 1;
        }

        return multiplier;
    }

    private int getEnergizedCubeMultiplier() {
        ItemStack energizedCubeStack = this.itemStackHandler.getStackInSlot(ENERGIZED_CUBE_SLOT_INDEX);
        int multiplier = MachineUpgradeHelper.getEnergizedCubeMultiplier(energizedCubeStack);
        if (multiplier <= 0) {
            return 1;
        }

        return multiplier;
    }

    private void clampStoredCobblestonePower() {
        long maxCobblestonePower = this.getMaxCobblestonePower();
        if (this.storedCobblestonePower > maxCobblestonePower) {
            this.storedCobblestonePower = maxCobblestonePower;
            this.setChanged();
        }
    }

    private boolean canOutput(StoneBreakSimulatorRecipe recipe, ItemStack toolStack) {
        CompressedStoneLootDefinition definition = this.getCurrentLootDefinition();
        if (definition == null) {
            return false;
        }

        int blockCount = this.getCraftBlockCount(toolStack);
        if (blockCount <= 0) {
            return false;
        }

        ItemStack expectedMainOutput = new ItemStack(definition.getCobblestoneBlock().get(), blockCount);
        if (expectedMainOutput.getCount() > expectedMainOutput.getMaxStackSize()) {
            return false;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(MAIN_OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(outputStack, expectedMainOutput)) {
            return false;
        }

        return outputStack.getCount() + expectedMainOutput.getCount() <= outputStack.getMaxStackSize();
    }

    private void craft(StoneBreakSimulatorRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX);
        ItemStack toolStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX);
        CompressedStoneLootDefinition definition = CompressedStoneLootDefinition.findByStoneInput(inputStack);
        if (definition == null) {
            return;
        }

        int blockCount = this.getCraftBlockCount(toolStack);
        if (blockCount <= 0) {
            return;
        }

        int fortuneLevel = this.getEnchantmentLevel(toolStack, "fortune");
        StoneBreakSimulatorLootHelper.GeneratedDrops generatedDrops = StoneBreakSimulatorLootHelper.generateDrops(
            definition,
            blockCount,
            fortuneLevel,
            currentLevel.random
        );

        this.insertMainOutput(generatedDrops.mainDrop());
        this.insertSubOutputs(generatedDrops.subDrops());
        inputStack.shrink(blockCount);

        // 実際に石を壊したような完了感が出るように、レシピ完了時に破壊音を鳴らします。
        currentLevel.playSound(null, this.worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    private void insertMainOutput(ItemStack mainDrop) {
        if (mainDrop.isEmpty()) {
            return;
        }

        ItemStack existingStack = this.itemStackHandler.getStackInSlot(MAIN_OUTPUT_SLOT_INDEX);
        if (existingStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(MAIN_OUTPUT_SLOT_INDEX, mainDrop.copy());
            return;
        }

        existingStack.grow(mainDrop.getCount());
    }

    private void insertSubOutputs(List<ItemStack> subDrops) {
        for (ItemStack subDrop : subDrops) {
            int remainingCount = subDrop.getCount();

            for (int slotIndex : SUB_OUTPUT_SLOTS) {
                if (remainingCount <= 0) {
                    break;
                }

                ItemStack existingStack = this.itemStackHandler.getStackInSlot(slotIndex);
                if (existingStack.isEmpty()) {
                    continue;
                }

                if (!ItemStack.isSameItemSameComponents(existingStack, subDrop)) {
                    continue;
                }

                int space = existingStack.getMaxStackSize() - existingStack.getCount();
                if (space <= 0) {
                    continue;
                }

                int movedCount = Math.min(space, remainingCount);
                existingStack.grow(movedCount);
                remainingCount -= movedCount;
            }

            for (int slotIndex : SUB_OUTPUT_SLOTS) {
                if (remainingCount <= 0) {
                    break;
                }

                ItemStack existingStack = this.itemStackHandler.getStackInSlot(slotIndex);
                if (!existingStack.isEmpty()) {
                    continue;
                }

                ItemStack newStack = subDrop.copy();
                int movedCount = Math.min(newStack.getMaxStackSize(), remainingCount);
                newStack.setCount(movedCount);
                this.itemStackHandler.setStackInSlot(slotIndex, newStack);
                remainingCount -= movedCount;
            }
        }
    }

    private int getCraftBlockCount(ItemStack toolStack) {
        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX);
        if (inputStack.isEmpty()) {
            return 0;
        }

        int efficiencyLevel = this.getEnchantmentLevel(toolStack, "efficiency");
        int requestedBlockCount = Math.max(1, 1 + efficiencyLevel);
        return Math.min(requestedBlockCount, inputStack.getCount());
    }

    private long getAdjustedTotalCobblestonePower(StoneBreakSimulatorRecipe recipe, ItemStack toolStack) {
        long divisor = 1L << Math.min(30, Math.max(0, this.getEnchantmentLevel(toolStack, "unbreaking")));
        long adjustedTotal = recipe.getTotalCobblestonePower() / Math.max(1L, divisor);
        return Math.max(1L, adjustedTotal);
    }

    private int getEnchantmentLevel(ItemStack stack, String enchantmentPath) {
        Level currentLevel = this.level;
        if (currentLevel == null || stack.isEmpty()) {
            return 0;
        }

        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = currentLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ResourceKey<Enchantment> enchantmentKey = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("minecraft", enchantmentPath)
        );
        Holder<Enchantment> enchantment = enchantmentLookup.getOrThrow(enchantmentKey);
        return stack.getEnchantmentLevel(enchantment);
    }

    private CompressedStoneLootDefinition getCurrentLootDefinition() {
        return CompressedStoneLootDefinition.findByStoneInput(this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX));
    }

    private boolean isPickaxe(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ItemTags.PICKAXES);
    }

    private boolean isOutputSlot(int slot) {
        return slot >= MAIN_OUTPUT_SLOT_INDEX && slot <= SUB_OUTPUT_END_INDEX;
    }

    private IItemHandler createSequentialInsertHandler(int[] targetSlots) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return targetSlots.length;
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                if (slot < 0 || slot >= targetSlots.length) {
                    return ItemStack.EMPTY;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getStackInSlot(targetSlots[slot]);
            }

            @Override
            public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (slot < 0 || slot >= targetSlots.length) {
                    return stack;
                }

                ItemStack remainingStack = stack;
                for (int targetSlot : targetSlots) {
                    remainingStack = StoneBreakSimulatorBlockEntity.this.itemStackHandler.insertItem(targetSlot, remainingStack, simulate);
                    if (remainingStack.isEmpty()) {
                        break;
                    }
                }

                return remainingStack;
            }

            @Override
            public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot < 0 || slot >= targetSlots.length) {
                    return 0;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlotLimit(targetSlots[slot]);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot < 0 || slot >= targetSlots.length) {
                    return false;
                }

                for (int targetSlot : targetSlots) {
                    if (StoneBreakSimulatorBlockEntity.this.itemStackHandler.isItemValid(targetSlot, stack)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    private IItemHandler createSingleSlotInsertHandler(int realSlot) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                if (slot != 0) {
                    return ItemStack.EMPTY;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getStackInSlot(realSlot);
            }

            @Override
            public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (slot != 0) {
                    return stack;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.insertItem(realSlot, stack, simulate);
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

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlotLimit(realSlot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot != 0) {
                    return false;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.isItemValid(realSlot, stack);
            }
        };
    }

    private IItemHandler createOutputHandler(int[] sourceSlots) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return sourceSlots.length;
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                if (slot < 0 || slot >= sourceSlots.length) {
                    return ItemStack.EMPTY;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getStackInSlot(sourceSlots[slot]);
            }

            @Override
            public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return stack;
            }

            @Override
            public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < 0 || slot >= sourceSlots.length) {
                    return ItemStack.EMPTY;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.extractItem(sourceSlots[slot], amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot < 0 || slot >= sourceSlots.length) {
                    return 0;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlotLimit(sourceSlots[slot]);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }
        };
    }

    private IItemHandler createAutomationAccessHandler() {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlots();
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                if (slot < 0 || slot >= StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlots()) {
                    return ItemStack.EMPTY;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getStackInSlot(slot);
            }

            @Override
            public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (slot < 0 || slot >= StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlots()) {
                    return stack;
                }

                if (StoneBreakSimulatorBlockEntity.this.isOutputSlot(slot)) {
                    return stack;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
            }

            @Override
            public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!StoneBreakSimulatorBlockEntity.this.isOutputSlot(slot)) {
                    return ItemStack.EMPTY;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot < 0 || slot >= StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlots()) {
                    return 0;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot < 0 || slot >= StoneBreakSimulatorBlockEntity.this.itemStackHandler.getSlots()) {
                    return false;
                }

                return StoneBreakSimulatorBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
            }
        };
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
        tag.putLong("storedCobblestonePower", this.storedCobblestonePower);
        tag.putBoolean("isAvailable", this.isAvailable);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.isAvailable = tag.getBoolean("isAvailable");
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.stone_break_simulator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData stoneBreakSimulatorData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_PROGRESS) {
                    return progress;
                }

                if (index == DATA_INDEX_MAX_PROGRESS) {
                    return maxProgress;
                }

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

                if (index == DATA_INDEX_CURRENT_POWER_RATE) {
                    return LongDataHelper.lowerInt(getCurrentCobblestonePowerConsumption());
                }

                if (index == DATA_INDEX_CURRENT_POWER_RATE_UPPER) {
                    return LongDataHelper.upperInt(getCurrentCobblestonePowerConsumption());
                }

                if (index >= DATA_INDEX_AUTOMATION_START && index < DATA_INDEX_CURRENT_POWER_RATE) {
                    return getAutomationModeId(index - DATA_INDEX_AUTOMATION_START);
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return getAutoExportEnabledId();
                }

                return 0;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_INDEX_AUTO_EXPORT + 1;
            }
        };

        return new StoneBreakSimulatorMenu(containerId, playerInventory, this, stoneBreakSimulatorData);
    }
}
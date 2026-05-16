package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;

import org.jetbrains.annotations.NotNull;

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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

public class CobblestoneFurnaceBlockEntity extends BaseBlockEntity implements MenuProvider {
    private static final int INPUT_SLOT_INDEX = 0;
    private static final int OUTPUT_SLOT_INDEX = 1;
    private static final int DATA_INDEX_BURN_TIME = 0;
    private static final int DATA_INDEX_MAX_BURN_TIME = 1;
    private static final int DATA_INDEX_AUTOMATION_START = 2;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_AUTOMATION_START + AUTOMATION_FACE_COUNT;

    private int burnTime = 60; // 炉の燃焼時間を管理する変数。初期値は60にしています。
    private int maxBurnTime = 60; // 炉の最大燃焼時間を管理する変数。初期値は60にしています。
    private boolean isAvailable = true; // 炉がON状態かOFF状態かを管理するフラグ。初期値はONにしています。

    // アイテムを管理するための ItemStackHandler。スロット数は2にしています。
    //スロット0: 入力、スロット1: 出力
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            // スロットの中身が変わったら保存対象が変わるので、BlockEntity を更新済みとして扱います。
            CobblestoneFurnaceBlockEntity.this.setChanged();
        }
    };

    // 上面と側面からは入力だけを許可する、自動搬入用ハンドラです。
    // 外部機械から見たときは 1 スロットだけ持つように見せます。
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

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_INDEX, stack, simulate);
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

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.getSlotLimit(INPUT_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot != 0) {
                return false;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.isItemValid(INPUT_SLOT_INDEX, stack);
        }
    };

    // 底面からは完成品だけを引き抜ける、自動搬出用ハンドラです。
    // こちらも外部機械からは 1 スロットだけ持つように見せます。
    private final IItemHandler outputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.getSlotLimit(OUTPUT_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

    // side が渡ってこない外部機械向けの汎用ハンドラです。
    // 入力スロット 0 は投入専用、出力スロット 1 は搬出専用、という基本ルールだけを守らせます。
    private final IItemHandler automationAccessHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot >= 2) {
                return ItemStack.EMPTY;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != INPUT_SLOT_INDEX) {
                return stack;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != OUTPUT_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 2) {
                return 0;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot != INPUT_SLOT_INDEX) {
                return false;
            }

            return CobblestoneFurnaceBlockEntity.this.itemStackHandler.isItemValid(INPUT_SLOT_INDEX, stack);
        }
    };

    public CobblestoneFurnaceBlockEntity(BlockPos pos, BlockState state) {
           super(ModBlockEntities.COBBLESTONE_FURNACE_BLOCK_ENTITY.get(), pos, state);
    }

    public int getBurnTime() {
        return burnTime; // 現在の燃焼時間を返すゲッターメソッド
    }

    public int getMaxBurnTime() {
        return maxBurnTime; // 最大燃焼時間を返すゲッターメソッド
    }

    public boolean getIsAvailable() {
        return isAvailable; // 炉がON状態かOFF状態かを返すゲッターメソッド
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler; // Menu から炉のスロット内容へアクセスするためのゲッターメソッド
    }

    public IItemHandler getAutomationItemHandler(Direction side) {
        // side が null の場合は、方向指定なしで外部アクセスしようとしている状態です。
        // このときでも「入力は投入専用、出力は搬出専用」という基本ルールを守る汎用ハンドラを返します。
        if (side == null) {
            return this.automationAccessHandler;
        }

        BlockState currentState = this.getBlockState();
        AutomationSide automationSide = AutomationSide.fromWorldSide(side, currentState);
        AutomationMode automationMode = this.getAutomationMode(automationSide);
        if (automationMode == AutomationMode.INPUT) {
            return this.inputAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return this.outputAutomationHandler;
        }

        if (automationMode == AutomationMode.IN_OUT) {
            return this.automationAccessHandler;
        }

        return EmptyItemHandler.INSTANCE;
    }

    public void reverseIsAvailable() {
        // ボタンからの開始要求はサーバー側でだけ受け付けます。
        // 炉の稼働状態を切り替えます。
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.isAvailable = !this.isAvailable;
        this.setChanged();
    }

     @Override
     public void onLoad() {
         super.onLoad();
         // ブロックエンティティがワールドにロードされたときの処理をここに追加できます。
         // 例えば、初期化処理や、ワールドからのデータの読み込みなどをここで行うことができます。
     }

     @Override
     public void setRemoved() {
         super.setRemoved();
         // ブロックエンティティがワールドから削除されるときの処理をここに追加できます。
         // 例えば、クリーンアップ処理や、ワールドへのデータの保存などをここで行うことができます。
     }

    @Override
    public void tick() {
        // ワールドが存在し、かつサーバーサイド（ゲームロジック側）でのみ処理を実行
        // クライアント側で実行するとマルチプレイで状態が同期されないため、サーバー側に限定している
        if (this.level == null || this.level.isClientSide) {
            return; // ワールドが存在しない場合やクライアント側の場合は処理を行わない
        }
        // このブロックの現在のブロック状態を取得（ブロックのプロパティ情報を含む）
        Level currentLevel = this.level;
        BlockState currentState = this.getBlockState();

        Optional<RecipeHolder<CobblestoneFurnaceRecipe>> recipeHolder = this.getCurrentRecipe();

        if (isAvailable && recipeHolder.isPresent()) {
            CobblestoneFurnaceRecipe recipe = recipeHolder.get().value();

            if (this.canOutput(recipe)) {
                if (this.maxBurnTime != recipe.getProcessingTime()) {
                    this.maxBurnTime = recipe.getProcessingTime();
                }

                this.burnTime++;
                currentState = currentState.setValue(OnOffBlock.ON, true);

                if (this.burnTime >= this.maxBurnTime) {
                    this.craft(recipe);
                    this.burnTime = 0;
                }

                this.setChanged();
            } else {
                this.burnTime = 0;
                currentState = currentState.setValue(OnOffBlock.ON, false);
            }
        } else {
            this.burnTime = 0;
            currentState = currentState.setValue(OnOffBlock.ON, false);
        }

        this.pushOutputToConfiguredSides();
        currentLevel.setBlock(this.worldPosition, currentState, 3); // ブロック状態を更新して、クライアントに変更を通知します。   
    }

    private void pushOutputToConfiguredSides() {
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            return;
        }

        ItemStack remainingOutput = this.pushItemStackToConfiguredSides(outputStack.copy(), AutomationMode.OUTPUT, AutomationMode.IN_OUT);
        this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, remainingOutput);
    }

    private Optional<RecipeHolder<CobblestoneFurnaceRecipe>> getCurrentRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        return currentLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.COBBLESTONE_FURNACE.get(), input, currentLevel);
    }

    private boolean canOutput(CobblestoneFurnaceRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        if (resultStack.isEmpty()) {
            return false;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
            return false;
        }

        return outputStack.getCount() + resultStack.getCount() <= outputStack.getMaxStackSize();
    }

    private void craft(CobblestoneFurnaceRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);

        inputStack.shrink(1);

        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("burnTime", this.burnTime); // ここに炉の燃焼時間などの状態を保存するコードを追加できます。
        tag.putInt("maxBurnTime", this.maxBurnTime); // 最大燃焼時間も一緒に保存して、再読み込み時にずれないようにします。
        tag.putBoolean("isAvailable", this.isAvailable); // ここに炉のON/OFF状態を保存するコードを追加できます。
        this.saveAutomationModes(tag);
        // ItemStackHandler が持っている全スロットの内容をまとめて保存します。
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.burnTime = tag.getInt("burnTime"); // ここにNBTタグから炉の燃焼時間などの状態を読み込むコードを追加できます。
        this.maxBurnTime = tag.getInt("maxBurnTime"); // 最大燃焼時間も保存値から戻します。
        this.isAvailable = tag.getBoolean("isAvailable"); // ここにNBTタグから炉のON/OFF状態を読み込むコードを追加できます。
        this.loadAutomationModes(tag);
        // 保存されているインベントリ情報があれば、全スロットをまとめて復元します。
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
    }

    // MenuProvider インターフェースの実装
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_furnace");
    }

    // MenuProvider インターフェースの実装
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // ここでは、CobblestoneFurnaceMenu クラスのインスタンスを作成して返すコードを追加します。
        // 例えば、CobblestoneFurnaceMenu クラスのコンストラクタに必要な引数を渡して、メニューを作成することができます。
        ContainerData furnaceData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_BURN_TIME) {
                    return burnTime;
                }

                if (index == DATA_INDEX_MAX_BURN_TIME) {
                    return maxBurnTime;
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
                if (index == DATA_INDEX_BURN_TIME) {
                    burnTime = value;
                }

                if (index == DATA_INDEX_MAX_BURN_TIME) {
                    maxBurnTime = value;
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

        return new CobblestoneFurnaceMenu(containerId, playerInventory, this, furnaceData);
    }
}
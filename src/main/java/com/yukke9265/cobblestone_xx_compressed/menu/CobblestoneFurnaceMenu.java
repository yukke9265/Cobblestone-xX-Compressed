package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneFurnaceBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CobblestoneFurnaceMenu extends BaseMenu {
    private static final AutomationMode[] FURNACE_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT
    };

    private static final int DATA_COUNT = 3 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_BURN_TIME = 0;
    private static final int DATA_INDEX_MAX_BURN_TIME = 1;
    private static final int DATA_INDEX_AUTO_EXPORT = 2 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int SLOT_SIZE = 18;
    private static final int MACHINE_SLOT_Y = 35;
    private static final int INPUT_SLOT_X = 56;
    private static final int OUTPUT_SLOT_X = 109;
    private static final int PLAYER_INVENTORY_START_X = 8;
    private static final int PLAYER_INVENTORY_START_Y = 84;
    private static final int HOTBAR_START_Y = 142;
    private static final int INPUT_SLOT_INDEX = 0;
    private static final int OUTPUT_SLOT_INDEX = 1;
    private static final int MACHINE_SLOT_COUNT = 2;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneFurnaceBlockEntity furnaceBlockEntity;
    private final ContainerData furnaceData;

    // CobblestoneFurnaceMenu クラスは、Cobblestone Furnace のインターフェースを定義するメニュークラスです。
    // サーバー側では、対象の BlockEntity 本体と同期したいデータを直接受け取ります。
    public CobblestoneFurnaceMenu(int containerId, Inventory playerInventory, CobblestoneFurnaceBlockEntity furnaceBlockEntity, ContainerData furnaceData) {
        super(ModMenuType.COBBLESTONE_FURNACE_MENU.get(), containerId);
        this.furnaceBlockEntity = furnaceBlockEntity;
        this.furnaceData = furnaceData;

        checkContainerDataCount(furnaceData, DATA_COUNT);
        this.addDataSlots(furnaceData);

        // ここでは、メニューが作成されたときに必要なスロットやデータスロットを追加することができます。
        this.addFurnaceSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    //getBurnTime() と getMaxBurnTime() は、CobblestoneFurnaceBlockEntity から燃焼時間の情報を取得するためのメソッドです。
    //これらのメソッドは、CobblestoneFurnaceScreen クラスで進行度バーを描画する際に使用されます。
    public int getBurnTime() {
        return this.furnaceData.get(DATA_INDEX_BURN_TIME);
    }

    public int getMaxBurnTime() {
        return this.furnaceData.get(DATA_INDEX_MAX_BURN_TIME);
    }

    //public CobblestoneFurnaceBlockEntity getFurnaceBlockEntity() {
    //    return this.furnaceBlockEntity;
    //}

    public boolean getIsAvailable() {
        return this.furnaceBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_MAX_BURN_TIME + 1 + automationSide.getIndex();
        return AutomationMode.fromId(this.furnaceData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.furnaceData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public int getAutomationButtonId(AutomationSide automationSide) {
        return this.getAutomationButtonId(automationSide.getIndex());
    }

    public int getAutoExportButtonId() {
        return this.getAutoExportToggleButtonId();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // Screen から送られたボタン番号を、サーバー側 Menu で受け取ります。
        // 今回は Start ボタンを 0 番として扱います。
        if (id == 0) {
            this.furnaceBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.handleAutomationButtonClick(this.furnaceBlockEntity, id, FURNACE_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleAutoExportButtonClick(this.furnaceBlockEntity, id)) {
            return true;
        }

        return false;
    }

    // NeoForge 1.21.1 の MenuType 登録では、クライアント側でこの 3 引数コンストラクタが必要です。
    // openMenu(furnaceBlockEntity, pos) で送られてきた座標を読み取り、
    // クライアント側のワールドから同じ位置の BlockEntity を引き直します。
    public CobblestoneFurnaceMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    @Override
    public boolean stillValid(Player player) {
        // まず BlockEntity がすでに削除されていないかを確認します。
        if (this.furnaceBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.furnaceBlockEntity.getBlockPos();

        // 現在その位置にあるブロックが、想定している炉ブロックのままかを確認します。
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_FURNACE.get())) {
            return false;
        }

        // その位置の BlockEntity が、今メニューが参照しているものと同じかを確認します。
        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.furnaceBlockEntity) {
            return false;
        }

        // プレイヤーが炉から離れすぎたらメニューを閉じるようにします。
        // 64.0 は、8 ブロック以内なら操作可能というバニラ系のよくある基準です。
        return player.distanceToSqr(
            blockPos.getX() + 0.5D,
            blockPos.getY() + 0.5D,
            blockPos.getZ() + 0.5D
        ) <= 64.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Shift + クリック時に、どの領域からどの領域へ送るかをここで決めます。
        // 今回の炉メニューでは、0 が入力、1 が出力、2 以降がプレイヤー所持品です。
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = slot.getItem();
        ItemStack copiedStack = sourceStack.copy();

        // 炉スロット側を Shift + クリックした場合は、プレイヤー所持品全体へ送ります。
        if (index < MACHINE_SLOT_COUNT) {
            if (!this.moveItemStackTo(sourceStack, PLAYER_INVENTORY_START_INDEX, HOTBAR_END_INDEX, true)) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(sourceStack, copiedStack);
        } else {
            // プレイヤー所持品側を Shift + クリックした場合は、まず入力スロットだけを試します。
            // 出力スロットは取り出し専用なので、ここでは移動先にしません。
            if (!this.moveItemStackTo(sourceStack, INPUT_SLOT_INDEX, INPUT_SLOT_INDEX + 1, false)) {
                // プレイヤーのメインインベントリから来た場合はホットバーへ、
                // ホットバーから来た場合はメインインベントリへ送って整理できるようにします。
                if (index < HOTBAR_START_INDEX) {
                    if (!this.moveItemStackTo(sourceStack, HOTBAR_START_INDEX, HOTBAR_END_INDEX, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(sourceStack, PLAYER_INVENTORY_START_INDEX, HOTBAR_START_INDEX, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (sourceStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (sourceStack.getCount() == copiedStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, sourceStack);
        return copiedStack;
    }

    // ここでは、CobblestoneFurnaceMenu クラスのコンストラクタを定義します。
    //このコンストラクタは、クライアント側で使用されるもので、RegistryFriendlyByteBuf から座標を読み取って、対応する BlockEntity を引き直すためのものです。
    private CobblestoneFurnaceMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getFurnaceBlockEntity(playerInventory, blockPos),// クライアント側で BlockEntity を引き直すためのメソッドを呼び出して、対応する BlockEntity を取得します。
            new SimpleContainerData(DATA_COUNT)// 燃焼時間などのデータを管理するための ContainerData を作成します。ここでは、SimpleContainerData を使用して、必要な数の整数スロットを確保しています。
        );
    }

    // getFurnaceBlockEntity メソッドは、クライアント側で BlockEntity を引き直すためのヘルパーメソッドです。
    // クライアント側のワールドから、指定された座標の BlockEntity を取得し、それが CobblestoneFurnaceBlockEntity のインスタンスであるかどうかを確認します。
    // もし取得した BlockEntity が CobblestoneFurnaceBlockEntity のインスタンスであれば、それを返します。そうでない場合は、IllegalStateException をスローしてエラーを報告します。
    private static CobblestoneFurnaceBlockEntity getFurnaceBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneFurnaceBlockEntity furnaceBlockEntity) {
            return furnaceBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Furnace の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addFurnaceSlots() {
        // ここで、炉のアイテムスロットを追加します。
        // ここでは入力スロット 1 個と、出力専用スロット 1 個を追加します。
        ItemStackHandler itemStackHandler = this.furnaceBlockEntity.getItemStackHandler();

        this.addSlot(new SlotItemHandler(itemStackHandler, INPUT_SLOT_INDEX, INPUT_SLOT_X, MACHINE_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, OUTPUT_SLOT_INDEX, OUTPUT_SLOT_X, MACHINE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                // 出力スロットにはアイテムを置けないようにします。
                return false;
            }
        });
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        // ここで、プレイヤーのインベントリスロットを追加します。
        // 3 行 9 列のメインインベントリを、機械スロットの下側へ並べます。
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
                int slotIndex = column + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;
                int x = PLAYER_INVENTORY_START_X + column * SLOT_SIZE;
                int y = PLAYER_INVENTORY_START_Y + row * SLOT_SIZE;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
    }

    private void addPlayerHotbarSlots(Inventory playerInventory) {
        // ここで、プレイヤーのホットバースロットを追加します。
        // 1 行 9 列のホットバーを、メインインベントリのさらに下へ並べます。
        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int x = PLAYER_INVENTORY_START_X + column * SLOT_SIZE;
            this.addSlot(new Slot(playerInventory, column, x, HOTBAR_START_Y));
        }
    }

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        // 入力スロット 0 のみをレシピ転送対象にし、
        // 出力スロット 1 は転送先に含めないようにします。
        return this.createSingleJeiRecipeTransferDefinition(
            ModJeiIds.COBBLESTONE_FURNACE,
            INPUT_SLOT_INDEX,
            1,
            PLAYER_INVENTORY_START_INDEX,
            PLAYER_INVENTORY_SLOT_COUNT + HOTBAR_SLOT_COUNT
        );
    }
}

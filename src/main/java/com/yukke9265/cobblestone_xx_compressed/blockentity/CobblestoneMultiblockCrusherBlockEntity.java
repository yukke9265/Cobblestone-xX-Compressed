package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMultiblockCrusherMenu;
import com.yukke9265.cobblestone_xx_compressed.multiblock.MultiblockPattern;
import com.yukke9265.cobblestone_xx_compressed.multiblock.MultiblockPoweredMachineBlockEntityBase;
import com.yukke9265.cobblestone_xx_compressed.multiblock.VirtualItemBuffer;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import net.minecraft.core.BlockPos;
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

/**
 * ポート型マルチブロッククラッシャーのコアです。
 * 既存 Crusher レシピを再利用し、入出力は long 仮想バッファで扱います。
 */
public class CobblestoneMultiblockCrusherBlockEntity
    extends MultiblockPoweredMachineBlockEntityBase<CobblestoneCrusherRecipe>
    implements MenuProvider {

    public static final long MAX_COBBLESTONE_POWER = 64_000L;
    public static final long BUFFER_CAPACITY = 1_000_000_000L;

    private static final MultiblockPattern PATTERN = MultiblockPattern.createMultiblockCrusherPattern();

    private final VirtualItemBuffer inputBuffer = new VirtualItemBuffer(BUFFER_CAPACITY);
    private final VirtualItemBuffer outputBuffer = new VirtualItemBuffer(BUFFER_CAPACITY);

    public CobblestoneMultiblockCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_MULTIBLOCK_CRUSHER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected MultiblockPattern getPattern() {
        return PATTERN;
    }

    @Override
    protected long getBaseMaxCobblestonePower() {
        return MAX_COBBLESTONE_POWER;
    }

    @Override
    protected VirtualItemBuffer getInputBuffer() {
        return this.inputBuffer;
    }

    @Override
    protected VirtualItemBuffer getOutputBuffer() {
        return this.outputBuffer;
    }

    public VirtualItemBuffer getInputBufferPublic() {
        return this.inputBuffer;
    }

    public VirtualItemBuffer getOutputBufferPublic() {
        return this.outputBuffer;
    }

    /**
     * 破壊時に仮想バッファ内のアイテムをドロップします。
     * long 個数はスタック単位に分割して落とします。
     */
    public void dropContents() {
        Level currentLevel = this.level;
        if (currentLevel == null || currentLevel.isClientSide) {
            return;
        }

        dropBuffer(currentLevel, this.worldPosition, this.inputBuffer);
        dropBuffer(currentLevel, this.worldPosition, this.outputBuffer);
    }

    private static void dropBuffer(Level level, BlockPos pos, VirtualItemBuffer buffer) {
        while (!buffer.isEmpty()) {
            ItemStack extracted = buffer.extract(buffer.getTemplateStack().getMaxStackSize(), false);
            if (extracted.isEmpty()) {
                break;
            }

            net.minecraft.world.Containers.dropItemStack(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                extracted
            );
        }
    }

    @Override
    protected Optional<CobblestoneCrusherRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null || this.inputBuffer.isEmpty()) {
            return Optional.empty();
        }

        ItemStack probe = this.inputBuffer.getTemplateStack().copyWithCount(1);
        SingleRecipeInput input = new SingleRecipeInput(probe);
        Optional<RecipeHolder<CobblestoneCrusherRecipe>> recipeHolder = currentLevel.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_CRUSHER.get(),
            input,
            currentLevel
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneCrusherRecipe recipe) {
        return this.canOutput(recipe) && this.inputBuffer.getCount() >= 1L;
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneCrusherRecipe recipe) {
        return !this.canOutput(recipe) || this.inputBuffer.getCount() < 1L;
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneCrusherRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneCrusherRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneCrusherRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        if (resultStack.isEmpty() || !this.inputBuffer.consume(1L)) {
            return;
        }

        this.outputBuffer.tryProduce(resultStack, resultStack.getCount());
    }

    private boolean canOutput(CobblestoneCrusherRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        if (resultStack.isEmpty()) {
            return false;
        }

        return this.outputBuffer.canProduce(resultStack, resultStack.getCount());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_multiblock_crusher");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData data = new ContainerData() {
            @Override
            public int get(int index) {
                return CobblestoneMultiblockCrusherBlockEntity.this.getMultiblockCommonData(index);
            }

            @Override
            public void set(int index, int value) {
                CobblestoneMultiblockCrusherBlockEntity.this.setMultiblockCommonData(index, value);
            }

            @Override
            public int getCount() {
                return CobblestoneMultiblockCrusherBlockEntity.this.getMultiblockDataCount();
            }
        };

        return new CobblestoneMultiblockCrusherMenu(containerId, playerInventory, this, data);
    }
}

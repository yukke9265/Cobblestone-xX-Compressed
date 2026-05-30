package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings("null")
public class ConfigurationCardItem extends Item {
    private static final String ROOT_TAG = "ConfigurationCard";
    private static final String DATA_TAG = "AutomationData";
    private static final String BLOCK_ID_TAG = "StoredBlockId";
    private static final String BLOCK_NAME_TAG = "StoredBlockName";
    private static final String TOOLTIP_EMPTY_KEY = "tooltip.cobblestonexxcompressed.configuration_card.empty";
    private static final String TOOLTIP_STORED_KEY = "tooltip.cobblestonexxcompressed.configuration_card.stored";
    private static final String MESSAGE_COPIED_KEY = "message.cobblestonexxcompressed.configuration_card.copied";
    private static final String MESSAGE_PASTED_KEY = "message.cobblestonexxcompressed.configuration_card.pasted";
    private static final String MESSAGE_CLEARED_KEY = "message.cobblestonexxcompressed.configuration_card.cleared";
    private static final String MESSAGE_INCOMPATIBLE_KEY = "message.cobblestonexxcompressed.configuration_card.incompatible";

    public ConfigurationCardItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @Nonnull InteractionResult useOn(@Nonnull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockEntity blockEntity = level.getBlockEntity(clickedPos);
        if (!(blockEntity instanceof BaseBlockEntity baseBlockEntity)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                this.storeConfiguration(stack, baseBlockEntity);
                player.displayClientMessage(
                    Component.translatable(MESSAGE_COPIED_KEY, blockEntity.getBlockState().getBlock().getName()).withStyle(ChatFormatting.GREEN),
                    true
                );
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        CompoundTag cardData = this.getStoredConfiguration(stack);
        if (cardData == null) {
            return InteractionResult.PASS;
        }

        if (!this.isCompatibleTarget(blockEntity.getBlockState().getBlock(), cardData)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(MESSAGE_INCOMPATIBLE_KEY).withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            CompoundTag automationData = cardData.getCompound(DATA_TAG);
            baseBlockEntity.applyAutomationCopyData(automationData);
            player.displayClientMessage(
                Component.translatable(MESSAGE_PASTED_KEY, blockEntity.getBlockState().getBlock().getName()).withStyle(ChatFormatting.GREEN),
                true
            );
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!player.isShiftKeyDown()) {
            return super.use(level, player, usedHand);
        }

        if (!level.isClientSide) {
            if (this.clearStoredConfiguration(stack)) {
                player.displayClientMessage(Component.translatable(MESSAGE_CLEARED_KEY).withStyle(ChatFormatting.GRAY), true);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CompoundTag cardData = this.getStoredConfiguration(stack);
        if (cardData == null || !cardData.contains(BLOCK_NAME_TAG)) {
            tooltipComponents.add(Component.translatable(TOOLTIP_EMPTY_KEY).withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltipComponents.add(
            Component.translatable(TOOLTIP_STORED_KEY, Component.translatable(cardData.getString(BLOCK_NAME_TAG)))
                .withStyle(ChatFormatting.GRAY)
        );
    }

    private void storeConfiguration(ItemStack stack, BaseBlockEntity blockEntity) {
        CompoundTag rootTag = this.getOrCreateCustomData(stack);
        CompoundTag cardTag = new CompoundTag();
        Block block = blockEntity.getBlockState().getBlock();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

        cardTag.putString(BLOCK_ID_TAG, blockId.toString());
        cardTag.putString(BLOCK_NAME_TAG, block.getDescriptionId());
        cardTag.put(DATA_TAG, blockEntity.createAutomationCopyData());
        rootTag.put(ROOT_TAG, cardTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(rootTag));
    }

    private boolean clearStoredConfiguration(ItemStack stack) {
        CompoundTag rootTag = this.getCustomDataCopy(stack);
        if (!rootTag.contains(ROOT_TAG)) {
            return false;
        }

        rootTag.remove(ROOT_TAG);
        if (rootTag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(rootTag));
        }
        return true;
    }

    private boolean isCompatibleTarget(Block targetBlock, CompoundTag cardData) {
        if (!cardData.contains(BLOCK_ID_TAG)) {
            return false;
        }

        ResourceLocation storedBlockId = ResourceLocation.tryParse(cardData.getString(BLOCK_ID_TAG));
        if (storedBlockId == null) {
            return false;
        }

        ResourceLocation targetBlockId = BuiltInRegistries.BLOCK.getKey(targetBlock);
        return storedBlockId.equals(targetBlockId);
    }

    private CompoundTag getStoredConfiguration(ItemStack stack) {
        CompoundTag rootTag = this.getCustomDataCopy(stack);
        if (!rootTag.contains(ROOT_TAG)) {
            return null;
        }

        return rootTag.getCompound(ROOT_TAG);
    }

    private CompoundTag getOrCreateCustomData(ItemStack stack) {
        CompoundTag rootTag = this.getCustomDataCopy(stack);
        return rootTag;
    }

    private CompoundTag getCustomDataCopy(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag();
    }
}
package com.yukke9265.cobblestone_xx_compressed.item;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@SuppressWarnings("null")
public abstract class BaseMachineUpgradeItem extends Item {
    public BaseMachineUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return super.useOn(context);
        }

        Level level = context.getLevel();
        if (!(level.getBlockEntity(context.getClickedPos()) instanceof BaseBlockEntity baseBlockEntity)) {
            return super.useOn(context);
        }

        ItemStack stack = context.getItemInHand();
        if (!baseBlockEntity.canInstallUpgradeItem(stack)) {
            return super.useOn(context);
        }

        if (!level.isClientSide && baseBlockEntity.installUpgradeItem(stack, false) && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
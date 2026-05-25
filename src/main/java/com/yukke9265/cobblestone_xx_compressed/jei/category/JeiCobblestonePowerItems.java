package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.ArrayList;
import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@SuppressWarnings("null")
public final class JeiCobblestonePowerItems {
    private JeiCobblestonePowerItems() {
    }

    public static List<ItemStack> getCatalystItems() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemStack(Items.COBBLESTONE));
        stacks.add(new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get()));

        for (ModBlocks.TierCompressedCobblestone tier : ModBlocks.TierCompressedCobblestone.values()) {
            stacks.add(new ItemStack(tier.getBlock().get()));
        }

        return List.copyOf(stacks);
    }
}
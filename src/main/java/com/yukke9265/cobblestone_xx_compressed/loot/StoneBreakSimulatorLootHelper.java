package com.yukke9265.cobblestone_xx_compressed.loot;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public final class StoneBreakSimulatorLootHelper {
    // 破壊シミュレート時に loot へ渡す仮プレイヤー。AE2 のクラスターなど THIS_ENTITY 必須のブロック向け。
    private static final GameProfile STONE_BREAK_FAKE_PLAYER_PROFILE = new GameProfile(
        UUID.nameUUIDFromBytes("cobblestonexxcompressed:stone_break_simulator".getBytes(StandardCharsets.UTF_8)),
        "[CobbleXX]"
    );

    private StoneBreakSimulatorLootHelper() {
    }

    public static GeneratedDrops generateDrops(CompressedStoneLootDefinition definition, int brokenBlockCount, int fortuneLevel, RandomSource random) {
        if (definition == null) {
            return new GeneratedDrops(ItemStack.EMPTY, List.of());
        }

        int safeBrokenBlockCount = Math.max(0, brokenBlockCount);
        if (safeBrokenBlockCount <= 0) {
            return new GeneratedDrops(ItemStack.EMPTY, List.of());
        }

        ItemStack mainDrop = new ItemStack(definition.getCobblestoneBlock().get(), safeBrokenBlockCount);
        List<ItemStack> subDrops = new ArrayList<>();

        for (CompressedStoneLootDefinition.BonusLootEntry bonusDrop : definition.getBonusDrops()) {
            if (!bonusDrop.hasResolvedItem()) {
                continue;
            }

            int totalDropCount = 0;
            for (int index = 0; index < safeBrokenBlockCount; index++) {
                if (random.nextDouble() >= bonusDrop.getChance()) {
                    continue;
                }

                totalDropCount += applyOreDropsFortune(1, fortuneLevel, random);
            }

            if (totalDropCount <= 0) {
                continue;
            }

            mergeStack(subDrops, new ItemStack(bonusDrop.getItem().get(), totalDropCount));
        }

        return new GeneratedDrops(mainDrop, List.copyOf(subDrops));
    }

    public static List<ItemStack> generateOreDrops(ItemStack oreInput, ItemStack toolStack, int brokenBlockCount, ServerLevel serverLevel, BlockPos machinePos) {
        List<ItemStack> mergedDrops = new ArrayList<>();
        if (oreInput.isEmpty() || serverLevel == null || machinePos == null) {
            return mergedDrops;
        }

        Block block = Block.byItem(oreInput.getItem());
        if (block == Blocks.AIR) {
            return mergedDrops;
        }

        int safeBrokenBlockCount = Math.max(0, brokenBlockCount);
        BlockState blockState = block.defaultBlockState();
        ItemStack toolForLoot = toolStack.copy();
        Player fakePlayer = getHarvestFakePlayer(serverLevel, machinePos);
        // ツルハシをそのまま渡すので、幸運とシルクタッチはバニラの loot table どおりに効きます。

        for (int index = 0; index < safeBrokenBlockCount; index++) {
            List<ItemStack> drops = Block.getDrops(blockState, serverLevel, machinePos, null, fakePlayer, toolForLoot);
            for (ItemStack drop : drops) {
                mergeStack(mergedDrops, drop);
            }
        }

        return mergedDrops;
    }

    private static Player getHarvestFakePlayer(ServerLevel serverLevel, BlockPos machinePos) {
        Player fakePlayer = FakePlayerFactory.get(serverLevel, STONE_BREAK_FAKE_PLAYER_PROFILE);
        fakePlayer.setPos(machinePos.getX() + 0.5D, machinePos.getY() + 0.5D, machinePos.getZ() + 0.5D);
        return fakePlayer;
    }

    private static int applyOreDropsFortune(int baseCount, int fortuneLevel, RandomSource random) {
        int safeBaseCount = Math.max(1, baseCount);
        if (fortuneLevel <= 0) {
            return safeBaseCount;
        }

        int bonus = random.nextInt(fortuneLevel + 2) - 1;
        if (bonus < 0) {
            bonus = 0;
        }

        return safeBaseCount * (bonus + 1);
    }

    private static void mergeStack(List<ItemStack> stacks, ItemStack stackToMerge) {
        if (stackToMerge.isEmpty()) {
            return;
        }

        for (ItemStack existingStack : stacks) {
            if (!ItemStack.isSameItemSameComponents(existingStack, stackToMerge)) {
                continue;
            }

            existingStack.grow(stackToMerge.getCount());
            return;
        }

        stacks.add(stackToMerge.copy());
    }

    public record GeneratedDrops(ItemStack mainDrop, List<ItemStack> subDrops) {
    }
}
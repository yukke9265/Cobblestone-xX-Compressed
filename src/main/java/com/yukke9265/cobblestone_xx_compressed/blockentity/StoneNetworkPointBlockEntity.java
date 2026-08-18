package com.yukke9265.cobblestone_xx_compressed.blockentity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.network.StoneNetworkAccess;
import com.yukke9265.cobblestone_xx_compressed.network.StoneNetworkBlocks;
import com.yukke9265.cobblestone_xx_compressed.network.StoneNetworkIds;
import com.yukke9265.cobblestone_xx_compressed.network.StoneNetworkRegistry;
import com.yukke9265.cobblestone_xx_compressed.network.StoneNetworkTopology;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * ネットワークポイントの BlockEntity です。
 *
 * BaseBlockEntity は使わない。面モードや自動搬出は機械側の仕事で、ここは転送だけを担当する。
 * 実体のアイテム・液体・FE は持たない。覚えるのは所属ネットワーク ID だけ。
 * 窓口オブジェクトは面ごとに固定なので、機械の付け外しでは invalidateCapabilities しない。
 */
public class StoneNetworkPointBlockEntity extends BlockEntity {
    private static final String NETWORK_ID_TAG = "NetworkId";

    private final StoneNetworkAccess networkAccess = new StoneNetworkAccess(this);
    private String networkId;
    private boolean chunkUnloading;

    public StoneNetworkPointBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STONE_NETWORK_POINT_BLOCK_ENTITY.get(), pos, state);
        this.networkId = StoneNetworkIds.fromPointPos(pos);
    }

    public String getNetworkId() {
        return this.networkId;
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return this.networkAccess.getItemHandler(side);
    }

    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        return this.networkAccess.getFluidHandler(side);
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return this.networkAccess.getEnergyStorage(side);
    }

    @Nullable
    public Object getFluxEnergyCapability(@Nullable Direction side) {
        return this.networkAccess.getFluxEnergyCapability(side);
    }

    public void clearLocalAccessCache() {
        this.networkAccess.invalidateCache();
    }

    public void applyRebuiltNetworkId(String newNetworkId) {
        if (newNetworkId.isEmpty()) {
            return;
        }

        Level currentLevel = this.level;
        if (newNetworkId.equals(this.networkId)) {
            this.networkAccess.invalidateCache();
            return;
        }

        if (currentLevel != null && !currentLevel.isClientSide) {
            StoneNetworkRegistry.unregister(currentLevel, this.worldPosition, this.networkId);
            this.networkId = newNetworkId;
            StoneNetworkRegistry.register(currentLevel, this.worldPosition, this.networkId);
            this.setChanged();
        } else {
            this.networkId = newNetworkId;
        }

        this.networkAccess.invalidateCache();
    }

    public void onNeighborChanged(BlockPos neighborPos) {
        Level currentLevel = this.level;
        if (currentLevel == null || currentLevel.isClientSide) {
            return;
        }

        if (StoneNetworkBlocks.isConductor(currentLevel.getBlockState(neighborPos))) {
            StoneNetworkTopology.scheduleRebuild(currentLevel, this.worldPosition);
            return;
        }

        StoneNetworkRegistry.scheduleCacheInvalidation(currentLevel, this.networkId);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Level currentLevel = this.level;
        if (currentLevel == null || currentLevel.isClientSide) {
            return;
        }

        StoneNetworkRegistry.register(currentLevel, this.worldPosition, this.networkId);
        StoneNetworkTopology.scheduleRebuild(currentLevel, this.worldPosition);
    }

    @Override
    public void onChunkUnloaded() {
        this.chunkUnloading = true;
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        Level currentLevel = this.level;
        String removedNetworkId = this.networkId;
        BlockPos removedPos = this.worldPosition.immutable();
        boolean shouldUnregister = currentLevel != null && !currentLevel.isClientSide;
        super.setRemoved();
        if (!shouldUnregister) {
            return;
        }

        StoneNetworkRegistry.unregister(currentLevel, removedPos, removedNetworkId);
        if (this.chunkUnloading) {
            StoneNetworkRegistry.scheduleCacheInvalidation(currentLevel, removedNetworkId);
            return;
        }

        StoneNetworkTopology.scheduleRebuildFromNeighbors(currentLevel, removedPos);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString(NETWORK_ID_TAG, this.networkId);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (!tag.contains(NETWORK_ID_TAG)) {
            return;
        }

        String loadedNetworkId = tag.getString(NETWORK_ID_TAG);
        if (loadedNetworkId.isEmpty() || StoneNetworkIds.SHARED.equals(loadedNetworkId)) {
            this.networkId = StoneNetworkIds.fromPointPos(this.worldPosition);
            return;
        }

        this.networkId = loadedNetworkId;
    }
}

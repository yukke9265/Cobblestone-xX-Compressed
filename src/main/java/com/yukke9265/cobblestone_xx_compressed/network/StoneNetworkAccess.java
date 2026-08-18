package com.yukke9265.cobblestone_xx_compressed.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.blockentity.StoneNetworkPointBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.compat.flux.FluxNetworkCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * ネットワークポイントの公開窓口と終点探索です。
 *
 * 設計意図:
 * - アイテム・液体・FE の実体は持たない。バッファにすると、後で導線を伸ばしたとき二重保管になる。
 * - 隣の handler をそのまま返さず、面ごとの転送窓口を返す。後から探索だけ差し替えても、外から見える窓口は同じままにする。
 * - 複数の隣を1つの巨大インベントリへまとめない。スロット番号がずれ、将来つなぐと数が爆発するため。
 * - 同じ機械の別面へ流すときは、スロットを足さず、入れられない／出せない終点の次を試す。
 * - 向きプロパティは持たない。問い合わせが来た面の隣だけ見ないことで、機械への自己ループを防ぐ。
 * - 同じネットワークポイントは終点にしない。導線扱いにし、機械の窓口だけを共有する。
 * - ポイント同士が隣でもつながってよい。歩くときはポイントもリレーと同じ導体として扱う。
 * - 地形の塗りつぶしは置く・壊すときだけ。問い合わせでは登録済みメンバーの隣だけ見る。
 * - キャッシュした終点の再検証はしない。無い handler は使うときに飛ばし、機械の変化でキャッシュを捨てる。
 * - キャッシュするのは座標と面だけ。handler 本体を握ると、機械の面設定変更で古いまま残る。
 * - 問い合わせ元のポイントの隣を先に試し、そのあと他メンバーへ回す。
 */
@SuppressWarnings("null")
public class StoneNetworkAccess {
    // 同じ問い合わせが自分経由で戻ってきても空で抜けるための印です。
    // 後でポイント同士をつなぐときも、この訪問集合を探索の重複防止に使います。
    private static final ThreadLocal<Set<BlockPos>> RESOLVING_POSITIONS =
        ThreadLocal.withInitial(HashSet::new);

    private final BlockEntity owner;
    // 終点の座標と面の一覧だけ覚える。スロットは結合せず、入れる・出すときに次の面を試す。
    private final Map<CacheKey, List<ResolvedEndpoint>> endpointCache = new HashMap<>();
    // 面ごとに窓口を1つ作って使い回す。identity を変えないので、パイプ側の再取得を減らせる。
    private final IItemHandler[] itemHandlers = new IItemHandler[sideCount()];
    private final IFluidHandler[] fluidHandlers = new IFluidHandler[sideCount()];
    private final IEnergyStorage[] energyStorages = new IEnergyStorage[sideCount()];
    private final Object[] fluxStorages = new Object[sideCount()];

    public StoneNetworkAccess(BlockEntity owner) {
        this.owner = owner;
        for (int index = 0; index < sideCount(); index++) {
            Direction side = sideFromIndex(index);
            this.itemHandlers[index] = new ForwardingItemHandler(side);
            this.fluidHandlers[index] = new ForwardingFluidHandler(side);
            this.energyStorages[index] = new ForwardingEnergyStorage(side);
        }
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        // 終点が無くても窓口自体は返す。空インベントリとして見せ、後から隣が付いたときに同じ窓口で届くようにする。
        return this.itemHandlers[sideIndex(side)];
    }

    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        return this.fluidHandlers[sideIndex(side)];
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return this.energyStorages[sideIndex(side)];
    }

    @Nullable
    public Object getFluxEnergyCapability(@Nullable Direction side) {
        if (!FluxNetworkCompat.isLoaded()) {
            return null;
        }

        int index = sideIndex(side);
        Object storage = this.fluxStorages[index];
        if (storage == null) {
            storage = FluxNetworkCompat.createLongEnergyStorage(new ForwardingFluxEnergyStorage(side));
            this.fluxStorages[index] = storage;
        }

        return storage;
    }

    public void invalidateCache() {
        this.endpointCache.clear();
    }

    private boolean beginResolve() {
        // 追加できなければ、今の呼び出しは自分自身へ戻ってきている。
        return RESOLVING_POSITIONS.get().add(this.owner.getBlockPos());
    }

    private void endResolve() {
        RESOLVING_POSITIONS.get().remove(this.owner.getBlockPos());
    }

    @Nullable
    private IItemHandler findItemHandler(@Nullable Direction excludeSide) {
        List<IItemHandler> handlers = this.findItemHandlers(excludeSide);
        if (handlers.isEmpty()) {
            return null;
        }

        return handlers.get(0);
    }

    private List<IItemHandler> findItemHandlers(@Nullable Direction excludeSide) {
        List<IItemHandler> handlers = new ArrayList<>();
        Level currentLevel = this.owner.getLevel();
        if (currentLevel == null) {
            return handlers;
        }

        for (ResolvedEndpoint endpoint : this.resolveEndpoints(CapabilityKind.ITEM, excludeSide)) {
            IItemHandler handler = currentLevel.getCapability(
                Capabilities.ItemHandler.BLOCK,
                endpoint.pos,
                endpoint.face
            );
            if (handler != null) {
                handlers.add(handler);
            }
        }

        return handlers;
    }

    @Nullable
    private IFluidHandler findFluidHandler(@Nullable Direction excludeSide) {
        List<IFluidHandler> handlers = this.findFluidHandlers(excludeSide);
        if (handlers.isEmpty()) {
            return null;
        }

        return handlers.get(0);
    }

    private List<IFluidHandler> findFluidHandlers(@Nullable Direction excludeSide) {
        List<IFluidHandler> handlers = new ArrayList<>();
        Level currentLevel = this.owner.getLevel();
        if (currentLevel == null) {
            return handlers;
        }

        for (ResolvedEndpoint endpoint : this.resolveEndpoints(CapabilityKind.FLUID, excludeSide)) {
            IFluidHandler handler = currentLevel.getCapability(
                Capabilities.FluidHandler.BLOCK,
                endpoint.pos,
                endpoint.face
            );
            if (handler != null) {
                handlers.add(handler);
            }
        }

        return handlers;
    }

    @Nullable
    private IEnergyStorage findEnergyStorage(@Nullable Direction excludeSide) {
        List<IEnergyStorage> storages = this.findEnergyStorages(excludeSide);
        if (storages.isEmpty()) {
            return null;
        }

        return storages.get(0);
    }

    private List<IEnergyStorage> findEnergyStorages(@Nullable Direction excludeSide) {
        List<IEnergyStorage> storages = new ArrayList<>();
        Level currentLevel = this.owner.getLevel();
        if (currentLevel == null) {
            return storages;
        }

        for (ResolvedEndpoint endpoint : this.resolveEndpoints(CapabilityKind.ENERGY, excludeSide)) {
            IEnergyStorage storage = currentLevel.getCapability(
                Capabilities.EnergyStorage.BLOCK,
                endpoint.pos,
                endpoint.face
            );
            if (storage != null) {
                storages.add(storage);
            }
        }

        return storages;
    }

    private List<ResolvedEndpoint> resolveEndpoints(CapabilityKind kind, @Nullable Direction excludeSide) {
        CacheKey cacheKey = new CacheKey(kind, excludeSide);
        List<ResolvedEndpoint> cachedEndpoints = this.endpointCache.get(cacheKey);
        if (cachedEndpoints != null) {
            return cachedEndpoints;
        }

        List<ResolvedEndpoint> foundEndpoints = this.scanAllEndpoints(kind, excludeSide);
        this.endpointCache.put(cacheKey, foundEndpoints);
        return foundEndpoints;
    }

    private List<ResolvedEndpoint> scanAllEndpoints(CapabilityKind kind, @Nullable Direction excludeSide) {
        Level currentLevel = this.owner.getLevel();
        if (currentLevel == null) {
            return List.of();
        }

        BlockPos origin = this.owner.getBlockPos();
        List<BlockPos> orderedMembers = this.orderMembers(
            StoneNetworkRegistry.getMembers(currentLevel, this.getNetworkId()),
            origin
        );

        List<ResolvedEndpoint> foundEndpoints = new ArrayList<>();
        for (BlockPos memberPos : orderedMembers) {
            this.collectMemberNeighbors(kind, excludeSide, origin, memberPos, foundEndpoints);
        }

        return foundEndpoints;
    }

    private List<BlockPos> orderMembers(List<BlockPos> members, BlockPos origin) {
        List<BlockPos> orderedMembers = new ArrayList<>(members.size() + 1);
        orderedMembers.add(origin.immutable());
        for (BlockPos memberPos : members) {
            if (!memberPos.equals(origin)) {
                orderedMembers.add(memberPos);
            }
        }

        return orderedMembers;
    }

    private void collectMemberNeighbors(
        CapabilityKind kind,
        @Nullable Direction excludeSide,
        BlockPos queriedPos,
        BlockPos memberPos,
        List<ResolvedEndpoint> foundEndpoints
    ) {
        Level currentLevel = this.owner.getLevel();
        if (currentLevel == null) {
            return;
        }

        boolean isQueriedPoint = memberPos.equals(queriedPos);
        for (Direction direction : Direction.values()) {
            if (isQueriedPoint && direction == excludeSide) {
                continue;
            }

            BlockPos neighborPos = memberPos.relative(direction);
            BlockState neighborState = currentLevel.getBlockState(neighborPos);
            if (StoneNetworkBlocks.isConductor(neighborState)) {
                continue;
            }

            Direction touchingFace = direction.getOpposite();
            if (!this.lookupNeighbor(kind, neighborPos, touchingFace)) {
                continue;
            }

            foundEndpoints.add(new ResolvedEndpoint(neighborPos.immutable(), touchingFace));
        }
    }

    private String getNetworkId() {
        if (this.owner instanceof StoneNetworkPointBlockEntity pointBlockEntity) {
            return pointBlockEntity.getNetworkId();
        }

        return StoneNetworkIds.fromPointPos(this.owner.getBlockPos());
    }

    private boolean lookupNeighbor(CapabilityKind kind, BlockPos neighborPos, Direction touchingFace) {
        Level currentLevel = this.owner.getLevel();
        if (currentLevel == null) {
            return false;
        }

        if (kind == CapabilityKind.ITEM) {
            return currentLevel.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, touchingFace) != null;
        }

        if (kind == CapabilityKind.FLUID) {
            return currentLevel.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, touchingFace) != null;
        }

        if (kind == CapabilityKind.ENERGY) {
            return currentLevel.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, touchingFace) != null;
        }

        BlockEntity neighborBlockEntity = currentLevel.getBlockEntity(neighborPos);
        if (FluxNetworkCompat.getBlockEnergyStorage(neighborBlockEntity, touchingFace) != null) {
            return true;
        }

        return currentLevel.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, touchingFace) != null;
    }

    @Nullable
    private Object findFluxStorage(@Nullable Direction excludeSide) {
        List<Object> storages = this.findFluxStorages(excludeSide);
        if (storages.isEmpty()) {
            return null;
        }

        return storages.get(0);
    }

    private List<Object> findFluxStorages(@Nullable Direction excludeSide) {
        List<Object> storages = new ArrayList<>();
        Level currentLevel = this.owner.getLevel();
        if (currentLevel == null) {
            return storages;
        }

        for (ResolvedEndpoint endpoint : this.resolveEndpoints(CapabilityKind.FLUX, excludeSide)) {
            BlockEntity neighborBlockEntity = currentLevel.getBlockEntity(endpoint.pos);
            Object fluxStorage = FluxNetworkCompat.getBlockEnergyStorage(neighborBlockEntity, endpoint.face);
            if (fluxStorage != null) {
                storages.add(fluxStorage);
                continue;
            }

            IEnergyStorage energyStorage = currentLevel.getCapability(
                Capabilities.EnergyStorage.BLOCK,
                endpoint.pos,
                endpoint.face
            );
            if (energyStorage != null) {
                storages.add(energyStorage);
            }
        }

        return storages;
    }

    private ItemStack insertIntoHandlers(List<IItemHandler> handlers, int slot, ItemStack stack, boolean simulate) {
        ItemStack remainingStack = stack;
        if (remainingStack.isEmpty() || handlers.isEmpty()) {
            return remainingStack;
        }

        IItemHandler firstHandler = handlers.get(0);
        if (slot >= 0 && slot < firstHandler.getSlots()) {
            remainingStack = firstHandler.insertItem(slot, remainingStack, simulate);
            if (remainingStack.isEmpty()) {
                return remainingStack;
            }
        }

        for (IItemHandler handler : handlers) {
            remainingStack = this.insertIntoHandler(handler, remainingStack, simulate);
            if (remainingStack.isEmpty()) {
                return remainingStack;
            }
        }

        return remainingStack;
    }

    private ItemStack insertIntoHandler(IItemHandler destinationHandler, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || destinationHandler.getSlots() <= 0) {
            return stack;
        }

        ItemStack remainingAfterFirstSlot = destinationHandler.insertItem(0, stack, simulate);
        if (remainingAfterFirstSlot.isEmpty() || destinationHandler.getSlots() == 1) {
            return remainingAfterFirstSlot;
        }

        ItemStack remainingIfSecondSlot = destinationHandler.insertItem(1, stack, simulate);
        boolean firstSlotFillsAllInputs = remainingAfterFirstSlot.getCount() == remainingIfSecondSlot.getCount()
            && ItemStack.isSameItemSameComponents(remainingAfterFirstSlot, remainingIfSecondSlot);
        if (firstSlotFillsAllInputs) {
            return remainingAfterFirstSlot;
        }

        ItemStack remainingStack = remainingAfterFirstSlot;
        for (int currentSlot = 1; currentSlot < destinationHandler.getSlots(); currentSlot++) {
            if (remainingStack.isEmpty()) {
                break;
            }

            remainingStack = destinationHandler.insertItem(currentSlot, remainingStack, simulate);
        }

        return remainingStack;
    }

    private ItemStack extractFromHandlers(List<IItemHandler> handlers, int slot, int amount, boolean simulate) {
        if (handlers.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        IItemHandler firstHandler = handlers.get(0);
        if (slot >= 0 && slot < firstHandler.getSlots()) {
            ItemStack extractedStack = firstHandler.extractItem(slot, amount, simulate);
            if (!extractedStack.isEmpty()) {
                return extractedStack;
            }
        }

        for (IItemHandler handler : handlers) {
            for (int currentSlot = 0; currentSlot < handler.getSlots(); currentSlot++) {
                ItemStack extractedStack = handler.extractItem(currentSlot, amount, simulate);
                if (!extractedStack.isEmpty()) {
                    return extractedStack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private static int sideCount() {
        return Direction.values().length + 1;
    }

    private static int sideIndex(@Nullable Direction side) {
        if (side == null) {
            return Direction.values().length;
        }

        return side.get3DDataValue();
    }

    @Nullable
    private static Direction sideFromIndex(int index) {
        if (index < 0 || index >= Direction.values().length) {
            return null;
        }

        return Direction.from3DDataValue(index);
    }

    private enum CapabilityKind {
        ITEM,
        FLUID,
        ENERGY,
        FLUX
    }

    private record CacheKey(CapabilityKind kind, @Nullable Direction excludeSide) {
    }

    // 終点は座標と、こちらに接している面だけを覚える。handler オブジェクトは使わない。
    private static final class ResolvedEndpoint {
        private final BlockPos pos;
        private final Direction face;

        private ResolvedEndpoint(BlockPos pos, Direction face) {
            this.pos = pos;
            this.face = face;
        }
    }

    // 外へ見せるのはこの転送窓口。スロットは結合せず、入れられなければ次の面を試す。
    private class ForwardingItemHandler implements IItemHandler {
        @Nullable
        private final Direction queriedSide;

        private ForwardingItemHandler(@Nullable Direction queriedSide) {
            this.queriedSide = queriedSide;
        }

        @Override
        public int getSlots() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                IItemHandler targetHandler = StoneNetworkAccess.this.findItemHandler(this.queriedSide);
                if (targetHandler == null) {
                    return 0;
                }

                return targetHandler.getSlots();
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return ItemStack.EMPTY;
            }

            try {
                IItemHandler targetHandler = StoneNetworkAccess.this.findItemHandler(this.queriedSide);
                if (targetHandler == null || slot < 0 || slot >= targetHandler.getSlots()) {
                    return ItemStack.EMPTY;
                }

                return targetHandler.getStackInSlot(slot);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || !StoneNetworkAccess.this.beginResolve()) {
                return stack;
            }

            try {
                List<IItemHandler> handlers = StoneNetworkAccess.this.findItemHandlers(this.queriedSide);
                return StoneNetworkAccess.this.insertIntoHandlers(handlers, slot, stack, simulate);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0 || !StoneNetworkAccess.this.beginResolve()) {
                return ItemStack.EMPTY;
            }

            try {
                List<IItemHandler> handlers = StoneNetworkAccess.this.findItemHandlers(this.queriedSide);
                return StoneNetworkAccess.this.extractFromHandlers(handlers, slot, amount, simulate);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                IItemHandler targetHandler = StoneNetworkAccess.this.findItemHandler(this.queriedSide);
                if (targetHandler == null || slot < 0 || slot >= targetHandler.getSlots()) {
                    return 0;
                }

                return targetHandler.getSlotLimit(slot);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return false;
            }

            try {
                List<IItemHandler> handlers = StoneNetworkAccess.this.findItemHandlers(this.queriedSide);
                if (handlers.isEmpty()) {
                    return false;
                }

                IItemHandler firstHandler = handlers.get(0);
                if (slot >= 0 && slot < firstHandler.getSlots() && firstHandler.isItemValid(slot, stack)) {
                    return true;
                }

                ItemStack leftoverStack = StoneNetworkAccess.this.insertIntoHandlers(handlers, slot, stack, true);
                return leftoverStack.getCount() < stack.getCount();
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }
    }

    private class ForwardingFluidHandler implements IFluidHandler {
        @Nullable
        private final Direction queriedSide;

        private ForwardingFluidHandler(@Nullable Direction queriedSide) {
            this.queriedSide = queriedSide;
        }

        @Override
        public int getTanks() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                IFluidHandler targetHandler = StoneNetworkAccess.this.findFluidHandler(this.queriedSide);
                if (targetHandler == null) {
                    return 0;
                }

                return targetHandler.getTanks();
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public @Nonnull FluidStack getFluidInTank(int tank) {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return FluidStack.EMPTY;
            }

            try {
                IFluidHandler targetHandler = StoneNetworkAccess.this.findFluidHandler(this.queriedSide);
                if (targetHandler == null || tank < 0 || tank >= targetHandler.getTanks()) {
                    return FluidStack.EMPTY;
                }

                return targetHandler.getFluidInTank(tank);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public int getTankCapacity(int tank) {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                IFluidHandler targetHandler = StoneNetworkAccess.this.findFluidHandler(this.queriedSide);
                if (targetHandler == null || tank < 0 || tank >= targetHandler.getTanks()) {
                    return 0;
                }

                return targetHandler.getTankCapacity(tank);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return false;
            }

            try {
                IFluidHandler targetHandler = StoneNetworkAccess.this.findFluidHandler(this.queriedSide);
                if (targetHandler == null || tank < 0 || tank >= targetHandler.getTanks()) {
                    return false;
                }

                return targetHandler.isFluidValid(tank, stack);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || !StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                List<IFluidHandler> handlers = StoneNetworkAccess.this.findFluidHandlers(this.queriedSide);
                FluidStack remainingStack = resource.copy();
                int filledAmount = 0;
                for (IFluidHandler handler : handlers) {
                    if (remainingStack.isEmpty()) {
                        break;
                    }

                    int acceptedAmount = handler.fill(remainingStack, action);
                    if (acceptedAmount <= 0) {
                        continue;
                    }

                    remainingStack.shrink(acceptedAmount);
                    filledAmount += acceptedAmount;
                }

                return filledAmount;
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || !StoneNetworkAccess.this.beginResolve()) {
                return FluidStack.EMPTY;
            }

            try {
                List<IFluidHandler> handlers = StoneNetworkAccess.this.findFluidHandlers(this.queriedSide);
                for (IFluidHandler handler : handlers) {
                    FluidStack drainedFluid = handler.drain(resource, action);
                    if (!drainedFluid.isEmpty()) {
                        return drainedFluid;
                    }
                }

                return FluidStack.EMPTY;
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
            if (maxDrain <= 0 || !StoneNetworkAccess.this.beginResolve()) {
                return FluidStack.EMPTY;
            }

            try {
                List<IFluidHandler> handlers = StoneNetworkAccess.this.findFluidHandlers(this.queriedSide);
                for (IFluidHandler handler : handlers) {
                    FluidStack drainedFluid = handler.drain(maxDrain, action);
                    if (!drainedFluid.isEmpty()) {
                        return drainedFluid;
                    }
                }

                return FluidStack.EMPTY;
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }
    }

    private class ForwardingEnergyStorage implements IEnergyStorage {
        @Nullable
        private final Direction queriedSide;

        private ForwardingEnergyStorage(@Nullable Direction queriedSide) {
            this.queriedSide = queriedSide;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (maxReceive <= 0 || !StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                List<IEnergyStorage> storages = StoneNetworkAccess.this.findEnergyStorages(this.queriedSide);
                int remainingEnergy = maxReceive;
                int acceptedEnergy = 0;
                for (IEnergyStorage storage : storages) {
                    if (remainingEnergy <= 0) {
                        break;
                    }

                    int accepted = storage.receiveEnergy(remainingEnergy, simulate);
                    if (accepted <= 0) {
                        continue;
                    }

                    remainingEnergy -= accepted;
                    acceptedEnergy += accepted;
                }

                return acceptedEnergy;
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (maxExtract <= 0 || !StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                List<IEnergyStorage> storages = StoneNetworkAccess.this.findEnergyStorages(this.queriedSide);
                int remainingEnergy = maxExtract;
                int extractedEnergy = 0;
                for (IEnergyStorage storage : storages) {
                    if (remainingEnergy <= 0) {
                        break;
                    }

                    int extracted = storage.extractEnergy(remainingEnergy, simulate);
                    if (extracted <= 0) {
                        continue;
                    }

                    remainingEnergy -= extracted;
                    extractedEnergy += extracted;
                }

                return extractedEnergy;
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public int getEnergyStored() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                IEnergyStorage targetStorage = StoneNetworkAccess.this.findEnergyStorage(this.queriedSide);
                if (targetStorage == null) {
                    return 0;
                }

                return targetStorage.getEnergyStored();
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public int getMaxEnergyStored() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return 0;
            }

            try {
                IEnergyStorage targetStorage = StoneNetworkAccess.this.findEnergyStorage(this.queriedSide);
                if (targetStorage == null) {
                    return 0;
                }

                return targetStorage.getMaxEnergyStored();
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public boolean canExtract() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return false;
            }

            try {
                IEnergyStorage targetStorage = StoneNetworkAccess.this.findEnergyStorage(this.queriedSide);
                return targetStorage != null && targetStorage.canExtract();
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public boolean canReceive() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return false;
            }

            try {
                IEnergyStorage targetStorage = StoneNetworkAccess.this.findEnergyStorage(this.queriedSide);
                return targetStorage != null && targetStorage.canReceive();
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }
    }

    private class ForwardingFluxEnergyStorage implements FluxNetworkCompat.LongEnergyStorage {
        @Nullable
        private final Direction queriedSide;

        private ForwardingFluxEnergyStorage(@Nullable Direction queriedSide) {
            this.queriedSide = queriedSide;
        }

        @Override
        public long receiveEnergyL(long amount, boolean simulate) {
            if (amount <= 0L || !StoneNetworkAccess.this.beginResolve()) {
                return 0L;
            }

            try {
                return this.receiveFromTarget(amount, simulate);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public long extractEnergyL(long amount, boolean simulate) {
            if (amount <= 0L || !StoneNetworkAccess.this.beginResolve()) {
                return 0L;
            }

            try {
                return this.extractFromTarget(amount, simulate);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public long getEnergyStoredL() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return 0L;
            }

            try {
                Object targetStorage = StoneNetworkAccess.this.findFluxStorage(this.queriedSide);
                if (targetStorage instanceof IEnergyStorage energyStorage) {
                    return energyStorage.getEnergyStored();
                }

                return FluxNetworkCompat.getEnergyStored(targetStorage);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public long getMaxEnergyStoredL() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return 0L;
            }

            try {
                Object targetStorage = StoneNetworkAccess.this.findFluxStorage(this.queriedSide);
                if (targetStorage instanceof IEnergyStorage energyStorage) {
                    return energyStorage.getMaxEnergyStored();
                }

                return FluxNetworkCompat.getMaxEnergyStored(targetStorage);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public boolean canExtract() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return false;
            }

            try {
                Object targetStorage = StoneNetworkAccess.this.findFluxStorage(this.queriedSide);
                if (targetStorage instanceof IEnergyStorage energyStorage) {
                    return energyStorage.canExtract();
                }

                return FluxNetworkCompat.canExtract(targetStorage);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        @Override
        public boolean canReceive() {
            if (!StoneNetworkAccess.this.beginResolve()) {
                return false;
            }

            try {
                Object targetStorage = StoneNetworkAccess.this.findFluxStorage(this.queriedSide);
                if (targetStorage instanceof IEnergyStorage energyStorage) {
                    return energyStorage.canReceive();
                }

                return FluxNetworkCompat.canReceive(targetStorage);
            } finally {
                StoneNetworkAccess.this.endResolve();
            }
        }

        private long receiveFromTarget(long amount, boolean simulate) {
            long remainingEnergy = amount;
            long acceptedEnergy = 0L;
            for (Object targetStorage : StoneNetworkAccess.this.findFluxStorages(this.queriedSide)) {
                if (remainingEnergy <= 0L) {
                    break;
                }

                long accepted;
                if (targetStorage instanceof IEnergyStorage energyStorage) {
                    int toSend = (int) Math.min(remainingEnergy, Integer.MAX_VALUE);
                    accepted = energyStorage.receiveEnergy(toSend, simulate);
                } else {
                    accepted = FluxNetworkCompat.receiveEnergy(targetStorage, remainingEnergy, simulate);
                }

                if (accepted <= 0L) {
                    continue;
                }

                remainingEnergy -= accepted;
                acceptedEnergy += accepted;
            }

            return acceptedEnergy;
        }

        private long extractFromTarget(long amount, boolean simulate) {
            long remainingEnergy = amount;
            long extractedEnergy = 0L;
            for (Object targetStorage : StoneNetworkAccess.this.findFluxStorages(this.queriedSide)) {
                if (remainingEnergy <= 0L) {
                    break;
                }

                long extracted;
                if (targetStorage instanceof IEnergyStorage energyStorage) {
                    int toExtract = (int) Math.min(remainingEnergy, Integer.MAX_VALUE);
                    extracted = energyStorage.extractEnergy(toExtract, simulate);
                } else {
                    extracted = FluxNetworkCompat.extractEnergy(targetStorage, remainingEnergy, simulate);
                }

                if (extracted <= 0L) {
                    continue;
                }

                remainingEnergy -= extracted;
                extractedEnergy += extracted;
            }

            return extractedEnergy;
        }
    }
}
